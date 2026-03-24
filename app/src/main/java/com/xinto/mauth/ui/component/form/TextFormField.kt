package com.xinto.mauth.ui.component.form

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xinto.mauth.R
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

class TextFormField(
    initial: String,

    @param:StringRes
    private val label: Int,

    @param:DrawableRes
    private val icon: Int = 0,
    private val required: Boolean = false
) : FormField<String>(initial, id = label) {

    @Composable
    override fun invoke(modifier: Modifier) {
        Column(modifier = modifier) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = { value = it },
                label = stringResource(label),
                leadingIcon = if (icon == 0) null else {
                    {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null
                        )
                    }
                },
                maxLines = 1,
                cornerRadius = 12.dp,
            )
            if (required) {
                Text(
                    text = stringResource(R.string.account_data_status_required),
                    modifier = Modifier.padding(start = 12.dp, top = 6.dp),
                    style = MiuixTheme.textStyles.footnote1,
                    color = if (error) MiuixTheme.colorScheme.error else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }
    }

    override fun isValid(): Boolean {
        if (!required) return true

        return value.isNotEmpty()
    }


}