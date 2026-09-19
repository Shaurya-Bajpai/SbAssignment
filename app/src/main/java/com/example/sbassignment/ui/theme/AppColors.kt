package com.example.sbassignment.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Fixed app palette — intentionally NOT derived from MaterialTheme.colorScheme.
 * Every screen pulls from here so the app looks identical regardless of the
 * device's light/dark system setting.
 */
object AppColors {
    val Primary = Color(0xFF5B4FE9)
    val PrimaryDark = Color(0xFF4338CA)
    val PrimaryLight = Color(0xFFEEECFF)
    val PrimarySoft = Color(0xFFF3F1FF)

    val Background = Color(0xFFF7F7FC)
    val Surface = Color(0xFFFFFFFF)

    val OnPrimary = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF1F2333)
    val OnSurfaceMuted = Color(0xFF6E7180)
    val Outline = Color(0xFFECEBF5)

    val Success = Color(0xFF16A34A)
    val SuccessBg = Color(0xFFE6F8EC)
    val Warning = Color(0xFFB45309)
    val WarningBg = Color(0xFFFFF3E0)

    val AvatarPalette = listOf(
        Color(0xFF6D5DF6), Color(0xFFEC6BAA), Color(0xFF22B8CF),
        Color(0xFFF2994A), Color(0xFF2F9E44), Color(0xFFE64980)
    )

    val PrimaryGradient = Brush.linearGradient(listOf(Color(0xFF6D5DF6), Color(0xFF9B6BF2)))
    val HeaderGradient = Brush.verticalGradient(listOf(Color(0xFF5B4FE9), Color(0xFF8B7CF6)))
}

/** Deterministic accent color per person, so the same name always gets the same tint. */
fun avatarColorFor(seed: String): Color {
    val index = (seed.sumOf { it.code }) % AppColors.AvatarPalette.size
    return AppColors.AvatarPalette[index]
}

fun initialsFor(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}