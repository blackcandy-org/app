package org.blackcandy.android.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF765084),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFF8D8FF),
        onPrimaryContainer = Color(0xFF2D0A3C),
        inversePrimary = Color(0xFFE4B7F3),
        secondary = Color(0xFF69596D),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFF1DCF4),
        onSecondaryContainer = Color(0xFF231728),
        tertiary = Color(0xFF815250),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFDAD8),
        onTertiaryContainer = Color(0xFF331111),
        background = Color(0xFFFFFFFB),
        onBackground = Color(0xFF1F1A1F),
        surface = Color(0xFFFFFFFB),
        onSurface = Color(0xFF1F1A1F),
        surfaceVariant = Color(0xFFEBDFE9),
        onSurfaceVariant = Color(0xFF4C444D),
        inverseSurface = Color(0xFF342F34),
        inverseOnSurface = Color(0xFFF8EEF5),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        outline = Color(0xFF7D747D),
        outlineVariant = Color(0xFFCEC3CD),
        surfaceBright = Color(0xFFFFFFFB),
        surfaceContainer = Color(0xFFF5EBF2),
        surfaceContainerHigh = Color(0xFFEFE5ED),
        surfaceContainerHighest = Color(0xFFE9E0E7),
        surfaceContainerLow = Color(0xFFFBF1F8),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceDim = Color(0xFFE1D7DF),
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFFE4B7F3),
        onPrimary = Color(0xFF442253),
        primaryContainer = Color(0xFF5D386B),
        onPrimaryContainer = Color(0xFFF8D8FF),
        inversePrimary = Color(0xFF765084),
        secondary = Color(0xFFD4C0D7),
        onSecondary = Color(0xFF392C3D),
        secondaryContainer = Color(0xFF504255),
        onSecondaryContainer = Color(0xFFF1DCF4),
        tertiary = Color(0xFFF5B7B5),
        onTertiary = Color(0xFF4C2524),
        tertiaryContainer = Color(0xFF663B39),
        onTertiaryContainer = Color(0xFFFFDAD8),
        background = Color(0xFF212121),
        onBackground = Color(0xFFE9E0E7),
        surface = Color(0xFF212121),
        onSurface = Color(0xFFE9E0E7),
        surfaceVariant = Color(0xFF4C444D),
        onSurfaceVariant = Color(0xFFCEC3CD),
        inverseSurface = Color(0xFFE9E0E7),
        inverseOnSurface = Color(0xFF342F34),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = Color(0xFF978E97),
        outlineVariant = Color(0xFF4C444D),
        surfaceBright = Color(0xFF3D373D),
        surfaceContainer = Color(0xFF231E23),
        surfaceContainerHigh = Color(0xFF2D282E),
        surfaceContainerHighest = Color(0xFF383339),
        surfaceContainerLow = Color(0xFF1F1A1F),
        surfaceContainerLowest = Color(0xFF110D12),
        surfaceDim = Color(0xFF212121),
    )

@Composable
fun BlackCandyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
        content = content,
    )
}
