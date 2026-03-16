package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val BORDER_WIDTH_FOCUSED_DP = 2
private const val BORDER_WIDTH_DEFAULT_DP = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordInputRow(
    modifier: Modifier = Modifier,
    value: String,
    hint: String,
    onFocusChanged: (isFocused: Boolean) -> Unit = {},
    onAdd: () -> Unit,
    onClear: () -> Unit,
    onValueChange: (value: String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val clearButtonTooltipText = stringResource(R.string.keywords_input_clear_tooltip)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = modifier.fillMaxWidth(),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onAdd() }),
            textStyle = VartovyiTheme.typography.bodyMedium.copy(
                color = VartovyiTheme.colors.onSurface,
            ),
            cursorBrush = SolidColor(VartovyiTheme.colors.primary),
            interactionSource = interactionSource,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { state -> onFocusChanged(state.isFocused) },
        ) { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = {
                    Text(
                        text = hint,
                        style = VartovyiTheme.typography.bodyMedium,
                    )
                },
                trailingIcon = {
                    if (value.isNotBlank()) {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                positioning = TooltipAnchorPosition.Above
                            ),
                            tooltip = {
                                PlainTooltip {
                                    Text(text = clearButtonTooltipText)
                                }
                            },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(
                                onClick = onClear,
                                enabled = value.isNotBlank(),
                                modifier = Modifier.size(VartovyiTheme.spacing.large),
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.close),
                                    contentDescription = null,
                                    tint = VartovyiTheme.colors.onSurfaceVariant,
                                    modifier = Modifier.size(VartovyiTheme.spacing.medium),
                                )
                            }
                        }
                    }
                },
                contentPadding = PaddingValues(
                    horizontal = VartovyiTheme.spacing.standard,
                    vertical = VartovyiTheme.spacing.small,
                ),
                container = {
                    val isFocused by interactionSource.collectIsFocusedAsState()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = if (isFocused) BORDER_WIDTH_FOCUSED_DP.dp else BORDER_WIDTH_DEFAULT_DP.dp,
                                color = if (isFocused) VartovyiTheme.colors.primary else VartovyiTheme.colors.outline,
                                shape = VartovyiTheme.shapes.small,
                            )
                    )
                },
            )
        }

        Button(
            onClick = onAdd,
            shape = VartovyiTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = VartovyiTheme.colors.primary,
                contentColor = VartovyiTheme.colors.onPrimary,
            ),
            contentPadding = PaddingValues(VartovyiTheme.spacing.small),
            modifier = Modifier.size(VartovyiTheme.spacing.massive),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.add),
                contentDescription = null,
                modifier = Modifier.size(VartovyiTheme.spacing.large),
            )
        }
    }
}

@Preview(name = "Word input row — empty")
@Composable
private fun PreviewWordInputRowEmpty() {
    VartovyiTheme {
        WordInputRow(
            value = "",
            hint = "Наприклад: Салтівка",
            onClear = {},
            onValueChange = {},
            onAdd = {},
        )
    }
}

@Preview(name = "Word input row — with text")
@Composable
private fun PreviewWordInputRowWithText() {
    VartovyiTheme {
        WordInputRow(
            value = "Центр",
            hint = "Наприклад: Салтівка",
            onClear = {},
            onValueChange = {},
            onAdd = {},
        )
    }
}
