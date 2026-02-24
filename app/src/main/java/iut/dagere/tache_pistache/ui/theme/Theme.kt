package iut.dagere.tache_pistache.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PistachioGreenDark,
    secondary = PistachioLightDark,
    tertiary = PistachioDarkDark,
    background = PistachioCreamDark,
    surface = PistachioShellDark,
    onPrimary = PistachioOnPrimaryDark,
    onBackground = PistachioOnBackgroundDark,
    onSurface = PistachioOnBackgroundDark
)

private val LightColorScheme = lightColorScheme(
    primary = PistachioGreen,
    secondary = PistachioLight,
    tertiary = PistachioDark,
    background = PistachioCream,
    surface = PistachioShell,
    onPrimary = PistachioOnPrimary,
    onBackground = PistachioOnBackground,
    onSurface = PistachioOnBackground
)

@Composable
fun TachePistacheTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}