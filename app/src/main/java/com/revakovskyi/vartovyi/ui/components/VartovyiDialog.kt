package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun VartovyiDialog(
    title: String,
    message: String,
    confirmText: String,
    shape: Shape = VartovyiTheme.shapes.large,
    containerColor: Color = VartovyiTheme.colors.surfaceVariant,
    confirmContentColor: Color = VartovyiTheme.colors.primary,
    dismissText: String? = null,
    onConfirm: (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    val resolvedOnConfirm = onConfirm ?: onDismiss

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
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = message,
                    style = VartovyiTheme.typography.bodyMedium,
                    color = VartovyiTheme.colors.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = resolvedOnConfirm) {
                Text(
                    text = confirmText,
                    style = VartovyiTheme.typography.labelLarge,
                    color = confirmContentColor,
                )
            }
        },
        dismissButton = {
            dismissText?.let { text ->
                TextButton(onClick = onDismiss) {
                    Text(
                        text = text,
                        style = VartovyiTheme.typography.labelLarge,
                        color = VartovyiTheme.colors.onSurfaceVariant,
                    )
                }
            }
        },
        containerColor = containerColor,
        shape = shape,
        modifier = Modifier.padding(vertical = VartovyiTheme.spacing.extraLarge)
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

@Preview(name = "Vartovyi dialog with dismiss")
@Composable
private fun PreviewVartovyiDialogWithDismiss() {
    VartovyiTheme {
        VartovyiDialog(
            title = "Очистити логи?",
            message = "Це видалить усі збережені записи журналу з цього пристрою.",
            confirmText = "Очистити",
            dismissText = "Скасувати",
            onDismiss = {},
            onConfirm = {},
        )
    }
}

@Preview(
    name = "Vartovyi dialog — long message",
    heightDp = 640,
)
@Composable
private fun PreviewVartovyiDialogLongMessage() {
    VartovyiTheme {
        VartovyiDialog(
            title = "Як працює імпорт ключових слів",
            message = "Під час імпорту ви можете обрати, що зробити з наявним списком: " +
                "повністю замінити його новими словами або додати нові слова поверх " +
                "існуючих. Якщо ви додаєте поверх, кожне нове слово перевіряється на " +
                "дублікат у тому самому режимі (слово, усі слова або фраза) — наявні " +
                "збігаються пропускаються, а решта додається. Дуже довгий текст у діалозі " +
                "більше не розтягується на весь екран: він обмежений по висоті та " +
                "прокручується всередині, лишаючи відступ від країв екрана зверху та знизу.",
            confirmText = "Зрозуміло",
            dismissText = "Скасувати",
            onDismiss = {},
            onConfirm = {},
        )
    }
}
