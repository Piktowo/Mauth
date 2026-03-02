package com.xinto.mauth.ui.screen.theme

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xinto.mauth.R
import com.xinto.mauth.core.settings.model.ThemeSetting
import com.xinto.mauth.ui.component.MauthCard
import com.xinto.mauth.ui.component.MauthScreenColumn
import com.xinto.mauth.ui.component.MauthSmallTitle
import com.xinto.mauth.ui.component.MauthTopBar
import com.xinto.mauth.ui.theme.colorFromArgb
import com.xinto.mauth.ui.theme.colorToArgbLong
import com.xinto.mauth.ui.theme.isDefaultThemeSeedArgb
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.ColorPicker
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.WindowBottomSheet
import top.yukonga.miuix.kmp.extra.WindowDropdown
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ThemeScreen(onExit: () -> Unit) {
    BackHandler(onBack = onExit)
    val viewModel: ThemeViewModel = koinViewModel()
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val themeSeedColor by viewModel.themeSeedColor.collectAsStateWithLifecycle()
    val scrollBehavior = MiuixScrollBehavior()
    var editingColor by remember(themeSeedColor) {
        mutableStateOf(runCatching { colorFromArgb(themeSeedColor) }.getOrDefault(Color.White))
    }
    var editingHex by remember(themeSeedColor) {
        mutableStateOf(themeHexFromArgb(themeSeedColor))
    }
    val showColorPicker = remember { mutableStateOf(false) }
    val themeItems = listOf(
        stringResource(R.string.theme_theme_system),
        stringResource(R.string.theme_theme_light),
        stringResource(R.string.theme_theme_dark),
    )
    val themeValues = ThemeSetting.entries
    Scaffold(
        topBar = {
            MauthTopBar(
                title = stringResource(R.string.theme_title),
                scrollBehavior = scrollBehavior,
                onBack = onExit,
            )
        }
    ) { innerPadding ->
        MauthScreenColumn(scrollBehavior = scrollBehavior, innerPadding = innerPadding) {
            item {
                MauthSmallTitle(
                    text = stringResource(R.string.theme_category_mode),
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 4.dp),
                )
                MauthCard {
                    WindowDropdown(
                        title = stringResource(R.string.theme_prefs_mode),
                        items = themeItems,
                        selectedIndex = themeValues.indexOf(theme).coerceAtLeast(0),
                        onSelectedIndexChange = { index ->
                            if (index in themeValues.indices) viewModel.updateTheme(themeValues[index])
                        },
                    )
                }
                MauthSmallTitle(
                    text = stringResource(R.string.theme_category_color),
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp),
                )
                MauthCard {
                    BasicComponent(
                        title = stringResource(R.string.theme_prefs_color),
                        summary = if (isDefaultThemeSeedArgb(themeSeedColor)) {
                            stringResource(R.string.theme_color_default)
                        } else {
                            themeHexFromArgb(themeSeedColor)
                        },
                        onClick = {
                            editingColor = runCatching { colorFromArgb(themeSeedColor) }
                                .getOrDefault(Color.White)
                            editingHex = themeHexFromArgb(themeSeedColor)
                            showColorPicker.value = true
                        },
                        endActions = {
                            val previewColor = remember(themeSeedColor) {
                                runCatching { colorFromArgb(themeSeedColor) }.getOrDefault(Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .width(48.dp).height(26.dp)
                                    .background(color = previewColor, shape = RoundedCornerShape(50))
                            )
                        },
                    )
                }
            }
        }
    }
    WindowBottomSheet(
        show = showColorPicker,
        title = stringResource(R.string.theme_color_picker_title),
        onDismissRequest = { showColorPicker.value = false },
        insideMargin = DpSize(24.dp, 16.dp),
    ) {
        ColorPicker(
            color = editingColor,
            onColorChanged = { newColor ->
                editingColor = newColor
                editingHex = themeHexFromArgb(colorToArgbLong(newColor))
            },
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = editingHex,
            onValueChange = { raw ->
                val normalized = normalizeThemeHex(raw)
                editingHex = normalized
                parseThemeHex(normalized)?.let { editingColor = it }
            },
            label = stringResource(R.string.theme_color_hex_label),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { viewModel.resetThemeSeedColor() }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.theme_color_reset))
            }
            Button(
                onClick = {
                    val argb = colorToArgbLong(editingColor)
                    if (isDefaultThemeSeedArgb(argb)) viewModel.resetThemeSeedColor()
                    else viewModel.updateThemeSeedColor(argb)
                    showColorPicker.value = false
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColorsPrimary(),
            ) {
                Text(text = stringResource(R.string.theme_color_apply), color = MiuixTheme.colorScheme.background)
            }
        }
    }
}

private fun themeHexFromArgb(argb: Long): String {
    val rgb = (argb and 0x00FFFFFFL).toString(16).uppercase().padStart(6, '0')
    return "#$rgb"
}

private fun normalizeThemeHex(input: String): String {
    val body = input.uppercase().filter { it in '0'..'9' || it in 'A'..'F' }.take(6)
    return "#$body"
}

private fun parseThemeHex(input: String): Color? {
    val body = input.removePrefix("#")
    if (body.length != 6) return null
    val rgb = body.toLongOrNull(16) ?: return null
    return colorFromArgb(0xFF000000L or rgb)
}

