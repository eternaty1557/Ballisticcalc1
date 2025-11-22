package com.example.ballisticcalc.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),      // тактический зелёный
    background = Color(0xFFEEC79E),    // светло-серый фон (не белый — чтобы не слепить)
    surface = Color(0xFFEEEEEE),       // чуть светлее фона — для карточек
    onBackground = Color(0xFF263238),  // тёмно-серый — отлично читается на светлом фоне
    onSurface = Color(0xFF1B263B)      // почти чёрный — для заголовков
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),
    background = Color(0xFF0D1B2A),    // тёмно-синий (не чёрный — лучше видимость)
    surface = Color(0xFF1B263B),
    onBackground = Color(0xFF81D4FA),  // ледяной голубой — отлично читается
    onSurface = Color(0xFFE0E0E0)      // светло-серый — не белый, чтобы не слепить
)

@Composable
fun BallisticCalcTheme(
    nightMode: Boolean,
    content: @Composable () -> Unit
) {
    val colors = if (nightMode) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !nightMode
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}