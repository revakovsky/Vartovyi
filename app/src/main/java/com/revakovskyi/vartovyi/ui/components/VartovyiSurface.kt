package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun VartovyiSurface(
    modifier: Modifier = Modifier,
    color: Color = VartovyiTheme.colors.surface,
    shape: Shape = VartovyiTheme.shapes.large,
    border: BorderStroke? = BorderStroke(
        width = VartovyiTheme.spacing.one,
        color = VartovyiTheme.colors.outlineVariant,
    ),
    isClickable: Boolean = false,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = shape,
        border = border,
        enabled = isClickable,
        onClick = onClick,
    ) {
        content()
    }
}

@Preview
@Composable
private fun PreviewVartovyiSurface() {
    VartovyiTheme {
        VartovyiSurface(
            content = { }
        )
    }
}
