package im.manus.atlas.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1F88E8),
    secondary = Color(0xFF0D7AA0),
    tertiary = Color(0xFF26A69A),
    background = Color(0xFF0F1419),
    surface = Color(0xFF1A202C),
    onBackground = Color(0xFFE8EAED),
    onSurface = Color(0xFFE8EAED)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1F88E8),
    secondary = Color(0xFF0D7AA0),
    tertiary = Color(0xFF26A69A),
    background = Color(0xFFFEFBFF),
    surface = Color(0xFFFEFBFF),
    onBackground = Color(0xFF1A1C1E),
    onSurface = Color(0xFF1A1C1E)
)

@Composable
fun AtlasTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}