package com.revakovskyi.vartovyi.ui.screen.keywords.components

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
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun ImportStrategyDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    mergeText: String,
    cancelText: String,
    replaceText: String,
    onMerge: () -> Unit,
    onReplace: () -> Unit,
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
                TextButton(
                    onClick = onMerge,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = mergeText,
                        style = VartovyiTheme.typography.labelLarge,
                        color = VartovyiTheme.colors.primary,
                    )
                }

                TextButton(
                    onClick = onReplace,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = replaceText,
                        style = VartovyiTheme.typography.labelLarge,
                        color = VartovyiTheme.colors.error,
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = cancelText,
                        style = VartovyiTheme.typography.labelLarge,
                        color = VartovyiTheme.colors.onSurfaceVariant,
                    )
                }
            }
        },
        containerColor = VartovyiTheme.colors.surfaceVariant,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier.padding(vertical = VartovyiTheme.spacing.extraLarge)
    )
}

@Preview(name = "Import strategy dialog")
@Composable
private fun PreviewImportStrategyDialog() {
    VartovyiTheme {
        ImportStrategyDialog(
            title = "Імпорт ключових слів",
            message = "У вас вже є збережені дані!\n\nЯк виконати імпорт?\n" +
                    "• Видалити старі дані — усі ваші збережені слова буде видалено, " +
                    "залишаться лише дані з імпортованого файлу.\n" +
                    "• Залишити (додати поверх) — вміст файлу буде додано до ваших наявних слів. " +
                    "Дублікати буде пропущено автоматично.\n\n" +
                    "Якщо ви оберете видалення своїх даних — цю дію неможливо скасувати!",
            mergeText = "Залишити (додати поверх)",
            cancelText = "Скасувати",
            replaceText = "Видалити старі дані",
            onMerge = {},
            onReplace = {},
            onDismiss = {},
        )
    }
}
