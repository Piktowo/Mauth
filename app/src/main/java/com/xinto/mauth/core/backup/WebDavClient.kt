package com.xinto.mauth.core.backup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import java.util.concurrent.TimeUnit

class WebDavClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        // WebDAV MUST NOT auto-follow redirects: OkHttp downgrades PUT/MKCOL to GET
        // on 301/302, which silently turns a backup into a read and returns 404.
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    suspend fun put(url: String, username: String, password: String, data: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val fileUrl = ensureFileUrl(url)
                // Try to create the parent directory via MKCOL before uploading.
                // This is required when the target folder does not exist yet
                // (e.g. Jianguoyun sub-folders). Failures are silently ignored.
                val dirUrl = fileUrl.substringBeforeLast('/') + "/"
                tryMkcol(dirUrl, username, password)
                val body = data.toRequestBody("text/plain; charset=utf-8".toMediaType())
                val requestBuilder = Request.Builder()
                    .url(fileUrl)
                    .put(body)
                    .header("User-Agent", USER_AGENT)
                if (username.isNotEmpty()) {
                    requestBuilder.header("Authorization", Credentials.basic(username, password))
                }
                client.newCall(requestBuilder.build()).execute().use { response ->
                    if (response.isRedirect) {
                        val location = response.header("Location") ?: "(unknown)"
                        error("HTTP ${response.code}: Server redirected to $location. Please use the final URL directly.")
                    }
                    if (!response.isSuccessful) {
                        error("HTTP ${response.code} ${response.message} (url=$fileUrl)")
                    }
                }
            }
        }

    suspend fun get(url: String, username: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val fileUrl = ensureFileUrl(url)
                val requestBuilder = Request.Builder()
                    .url(fileUrl)
                    .get()
                    .header("User-Agent", USER_AGENT)
                if (username.isNotEmpty()) {
                    requestBuilder.header("Authorization", Credentials.basic(username, password))
                }
                client.newCall(requestBuilder.build()).execute().use { response ->
                    if (response.isRedirect) {
                        val location = response.header("Location") ?: "(unknown)"
                        error("HTTP ${response.code}: Server redirected to $location. Please use the final URL directly.")
                    }
                    if (!response.isSuccessful) {
                        if (response.code == 404) {
                            error("HTTP 404: Backup file not found on server (url=$fileUrl). Please perform a backup first.")
                        }
                        error("HTTP ${response.code} ${response.message} (url=$fileUrl)")
                    }
                    response.body?.string() ?: ""
                }
            }
        }

    suspend fun testConnection(url: String, username: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val baseUrl = url.trim().let { if (it.endsWith("/")) it else "$it/" }
                // PROPFIND depth=0 is the standard WebDAV way to verify both URL and credentials
                // without modifying any data.
                val body = """<?xml version="1.0" encoding="UTF-8"?><propfind xmlns="DAV:"><prop><resourcetype/></prop></propfind>"""
                    .toRequestBody("application/xml".toMediaType())
                val requestBuilder = Request.Builder()
                    .url(baseUrl)
                    .method("PROPFIND", body)
                    .header("User-Agent", USER_AGENT)
                    .header("Depth", "0")
                if (username.isNotEmpty()) {
                    requestBuilder.header("Authorization", Credentials.basic(username, password))
                }
                client.newCall(requestBuilder.build()).execute().use { response ->
                    if (response.isRedirect) {
                        val location = response.header("Location") ?: "(unknown)"
                        error("HTTP ${response.code}: Server redirected to $location. Please use the final URL directly.")
                    }
                    if (!response.isSuccessful) {
                        error("HTTP ${response.code} ${response.message}")
                    }
                }
            }
        }

    /**
     * Silently attempts to create a WebDAV collection (directory) at [dirUrl].
     * A 405 response means the collection already exists, which is fine.
     * All errors are ignored so they do not affect the main operation.
     */
    private fun tryMkcol(dirUrl: String, username: String, password: String) {
        runCatching {
            val requestBuilder = Request.Builder()
                .url(dirUrl)
                .method("MKCOL", null)
                .header("User-Agent", USER_AGENT)
            if (username.isNotEmpty()) {
                requestBuilder.header("Authorization", Credentials.basic(username, password))
            }
            client.newCall(requestBuilder.build()).execute().close()
        }
    }

    companion object {
        private const val USER_AGENT = "Mauth/1.0"
        private const val DEFAULT_BACKUP_DIR = "Mauth"
        private const val DEFAULT_BACKUP_FILE = "mauth_backup.txt"

        fun ensureFileUrl(url: String): String {
            val trimmed = url.trim()
            val path = URL(trimmed).path
            val lastSegment = path.trimEnd('/').substringAfterLast('/')
            return if (lastSegment.contains('.')) {
                // Explicit file path provided by user, use as-is
                trimmed
            } else {
                // Directory URL: append subfolder + filename.
                // Jianguoyun (and many WebDAV servers) reject PUT directly in
                // the DAV root (/dav/), so we always write into a subdirectory.
                val base = if (trimmed.endsWith("/")) trimmed else "$trimmed/"
                "$base$DEFAULT_BACKUP_DIR/$DEFAULT_BACKUP_FILE"
            }
        }
    }
}
