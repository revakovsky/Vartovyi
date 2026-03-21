package com.revakovskyi.vartovyi.ui.screen.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
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
fun SettingsSectionContainer(
    modifier: Modifier = Modifier,
    title: String,
    titleTooltipText: String? = null,
    content: @Composable () -> Unit,
) {
    Surface(
        color = VartovyiTheme.colors.surface,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(VartovyiTheme.spacing.standard)
        ) {
            SettingsSectionTitleRow(
                title = title,
                tooltipText = titleTooltipText,
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

            HorizontalDivider(color = VartovyiTheme.colors.outline)

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

            content()
        }
    }
}

@Composable
private fun SettingsSectionTitleRow(
    title: String,
    tooltipText: String?,
) {
    if (tooltipText == null) {
        Text(
            text = title,
            style = VartovyiTheme.typography.titleLarge,
            color = VartovyiTheme.colors.onSurface,
        )

        return
    }

    var showDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = VartovyiTheme.typography.titleLarge,
            color = VartovyiTheme.colors.onSurface,
            modifier = Modifier.weight(1f)
        )

        FilledTonalIconButton(
            onClick = { showDialog = true },
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = VartovyiTheme.colors.onSurfaceVariant.copy(alpha = 0.35f),
            ),
            modifier = Modifier.size(VartovyiTheme.spacing.extraLarge)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.info),
                contentDescription = null,
                modifier = Modifier.size(VartovyiTheme.spacing.standard)
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

@Preview(name = "Title only")
@Composable
private fun SettingsSectionContainerPreviewTitleOnly() {
    VartovyiTheme {
        SettingsSectionContainer(
            title = "Data",
        ) {
            Text(
                text = "Section body",
                style = VartovyiTheme.typography.bodyMedium,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "With info button")
@Composable
private fun SettingsSectionContainerPreviewWithTooltip() {
    VartovyiTheme {
        SettingsSectionContainer(
            title = "Work schedule",
            titleTooltipText = "When enabled, keyword checks apply only inside the selected time range.",
        ) {
            Text(
                text = "Toggle and time rows would go here.",
                style = VartovyiTheme.typography.bodyMedium,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )
        }
    }
}
