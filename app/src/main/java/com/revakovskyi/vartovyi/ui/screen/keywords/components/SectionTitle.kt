package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String,
    tooltipText: String,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = VartovyiTheme.typography.titleMedium,
            color = VartovyiTheme.colors.onSurface,
        )

        FilledTonalIconButton(
            onClick = { showDialog = true },
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = VartovyiTheme.colors.onSurfaceVariant.copy(alpha = 0.35f),
            ),
            modifier = Modifier.size(VartovyiTheme.spacing.extraLarge),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.info),
                contentDescription = null,
                modifier = Modifier.size(VartovyiTheme.spacing.standard),
            )
        }
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
