package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.constants.SizeConstants.TOP_BAR_PERMISSION_ICON_SIZE
import com.revakovskyi.vartovyi.ui.components.InfoIconButton
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String,
    tooltipText: String,
    alignIconToEnd: Boolean = false,
) {
    var showDialog by remember { mutableStateOf(false) }
    val horizontalArrangement =
        if (alignIconToEnd) Arrangement.SpaceBetween
        else Arrangement.spacedBy(VartovyiTheme.spacing.small)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = VartovyiTheme.typography.titleMedium,
            color = VartovyiTheme.colors.onSurface,
            modifier = Modifier.weight(1f, fill = false),
        )

        InfoIconButton(
            onClick = { showDialog = true },
            modifier = Modifier.size(TOP_BAR_PERMISSION_ICON_SIZE.dp)
        )
    }

    if (showDialog) {
        VartovyiDialog(
            title = title,
            message = tooltipText,
            confirmText = stringResource(R.string.ok),
            onDismiss = { showDialog = false },
        )
    }
}

@Preview(name = "Section title — info button")
@Composable
private fun PreviewSectionTitle() {
    VartovyiTheme {
        SectionTitle(
            title = "Тригер-слова",
            tooltipText = "Тривога спрацює, якщо повідомлення містить будь-яке з цих слів",
        )
    }
}
