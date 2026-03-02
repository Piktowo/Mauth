package com.xinto.mauth.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.xinto.mauth.core.settings.model.ColorSetting
import com.xinto.mauth.core.settings.model.ThemeSetting
import com.xinto.mauth.ui.theme.color.BlueberryBlueDark
import com.xinto.mauth.ui.theme.color.BlueberryBlueLight
import com.xinto.mauth.ui.theme.color.LemonYellowDark
import com.xinto.mauth.ui.theme.color.LemonYellowLight
import com.xinto.mauth.ui.theme.color.LimeGreenDark
import com.xinto.mauth.ui.theme.color.LimeGreenLight
import com.xinto.mauth.ui.theme.color.MothPurpleDark
import com.xinto.mauth.ui.theme.color.MothPurpleLight
import com.xinto.mauth.ui.theme.color.OrangeOrangeDark
import com.xinto.mauth.ui.theme.color.OrangeOrangeLight
import com.xinto.mauth.ui.theme.color.SkyCyanDark
import com.xinto.mauth.ui.theme.color.SkyCyanLight

private val MauthShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

private val StaticColorSchemes: Map<ColorSetting, Pair<ColorScheme, ColorScheme>> = mapOf(
    ColorSetting.MothPurple to (MothPurpleLight to MothPurpleDark),
    ColorSetting.BlueberryBlue to (BlueberryBlueLight to BlueberryBlueDark),
    ColorSetting.PickleYellow to (LemonYellowLight to LemonYellowDark),
    ColorSetting.ToxicGreen to (LimeGreenLight to LimeGreenDark),
    ColorSetting.LeatherOrange to (OrangeOrangeLight to OrangeOrangeDark),
    ColorSetting.OceanTurquoise to (SkyCyanLight to SkyCyanDark),
)

@Composable
fun MauthTheme(
    theme: ThemeSetting = ThemeSetting.DEFAULT,
    color: ColorSetting = ColorSetting.DEFAULT,
    content: @Composable () -> Unit
) {
    val isDark = when (theme) {
        ThemeSetting.System -> isSystemInDarkTheme()
        ThemeSetting.Dark -> true
        ThemeSetting.Light -> false
    }
    val isInPreview = LocalInspectionMode.current
    val colorScheme = if (color == ColorSetting.Dynamic &&
        (isInPreview || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    ) {
        val context = LocalContext.current
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        val schemes = StaticColorSchemes[color] ?: (MothPurpleLight to MothPurpleDark)
        if (isDark) schemes.second else schemes.first
    }
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = MauthShapes,
        typography = Typography,
        content = content
    )
}