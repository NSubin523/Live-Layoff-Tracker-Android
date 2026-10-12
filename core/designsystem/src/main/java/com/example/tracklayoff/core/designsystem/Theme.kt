package com.example.tracklayoff.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A237E), onPrimary = Color.White,
    primaryContainer = Color(0xFFE4E7FF), onPrimaryContainer = Color(0xFF10165C),
    secondary = Color(0xFF565D78), onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3E7F4), onSecondaryContainer = Color(0xFF20263D),
    tertiary = Color(0xFF0D47A1), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD9E6FF), onTertiaryContainer = Color(0xFF082D66),
    background = Color(0xFFF5F5F7), onBackground = Color(0xFF1B1B24),
    surface = Color(0xFFFCFCFF), onSurface = Color(0xFF1B1B24),
    surfaceVariant = Color(0xFFE9EAF2), onSurfaceVariant = Color(0xFF575967),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFF7F7FC),
    surfaceContainer = Color(0xFFF0F0F7), surfaceContainerHigh = Color(0xFFEAEAF2),
    surfaceContainerHighest = Color(0xFFE4E4ED),
    outline = Color(0xFF777986), outlineVariant = Color(0xFFDCDDE7),
    error = Color(0xFFB3261E), onError = Color.White,
    errorContainer = Color(0xFFFCE1DE), onErrorContainer = Color(0xFF7A1711),
    inverseSurface = Color(0xFF30313C), inverseOnSurface = Color(0xFFF3F3FA),
    inversePrimary = Color(0xFFBBC3FF), surfaceTint = Color(0xFF1A237E)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBBC3FF), onPrimary = Color(0xFF17216D),
    primaryContainer = Color(0xFF303B88), onPrimaryContainer = Color(0xFFE0E4FF),
    secondary = Color(0xFFBEC5E0), onSecondary = Color(0xFF293049),
    secondaryContainer = Color(0xFF3F4660), onSecondaryContainer = Color(0xFFE1E6FA),
    tertiary = Color(0xFFA8C8FF), onTertiary = Color(0xFF07326F),
    tertiaryContainer = Color(0xFF174784), onTertiaryContainer = Color(0xFFD9E6FF),
    background = Color(0xFF111219), onBackground = Color(0xFFE5E5EF),
    surface = Color(0xFF15161E), onSurface = Color(0xFFE5E5EF),
    surfaceVariant = Color(0xFF333541), onSurfaceVariant = Color(0xFFBEC0CE),
    surfaceContainerLowest = Color(0xFF0D0E14), surfaceContainerLow = Color(0xFF1C1D27),
    surfaceContainer = Color(0xFF22232E), surfaceContainerHigh = Color(0xFF2C2D38),
    surfaceContainerHighest = Color(0xFF363743),
    outline = Color(0xFF898C9E), outlineVariant = Color(0xFF414351),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF662721), onErrorContainer = Color(0xFFFFDAD5),
    inverseSurface = Color(0xFFE5E5EF), inverseOnSurface = Color(0xFF30313C),
    inversePrimary = Color(0xFF1A237E), surfaceTint = Color(0xFFBBC3FF)
)

@Composable
fun TrackLayoffTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
