package com.revakovskyi.vartovyi.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

data class Shapes(
    val extraSmall: RoundedCornerShape = RoundedCornerShape(2.dp),
    val small: RoundedCornerShape = RoundedCornerShape(4.dp),
    val medium: RoundedCornerShape = RoundedCornerShape(8.dp),
    val large: RoundedCornerShape = RoundedCornerShape(12.dp),
    val extraLarge: RoundedCornerShape = RoundedCornerShape(16.dp),
    val largeIncreased: RoundedCornerShape = RoundedCornerShape(24.dp),
)

val LocalShapes = staticCompositionLocalOf { Shapes() }
