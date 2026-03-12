package com.revakovskyi.vartovyi.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun VartovyiDialog(
    title: String,
    message: String,
    confirmText: String,
    onDismiss: () -> Unit,
    containerColor: Color = VartovyiTheme.colors.surface,
    shape: Shape = VartovyiTheme.shapes.large,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = VartovyiTheme.typography.titleMedium,
                color = VartovyiTheme.colors.onSurface,
            )
        },
        text = {
            Text(
                text = message,
                style = VartovyiTheme.typography.bodyMedium,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = confirmText,
                    style = VartovyiTheme.typography.labelLarge,
                    color = VartovyiTheme.colors.primary,
                )
            }
        },
        containerColor = containerColor,
        shape = shape,
    )
}

@Preview(name = "Vartovyi dialog")
@Composable
private fun PreviewVartovyiDialog() {
    VartovyiTheme {
        VartovyiDialog(
            title = "Дублікат",
            message = "Слово \"Салтівка\" вже є у списку.",
            confirmText = "Зрозуміло",
            onDismiss = {},
        )
    }
}
