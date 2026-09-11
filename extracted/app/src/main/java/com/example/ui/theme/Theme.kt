package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NatureGreenPrimaryDark,
    onPrimary = NatureGreenOnPrimaryDark,
    primaryContainer = NatureGreenContainerDark,
    onPrimaryContainer = NatureGreenOnContainerDark,
    secondary = NatureSageSecondaryDark,
    onSecondary = NatureSageOnSecondaryDark,
    secondaryContainer = NatureSageContainerDark,
    onSecondaryContainer = NatureSageOnContainerDark,
    tertiary = NatureEarthTertiaryDark,
    onTertiary = NatureEarthOnContainerDark,
    tertiaryContainer = NatureEarthContainerDark,
    onTertiaryContainer = NatureEarthTertiaryDark,
    background = NatureBackgroundDark,
    onBackground = NatureTextPrimaryDark,
    surface = NatureSurfaceDark,
    onSurface = NatureTextPrimaryDark,
    surfaceVariant = NatureSurfaceVariantDark,
    onSurfaceVariant = NatureTextSecondaryDark,
    outline = NatureOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = NatureGreenPrimaryLight,
    onPrimary = NatureGreenOnPrimaryLight,
    primaryContainer = NatureGreenContainerLight,
    onPrimaryContainer = NatureGreenOnContainerLight,
    secondary = NatureSageSecondaryLight,
    onSecondary = NatureSageOnSecondaryLight,
    secondaryContainer = NatureSageContainerLight,
    onSecondaryContainer = NatureSageOnContainerLight,
    tertiary = NatureEarthTertiaryLight,
    onTertiary = NatureEarthOnContainerLight,
    tertiaryContainer = NatureEarthContainerLight,
    onTertiaryContainer = NatureEarthTertiaryLight,
    background = NatureBackgroundLight,
    onBackground = NatureTextPrimaryLight,
    surface = NatureSurfaceLight,
    onSurface = NatureTextPrimaryLight,
    surfaceVariant = NatureSurfaceVariantLight,
    onSurfaceVariant = NatureTextSecondaryLight,
    outline = NatureOutlineLight
)

@Composable
fun HisabBoiTheme(
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
