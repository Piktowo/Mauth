package com.xinto.mauth.ui.component.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.xinto.mauth.R
import com.xinto.mauth.ui.theme.MauthUiTokens
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

class IntFormField(
    initial: Int,

    @StringRes
    private val label: Int,

    private val min: Int = Int.MIN_VALUE,
    private val max: Int = Int.MAX_VALUE
) : FormField<String>(initial.toString(), id = label) {

    @Composable
    override fun invoke(modifier: Modifier) {
        Column(modifier = modifier) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = { value = it },
                label = stringResource(label),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLines = 1,
                cornerRadius = MauthUiTokens.Radius.input,
            )
            if (max != Int.MAX_VALUE) {
                Text(
                    text = stringResource(R.string.account_data_status_range, min.toString(), max.toString()),
                    modifier = Modifier.padding(
                        start = MauthUiTokens.Space.helperStart,
                        top = MauthUiTokens.Space.helperTop,
                    ),
                    style = MiuixTheme.textStyles.footnote1,
                    color = if (error) MiuixTheme.colorScheme.error else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }
    }

    override fun isValid(): Boolean {
        val intValue = value.toIntOrNull() ?: return false

        return intValue in min..max
    }

}