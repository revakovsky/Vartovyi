package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val BORDER_STROKE_WIDTH_DP = 1
private const val BUTTON_MAX_WIDTH = 450

enum class VartovyiActionButtonStyle {
    Filled,
    Outlined,
}

@Composable
fun VartovyiActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    style: VartovyiActionButtonStyle,
    enabled: Boolean = true,
    containerColor: Color = VartovyiTheme.colors.primary,
    contentColor: Color = VartovyiTheme.colors.onPrimary,
    borderColor: Color = VartovyiTheme.colors.outline,
    icon: ImageVector? = null,
    iconTint: Color = contentColor,
    iconSize: Dp = VartovyiTheme.spacing.large,
    minWidth: Dp = Dp.Unspecified,
    maxWidth: Dp = BUTTON_MAX_WIDTH.dp,
    fillMaxWidthFraction: Float = 1f,
    height: Dp = VartovyiTheme.spacing.massive,
) {
    val buttonModifier = modifier
        .widthIn(
            min = minWidth,
            max = maxWidth,
        )
        .fillMaxWidth(fillMaxWidthFraction)
        .padding(horizontal = VartovyiTheme.spacing.small)
        .height(height)

    if (style == VartovyiActionButtonStyle.Filled) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
            modifier = buttonModifier
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize),
                )

                Spacer(modifier = Modifier.width(VartovyiTheme.spacing.medium))
            }

            Text(
                text = text,
                style = VartovyiTheme.typography.titleMedium,
                color = contentColor,
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            border = BorderStroke(
                width = BORDER_STROKE_WIDTH_DP.dp,
                color = borderColor,
            ),
            modifier = buttonModifier
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize),
                )

                Spacer(modifier = Modifier.width(VartovyiTheme.spacing.medium))
            }

            Text(
                text = text,
                style = VartovyiTheme.typography.titleMedium,
                color = contentColor,
            )
        }
    }
}

@Preview
@Composable
private fun VartovyiActionButtonFilledPreview() {
    VartovyiTheme {
        VartovyiActionButton(
            text = "Activate",
            onClick = {},
            style = VartovyiActionButtonStyle.Filled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun VartovyiActionButtonOutlinedPreview() {
    VartovyiTheme {
        VartovyiActionButton(
            text = "Clear logs",
            onClick = {},
            style = VartovyiActionButtonStyle.Outlined,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
