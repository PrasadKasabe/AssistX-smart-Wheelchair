package com.smartwheelchair.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class WheelchairColors(
    val background: Color,
    val surface: Color,
    val border: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val accentButton: Color,
    val accentButtonContent: Color,
    val accentTintBg: Color,
    val accentText: Color,
    val stopButton: Color,
    val stopButtonContent: Color,
    val tabContainer: Color,
    val tabSelected: Color,
    val darkBtnBg: Color,
    val darkBtnContent: Color,
    val connectedBg: Color,
    val connectedText: Color,
    val offlineBg: Color,
    val offlineText: Color
)

val LocalWheelchairColors = staticCompositionLocalOf {
    WheelchairColors(
        background = LightBackground,
        surface = LightSurface,
        border = LightBorder,
        textPrimary = LightTextPrimary,
        textMuted = LightTextMuted,
        accentButton = LightAccentButton,
        accentButtonContent = Color.White,
        accentTintBg = LightAccentTintBg,
        accentText = LightAccentText,
        stopButton = LightStopButton,
        stopButtonContent = Color.White,
        tabContainer = LightTabContainer,
        tabSelected = Color.White,
        darkBtnBg = LightDarkBtnBg,
        darkBtnContent = Color.White,
        connectedBg = ConnectedBgLight,
        connectedText = ConnectedTextLight,
        offlineBg = OfflineBgLight,
        offlineText = OfflineTextLight
    )
}

private val LightColorScheme = lightColorScheme(
    primary = LightAccentButton,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccentButton,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkAccentButtonContent,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

@Composable
fun WheelchairTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val wheelchairColors = if (darkTheme) {
        WheelchairColors(
            background = DarkBackground,
            surface = DarkSurface,
            border = DarkBorder,
            textPrimary = DarkTextPrimary,
            textMuted = DarkTextMuted,
            accentButton = DarkAccentButton,
            accentButtonContent = DarkAccentButtonContent,
            accentTintBg = DarkAccentTintBg,
            accentText = DarkAccentText,
            stopButton = DarkStopButton,
            stopButtonContent = Color.White,
            tabContainer = DarkTabContainer,
            tabSelected = DarkTabSelected,
            darkBtnBg = DarkDarkBtnBg,
            darkBtnContent = DarkDarkBtnContent,
            connectedBg = ConnectedBgDark,
            connectedText = ConnectedTextDark,
            offlineBg = OfflineBgDark,
            offlineText = OfflineTextDark
        )
    } else {
        WheelchairColors(
            background = LightBackground,
            surface = LightSurface,
            border = LightBorder,
            textPrimary = LightTextPrimary,
            textMuted = LightTextMuted,
            accentButton = LightAccentButton,
            accentButtonContent = Color.White,
            accentTintBg = LightAccentTintBg,
            accentText = LightAccentText,
            stopButton = LightStopButton,
            stopButtonContent = Color.White,
            tabContainer = LightTabContainer,
            tabSelected = Color.White,
            darkBtnBg = LightDarkBtnBg,
            darkBtnContent = Color.White,
            connectedBg = ConnectedBgLight,
            connectedText = ConnectedTextLight,
            offlineBg = OfflineBgLight,
            offlineText = OfflineTextLight
        )
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalWheelchairColors provides wheelchairColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object WheelchairTheme {
    val colors: WheelchairColors
        @Composable
        get() = LocalWheelchairColors.current
}
