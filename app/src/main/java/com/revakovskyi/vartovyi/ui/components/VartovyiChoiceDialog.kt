package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.revakovskyi.vartovyi.ui.theme.MinAutoSizeFontSize
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
    val windowSize = LocalWindowInfo.current.containerSize

    val isLandscape = windowSize.width > windowSize.height

    val actionChoices = choices.filter { choice -> choice.role != DialogChoiceRole.NEUTRAL }
    val neutralChoices = choices.filter { choice -> choice.role == DialogChoiceRole.NEUTRAL }

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
                if (isLandscape) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            space = VartovyiTheme.spacing.small,
                            alignment = Alignment.CenterHorizontally,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        actionChoices.forEach { choice ->
                            DialogChoiceButton(
                                choice = choice,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    neutralChoices.forEach { choice ->
                        DialogChoiceButton(
                            choice = choice,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    choices.forEach { choice ->
                        DialogChoiceButton(
                            choice = choice,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        containerColor = VartovyiTheme.colors.surfaceVariant,
        shape = VartovyiTheme.shapes.large,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier
            .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
            .padding(
                horizontal = VartovyiTheme.spacing.standard,
                vertical = VartovyiTheme.spacing.extraLarge,
            )
    )
}

@Composable
private fun DialogChoiceButton(
    modifier: Modifier = Modifier,
    choice: DialogChoice,
) {
    TextButton(
        onClick = choice.onClick,
        modifier = modifier
    ) {
        Text(
            text = choice.text,
            style = VartovyiTheme.typography.labelLarge,
            color = dialogChoiceColor(choice.role),
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            autoSize = TextAutoSize.StepBased(
                minFontSize = MinAutoSizeFontSize,
                maxFontSize = VartovyiTheme.typography.labelLarge.fontSize,
            ),
        )
    }
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
