package com.xinto.mauth.ui.screen.account

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.xinto.mauth.R
import com.xinto.mauth.ui.component.UriImage
import com.xinto.mauth.ui.component.form.FormField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class IconFormField(
    initial: Uri?,
    private val issuerProvider: (() -> String)? = null,
) : FormField<Uri?>(initial, 0) {

    @Composable
    override fun invoke(modifier: Modifier) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val imageSelectLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {
                value = makePermanentUri(context, it)
            }
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(96.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
                onClick = {
                    imageSelectLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            ) {
                if (value != null) {
                    UriImage(uri = value!!)
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            modifier = Modifier.size(36.dp),
                            painter = painterResource(R.drawable.ic_add_a_photo),
                            contentDescription = null
                        )
                    }
                }
            }
            if (issuerProvider != null) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 4.dp,
                        onClick = {
                            val issuer = issuerProvider().trim()
                            if (issuer.isEmpty()) return@Surface
                            scope.launch {
                                val uri = fetchIconForIssuer(context, issuer)
                                if (uri != null) {
                                    value = uri
                                    Toast.makeText(context, R.string.account_icon_fetch_success, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, R.string.account_icon_fetch_fail, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = context.getString(R.string.account_icon_fetch),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }

    private fun makePermanentUri(context: Context, uri: Uri?): Uri? {
        if (uri == null) return null

        try {
            val contentResolver = context.contentResolver
            val bitmap = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, false)
            }

            val destination = File(context.filesDir, "${id}_${UUID.randomUUID()}.png").apply {
                if (exists()) {
                    delete()
                }
                createNewFile()
            }
            destination.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            value?.toFile()?.delete()

            return destination.toUri()
        } catch (e: Exception) {
            return null
        }
    }

    private suspend fun fetchIconForIssuer(context: Context, issuer: String): Uri? =
        withContext(Dispatchers.IO) {
            val domain = issuer.lowercase()
            val candidates = listOf(
                // Google 高清 favicon（128px），提供官方品牌图标
                "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=https://${domain}.com&size=128",
                // Clearbit logo API — 提供矢量转换的高清品牌 logo
                "https://logo.clearbit.com/${domain}.com",
                // DuckDuckGo favicon（高质量备用）
                "https://icons.duckduckgo.com/ip3/${domain}.com.ico",
                // Bitwarden 图标服务（最后备选）
                "https://icons.bitwarden.net/${domain}.com/icon.png",
            )
            for (urlString in candidates) {
                try {
                    val connection = URL(urlString).openConnection() as HttpURLConnection
                    connection.connectTimeout = 10_000
                    connection.readTimeout = 10_000
                    connection.instanceFollowRedirects = true
                    connection.connect()
                    if (connection.responseCode !in 200..299) {
                        connection.disconnect()
                        continue
                    }
                    val bitmap = BitmapFactory.decodeStream(connection.inputStream)
                    connection.disconnect()
                    // 过滤掉分辨率太低的图标（宽或高小于 32px 的通常是占位图）
                    if (bitmap == null || (bitmap.width < 32 || bitmap.height < 32)) continue

                    val destination = File(context.filesDir, "${id}_${UUID.randomUUID()}.png").apply {
                        if (exists()) delete()
                        createNewFile()
                    }
                    destination.outputStream().use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    value?.toFile()?.delete()
                    return@withContext destination.toUri()
                } catch (_: Exception) {
                    // 尝试下一个候选 URL
                }
            }
            null
        }
}
