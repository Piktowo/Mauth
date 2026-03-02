package com.xinto.mauth.ui.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xinto.mauth.R
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeDeleteAccountsDialog(
    show: MutableState<Boolean>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    SuperDialog(
        show = show,
        title = stringResource(R.string.home_delete_title),
        summary = stringResource(R.string.home_delete_subtitle),
        onDismissRequest = {
            show.value = false
            onCancel()
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = {
                    show.value = false
                    onCancel()
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.home_delete_button_cancel))
            }
            Button(
                onClick = {
                    show.value = false
                    onConfirm()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColorsPrimary(),
            ) {
                Text(
                    text = stringResource(R.string.home_delete_button_delete),
                    color = MiuixTheme.colorScheme.background,
                )
            }
        }
    }
}
