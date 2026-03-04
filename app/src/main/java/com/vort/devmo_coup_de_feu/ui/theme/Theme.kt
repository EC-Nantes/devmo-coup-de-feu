package com.vort.devmo_coup_de_feu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CoupDeFeuColorScheme = darkColorScheme(
    primary = Orange,
    onPrimary = TextPrimary,
    secondary = Blue,
    onSecondary = TextPrimary,
    error = Red,
    onError = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceBorder,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder
)

@Composable
fun DevmocoupdefeuTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CoupDeFeuColorScheme,
        typography = Typography,
        content = content
    )
}