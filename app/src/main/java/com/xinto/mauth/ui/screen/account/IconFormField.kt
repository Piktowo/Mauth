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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
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
            }
            if (issuerProvider != null) {
                IconButton(
                    onClick = {
                        val issuer = issuerProvider().trim()
                        if (issuer.isEmpty()) return@IconButton
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
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = context.getString(R.string.account_icon_fetch)
                    )
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
            val candidates = listOf(
                "https://logo.clearbit.com/${issuer.lowercase()}.com",
                "https://logo.clearbit.com/${issuer.lowercase()}",
                "https://icons.bitwarden.net/${issuer.lowercase()}.com/icon.png",
            )
            for (urlString in candidates) {
                try {
                    val connection = URL(urlString).openConnection() as HttpURLConnection
                    connection.connectTimeout = 10_000
                    connection.readTimeout = 10_000
                    connection.connect()
                    if (connection.responseCode !in 200..299) {
                        connection.disconnect()
                        continue
                    }
                    val bitmap = BitmapFactory.decodeStream(connection.inputStream)
                    connection.disconnect()
                    if (bitmap == null) continue

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
                    // try next candidate
                }
            }
            null
        }
}
