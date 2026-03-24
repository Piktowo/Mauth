package com.xinto.mauth.ui.screen.account.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
fun AccountExitDialog(
    show: MutableState<Boolean>,
    onConfirm: () -> Unit,
) {
    SuperDialog(
        show = show,
        title = stringResource(R.string.account_discard_title),
        summary = stringResource(R.string.account_discard_subtitle),
        onDismissRequest = { show.value = false },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.compact),
        ) {
            Button(
                onClick = { show.value = false },
                modifier = Modifier.weight(1f),
                cornerRadius = MauthUiTokens.Radius.button,
            ) {
                Text(stringResource(R.string.account_discard_buttons_cancel))
            }
            Button(
                onClick = {
                    show.value = false
                    onConfirm()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColorsPrimary(),
                cornerRadius = MauthUiTokens.Radius.button,
            ) {
                Text(
                    text = stringResource(R.string.account_discard_buttons_discard),
                    color = MiuixTheme.colorScheme.background,
                )
            }
        }
    }
}