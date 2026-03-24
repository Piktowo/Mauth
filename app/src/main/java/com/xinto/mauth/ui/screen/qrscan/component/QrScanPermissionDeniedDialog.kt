package com.xinto.mauth.ui.screen.qrscan.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.xinto.mauth.R
import com.xinto.mauth.ui.theme.MauthUiTokens
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun QrScanPermissionDeniedDialog(
    shouldShowRationale: Boolean,
    onGrantPermission: () -> Unit,
    onCancel: () -> Unit,
) {
    val show = remember { mutableStateOf(true) }
    SuperDialog(
        show = show,
        title = stringResource(R.string.qrscan_permissions_title),
        summary = if (shouldShowRationale) {
            stringResource(R.string.qrscan_permissions_subtitle_rationale)
        } else {
            stringResource(R.string.qrscan_permissions_subtitle)
        },
        onDismissRequest = {
            show.value = false
            onCancel()
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.compact),
        ) {
            Button(
                onClick = {
                    show.value = false
                    onCancel()
                },
                modifier = Modifier.weight(1f),
                cornerRadius = MauthUiTokens.Radius.button,
            ) {
                Text(stringResource(R.string.qrscan_permissions_button_cancel))
            }
            Button(
                onClick = {
                    show.value = false
                    onGrantPermission()
                },
                modifier = Modifier.weight(1f),
                cornerRadius = MauthUiTokens.Radius.button,
                colors = ButtonDefaults.buttonColorsPrimary(),
            ) {
                Text(
                    text = stringResource(R.string.qrscan_permissions_button_grant),
                    color = MiuixTheme.colorScheme.background,
                )
            }
        }
    }
}