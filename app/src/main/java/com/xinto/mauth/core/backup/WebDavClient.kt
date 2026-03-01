package com.xinto.mauth.core.backup

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class WebDavClient {

    suspend fun put(url: String, username: String, password: String, data: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "text/plain; charset=utf-8")
                    if (username.isNotEmpty()) {
                        setRequestProperty("Authorization", basicAuth(username, password))
                    }
                    connectTimeout = 15_000
                    readTimeout = 15_000
                }
                connection.outputStream.use { it.write(data.toByteArray(Charsets.UTF_8)) }
                val code = connection.responseCode
                connection.disconnect()
                if (code !in 200..299) {
                    error("HTTP $code")
                }
            }
        }

    suspend fun get(url: String, username: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    if (username.isNotEmpty()) {
                        setRequestProperty("Authorization", basicAuth(username, password))
                    }
                    connectTimeout = 15_000
                    readTimeout = 15_000
                }
                val code = connection.responseCode
                if (code !in 200..299) {
                    connection.disconnect()
                    error("HTTP $code")
                }
                val body = connection.inputStream.use { it.readBytes().toString(Charsets.UTF_8) }
                connection.disconnect()
                body
            }
        }

    private fun basicAuth(username: String, password: String): String {
        val credentials = "$username:$password"
        return "Basic " + Base64.encodeToString(credentials.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }
}
