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
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun put(url: String, username: String, password: String, data: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val fileUrl = ensureFileUrl(url)
                val body = data.toRequestBody("text/plain; charset=utf-8".toMediaType())
                val requestBuilder = Request.Builder()
                    .url(fileUrl)
                    .put(body)
                    .header("User-Agent", USER_AGENT)
                if (username.isNotEmpty()) {
                    requestBuilder.header("Authorization", Credentials.basic(username, password))
                }
                client.newCall(requestBuilder.build()).execute().use { response ->
                    if (!response.isSuccessful) {
                        error("HTTP ${response.code} ${response.message}")
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
                    if (!response.isSuccessful) {
                        error("HTTP ${response.code} ${response.message}")
                    }
                    response.body?.string() ?: ""
                }
            }
        }

    companion object {
        private const val USER_AGENT = "Mauth/1.0"
        private const val DEFAULT_BACKUP_FILE = "mauth_backup.txt"

        fun ensureFileUrl(url: String): String {
            val trimmed = url.trim()
            val path = URL(trimmed).path
            val lastSegment = path.trimEnd('/').substringAfterLast('/')
            return if (lastSegment.contains('.')) {
                trimmed
            } else {
                val base = if (trimmed.endsWith("/")) trimmed else "$trimmed/"
                "$base$DEFAULT_BACKUP_FILE"
            }
        }
    }
}
