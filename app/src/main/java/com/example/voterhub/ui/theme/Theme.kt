package com.example.voterhub.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val BrandColorScheme = lightColorScheme(
    primary = Saffron,
    onPrimary = Color.White,
    secondary = NavyBlue,
    onSecondary = Color.White,
    tertiary = IndiaGreen,
    onTertiary = Color.White,
    primaryContainer = AmberGlow,
    onPrimaryContainer = DarkGray,
    secondaryContainer = GentleLavender,
    onSecondaryContainer = NavyBlue,
    background = SoftCream,
    onBackground = DarkGray,
    surface = BackgroundWhite,
    onSurface = DarkGray,
    surfaceVariant = SurfaceVariant,
    outline = MediumGray,
    tertiaryContainer = AquaTeal,
    onTertiaryContainer = Color.White
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
)

@Composable
fun VoterHubTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BrandColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}