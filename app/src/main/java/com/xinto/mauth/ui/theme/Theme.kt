package com.xinto.mauth.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xinto.mauth.core.settings.model.ThemeSetting
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MauthTheme(
    theme: ThemeSetting = ThemeSetting.DEFAULT,
    themeSeedColor: Long = DEFAULT_THEME_SEED_ARGB,
    content: @Composable () -> Unit
) {
    val isDark = when (theme) {
        ThemeSetting.System -> isSystemInDarkTheme()
        ThemeSetting.Dark -> true
        ThemeSetting.Light -> false
    }
    val colors = remember(isDark, themeSeedColor) {
        buildMiuixColorScheme(
            seed = if (isDefaultThemeSeedArgb(themeSeedColor)) null else themeSeedColor,
            isDark = isDark,
        )
    }
    MiuixTheme(colors = colors, content = content)
}
