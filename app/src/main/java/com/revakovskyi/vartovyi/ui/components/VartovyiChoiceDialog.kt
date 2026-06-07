package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

enum class DialogChoiceRole {
    PRIMARY,
    DESTRUCTIVE,
    NEUTRAL,
}

data class DialogChoice(
    val text: String,
    val role: DialogChoiceRole,
    val onClick: () -> Unit,
)

@Composable
fun VartovyiChoiceDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    choices: List<DialogChoice>,
    onDismiss: () -> Unit,
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
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                choices.forEach { choice ->
                    TextButton(
                        onClick = choice.onClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = choice.text,
                            style = VartovyiTheme.typography.labelLarge,
                            color = dialogChoiceColor(choice.role),
                        )
                    }
                }
            }
        },
        containerColor = VartovyiTheme.colors.surfaceVariant,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier.padding(vertical = VartovyiTheme.spacing.extraLarge)
    )
}

@Composable
private fun dialogChoiceColor(role: DialogChoiceRole): Color {
    return when (role) {
        DialogChoiceRole.PRIMARY -> VartovyiTheme.colors.primary
        DialogChoiceRole.DESTRUCTIVE -> VartovyiTheme.colors.error
        DialogChoiceRole.NEUTRAL -> VartovyiTheme.colors.onSurfaceVariant
    }
}

@Preview(name = "Choice dialog — import (with destructive)")
@Composable
private fun PreviewVartovyiChoiceDialogImport() {
    VartovyiTheme {
        VartovyiChoiceDialog(
            title = "Імпорт ключових слів",
            message = "У вас вже є збережені дані!\n\nЯк виконати імпорт?\n" +
                    "• Видалити старі дані — усі ваші збережені слова буде видалено, " +
                    "залишаться лише дані з імпортованого файлу.\n" +
                    "• Залишити (додати поверх) — вміст файлу буде додано до ваших наявних слів. " +
                    "Дублікати буде пропущено автоматично.\n\n" +
                    "Якщо ви оберете видалення своїх даних — цю дію неможливо скасувати!",
            choices = listOf(
                DialogChoice(
                    text = "Залишити (додати поверх)",
                    role = DialogChoiceRole.PRIMARY,
                    onClick = {},
                ),
                DialogChoice(
                    text = "Видалити старі дані",
                    role = DialogChoiceRole.DESTRUCTIVE,
                    onClick = {},
                ),
                DialogChoice(
                    text = "Скасувати",
                    role = DialogChoiceRole.NEUTRAL,
                    onClick = {},
                ),
            ),
            onDismiss = {},
        )
    }
}

@Preview(name = "Choice dialog — export (two primary)")
@Composable
private fun PreviewVartovyiChoiceDialogExport() {
    VartovyiTheme {
        VartovyiChoiceDialog(
            title = "Експорт ключових слів",
            message = "Як ви хочете експортувати ваші дані?\n" +
                    "• Зберегти на пристрій — обрати теку і зберегти файл локально.\n" +
                    "• Поділитися — надіслати файл через інші додатки.",
            choices = listOf(
                DialogChoice(
                    text = "Зберегти на пристрій",
                    role = DialogChoiceRole.PRIMARY,
                    onClick = {},
                ),
                DialogChoice(
                    text = "Поділитися",
                    role = DialogChoiceRole.PRIMARY,
                    onClick = {},
                ),
                DialogChoice(
                    text = "Скасувати",
                    role = DialogChoiceRole.NEUTRAL,
                    onClick = {},
                ),
            ),
            onDismiss = {},
        )
    }
}
