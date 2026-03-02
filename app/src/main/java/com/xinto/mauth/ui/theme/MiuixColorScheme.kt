package com.xinto.mauth.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

const val DEFAULT_THEME_SEED_ARGB: Long = 0xFFFFFFFFL

private data class ThemeColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryVariant: Color,
    val onPrimaryVariant: Color,
    val disabledPrimary: Color,
    val disabledOnPrimary: Color,
    val disabledPrimaryButton: Color,
    val disabledOnPrimaryButton: Color,
    val disabledPrimarySlider: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val tertiaryContainerVariant: Color,
    val onBackgroundVariant: Color,
)

private data class ThemePalette(val light: ThemeColors, val dark: ThemeColors)

private val monochromePalette = ThemePalette(
    light = ThemeColors(
        primary = Color(0xFF000000),
        onPrimary = Color.White,
        primaryVariant = Color(0xFF222222),
        onPrimaryVariant = Color(0xFFAAAAAA),
        disabledPrimary = Color(0xFFBDBDBD),
        disabledOnPrimary = Color(0xFFE0E0E0),
        disabledPrimaryButton = Color(0xFFBDBDBD),
        disabledOnPrimaryButton = Color(0xFFEEEEEE),
        disabledPrimarySlider = Color(0xFFDCDCDC),
        primaryContainer = Color(0xFFF0F0F0),
        onPrimaryContainer = Color(0xFF000000),
        tertiaryContainer = Color(0xFFF8F8F8),
        onTertiaryContainer = Color(0xFF000000),
        tertiaryContainerVariant = Color(0xFFF8F8F8),
        onBackgroundVariant = Color(0xFF000000),
    ),
    dark = ThemeColors(
        primary = Color.White,
        onPrimary = Color(0xFF000000),
        primaryVariant = Color(0xFFE0E0E0),
        onPrimaryVariant = Color(0xFF555555),
        disabledPrimary = Color(0xFF333333),
        disabledOnPrimary = Color(0xFF757575),
        disabledPrimaryButton = Color(0xFF333333),
        disabledOnPrimaryButton = Color(0xFF757575),
        disabledPrimarySlider = Color(0xFF444444),
        primaryContainer = Color(0xFF252525),
        onPrimaryContainer = Color.White,
        tertiaryContainer = Color(0xFF1C1C1C),
        onTertiaryContainer = Color.White,
        tertiaryContainerVariant = Color(0xFF303030),
        onBackgroundVariant = Color(0xFFE0E0E0),
    ),
)

fun isDefaultThemeSeedArgb(argb: Long): Boolean {
    val rgb = argb and 0x00FFFFFFL
    return rgb == 0x000000L || rgb == 0xFFFFFFL
}

fun colorFromArgb(argb: Long): Color = Color(argb.toInt())

fun colorToArgbLong(color: Color): Long = color.toArgb().toLong() and 0xFFFFFFFFL

fun buildMiuixColorScheme(seed: Long?, isDark: Boolean) =
    if (seed == null || isDefaultThemeSeedArgb(seed)) {
        monochromePalette.toColorScheme(isDark)
    } else {
        derivePaletteFromSeed(colorFromArgb(seed)).toColorScheme(isDark)
    }

private fun ThemePalette.toColorScheme(isDark: Boolean) =
    if (isDark) dark.toDarkScheme() else light.toLightScheme()

private fun ThemeColors.toLightScheme() = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryVariant = primaryVariant,
    onPrimaryVariant = onPrimaryVariant,
    disabledPrimary = disabledPrimary,
    disabledOnPrimary = disabledOnPrimary,
    disabledPrimaryButton = disabledPrimaryButton,
    disabledOnPrimaryButton = disabledOnPrimaryButton,
    disabledPrimarySlider = disabledPrimarySlider,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    tertiaryContainerVariant = tertiaryContainerVariant,
    onBackgroundVariant = onBackgroundVariant,
)

private fun ThemeColors.toDarkScheme() = darkColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryVariant = primaryVariant,
    onPrimaryVariant = onPrimaryVariant,
    disabledPrimary = disabledPrimary,
    disabledOnPrimary = disabledOnPrimary,
    disabledPrimaryButton = disabledPrimaryButton,
    disabledOnPrimaryButton = disabledOnPrimaryButton,
    disabledPrimarySlider = disabledPrimarySlider,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    tertiaryContainerVariant = tertiaryContainerVariant,
    onBackgroundVariant = onBackgroundVariant,
)

private fun derivePaletteFromSeed(seed: Color) = ThemePalette(
    light = deriveThemeColors(monochromePalette.light, seed, dark = false),
    dark = deriveThemeColors(monochromePalette.dark, seed, dark = true),
)

private fun deriveThemeColors(base: ThemeColors, seed: Color, dark: Boolean): ThemeColors {
    val primary = if (dark) seed.mix(Color.White, 0.20f) else seed.mix(Color.Black, 0.05f)
    val primaryVariant = if (dark) seed.mix(Color.White, 0.36f) else seed.mix(Color.White, 0.25f)
    val onPrimary = Color(0xFFFDFDFD)
    val onPrimaryVariant = if (dark) Color(0xFFF2F2F2) else seed.mix(Color.Black, 0.72f)
    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryVariant = primaryVariant,
        onPrimaryVariant = onPrimaryVariant,
        disabledPrimary = base.disabledPrimary.mix(seed, if (dark) 0.18f else 0.14f),
        disabledOnPrimary = base.disabledOnPrimary.mix(seed, if (dark) 0.08f else 0.06f),
        disabledPrimaryButton = base.disabledPrimaryButton.mix(seed, if (dark) 0.16f else 0.12f),
        disabledOnPrimaryButton = base.disabledOnPrimaryButton.mix(seed, if (dark) 0.06f else 0.05f),
        disabledPrimarySlider = base.disabledPrimarySlider.mix(seed, if (dark) 0.14f else 0.10f),
        primaryContainer = base.primaryContainer.mix(seed, if (dark) 0.18f else 0.14f),
        onPrimaryContainer = if (dark) Color(0xFFF2F2F2) else seed.mix(Color.Black, 0.80f),
        tertiaryContainer = base.tertiaryContainer.mix(seed, if (dark) 0.13f else 0.16f),
        onTertiaryContainer = if (dark) seed.mix(Color.White, 0.22f) else seed.mix(Color.Black, 0.30f),
        tertiaryContainerVariant = base.tertiaryContainerVariant.mix(seed, if (dark) 0.15f else 0.13f),
        onBackgroundVariant = if (dark) seed.mix(Color.White, 0.25f) else seed.mix(Color.Black, 0.18f),
    )
}

private fun Color.mix(other: Color, ratio: Float): Color {
    val t = ratio.coerceIn(0f, 1f)
    return Color(
        red = red + (other.red - red) * t,
        green = green + (other.green - green) * t,
        blue = blue + (other.blue - blue) * t,
        alpha = alpha + (other.alpha - alpha) * t,
    )
}

@Suppress("unused")
private fun Color.autoOnColor(): Color = if (luminance() > 0.52f) Color.Black else Color.White
