package com.xinto.mauth.ui.component.form

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import top.yukonga.miuix.kmp.extra.WindowDropdown

class ComboBoxFormField<E: Enum<E>>(
    initial: E,

    @param:StringRes
    private val label: Int
) : FormField<E>(initial, id = label) {

    private val clazz = initial.declaringJavaClass

    @Composable
    override fun invoke(modifier: Modifier) {
        val items = clazz.enumConstants!!.map { it.name }
        val selectedIndex = clazz.enumConstants!!.indexOfFirst { it == value }.coerceAtLeast(0)
        WindowDropdown(
            title = stringResource(label),
            items = items,
            selectedIndex = selectedIndex,
            onSelectedIndexChange = { index ->
                clazz.enumConstants!!.getOrNull(index)?.let { value = it }
            },
        )
    }
}