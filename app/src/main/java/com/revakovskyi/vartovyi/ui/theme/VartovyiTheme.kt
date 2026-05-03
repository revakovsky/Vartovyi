package com.revakovskyi.vartovyi.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

object VartovyiTheme {

    val colors: ColorScheme
        @Composable get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable get() = MaterialTheme.typography

    val spacing: Spacing
        @Composable get() = LocalSpacing.current

    val shapes: Shapes
        @Composable get() = LocalShapes.current

}
