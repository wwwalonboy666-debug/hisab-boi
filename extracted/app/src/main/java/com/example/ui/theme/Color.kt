package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Light Palette: Nature x Finance x Peace
// Warm off-white, soft forest green, sage and warm earth
val NatureGreenPrimaryLight = Color(0xFF1B5E3C)
val NatureGreenOnPrimaryLight = Color(0xFFFFFFFF)
val NatureGreenContainerLight = Color(0xFFD3EEDD)
val NatureGreenOnContainerLight = Color(0xFF042113)

val NatureSageSecondaryLight = Color(0xFF426850)
val NatureSageOnSecondaryLight = Color(0xFFFFFFFF)
val NatureSageContainerLight = Color(0xFFE3EDE5)
val NatureSageOnContainerLight = Color(0xFF132A1D)

val NatureEarthTertiaryLight = Color(0xFF7A5C43)
val NatureEarthContainerLight = Color(0xFFF7ECE4)
val NatureEarthOnContainerLight = Color(0xFF2C190D)

val NatureBackgroundLight = Color(0xFFF7FAF7) // Serene warm off-white canvas
val NatureSurfaceLight = Color(0xFFFFFFFF)
val NatureSurfaceVariantLight = Color(0xFFEAF1EB)
val NatureTextPrimaryLight = Color(0xFF15221B)
val NatureTextSecondaryLight = Color(0xFF4F6358)
val NatureOutlineLight = Color(0xFFD6E2D9)

// Dark Palette: Deep charcoal / green-black, comfortable bright light text and calming leaf green accents
val NatureGreenPrimaryDark = Color(0xFF68D391) // Crisp vibrant green accent
val NatureGreenOnPrimaryDark = Color(0xFF042614)
val NatureGreenContainerDark = Color(0xFF174D2E)
val NatureGreenOnContainerDark = Color(0xFFD0F8DF)

val NatureSageSecondaryDark = Color(0xFF90CCA3)
val NatureSageOnSecondaryDark = Color(0xFF153622)
val NatureSageContainerDark = Color(0xFF23442E)
val NatureSageOnContainerDark = Color(0xFFDCF5E3)

val NatureEarthTertiaryDark = Color(0xFFDEC3AC)
val NatureEarthContainerDark = Color(0xFF4A3423)
val NatureEarthOnContainerDark = Color(0xFFFCEFE6)

val NatureBackgroundDark = Color(0xFF0F1713) // Deep charcoal green
val NatureSurfaceDark = Color(0xFF1A2A24) // Elevated dark slate leaf
val NatureSurfaceVariantDark = Color(0xFF2A3C35)
val NatureTextPrimaryDark = Color(0xFFF6FFF8) // Crisp, bright white-mint with maximum legibility
val NatureTextSecondaryDark = Color(0xFFE3F0E8) // Brighter dark-mode text for cards, inputs, and dialogs
val NatureOutlineDark = Color(0xFF5D7C6D)

// Financial semantic colors (High WCAG Contrast in both Light & Dark modes)
val IncomeGreenLight = Color(0xFF1B7330)
val IncomeGreenDark = Color(0xFF4ADE80) // Vibrant, luminous spring green on dark surfaces (> 9:1 contrast)
val IncomeContainerLight = Color(0xFFE8F5E9)
val IncomeContainerDark = Color(0xFF163821)

val ExpenseRoseLight = Color(0xFFC62828)
val ExpenseRoseDark = Color(0xFFFF8A80) // Soft, radiant coral rose on dark surfaces (> 8.5:1 contrast)
val ExpenseContainerLight = Color(0xFFFFEBEE)
val ExpenseContainerDark = Color(0xFF3B1A1E)

// Warning / Alert semantic colors
val WarningAmberLight = Color(0xFFE65100)
val WarningAmberDark = Color(0xFFFFB74D) // Luminous warm amber on dark surfaces
val WarningContainerLight = Color(0xFFFFF3E0)
val WarningContainerDark = Color(0xFF3E2713)

// Backward-compatible defaults
val IncomeGreen = IncomeGreenLight
val ExpenseRose = ExpenseRoseLight
val ForestGreen = NatureGreenPrimaryLight
val IncomeGreenContainerLight = IncomeContainerLight
val ExpenseRoseContainerLight = ExpenseContainerLight

val GoldenCoin = Color(0xFFD49E24)
val GoldenCoinLight = Color(0xFFFFF8E1)

// Dynamic theme-aware financial color getters
@Composable
@ReadOnlyComposable
fun financialIncomeColor(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) IncomeGreenDark else IncomeGreenLight
}

@Composable
@ReadOnlyComposable
fun financialExpenseColor(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) ExpenseRoseDark else ExpenseRoseLight
}

@Composable
@ReadOnlyComposable
fun financialIncomeContainer(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) IncomeContainerDark else IncomeContainerLight
}

@Composable
@ReadOnlyComposable
fun financialExpenseContainer(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) ExpenseContainerDark else ExpenseContainerLight
}

@Composable
@ReadOnlyComposable
fun warningColor(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) WarningAmberDark else WarningAmberLight
}

@Composable
@ReadOnlyComposable
fun warningContainer(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) WarningContainerDark else WarningContainerLight
}

