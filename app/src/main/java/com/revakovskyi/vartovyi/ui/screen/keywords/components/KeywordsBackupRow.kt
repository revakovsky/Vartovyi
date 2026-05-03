package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val BACKUP_ROW_MAX_WIDTH = 450

@Composable
fun KeywordsBackupRow(
    modifier: Modifier = Modifier,
    isExportEnabled: Boolean = true,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .widthIn(max = BACKUP_ROW_MAX_WIDTH.dp)
            .fillMaxWidth()
    ) {
        VartovyiActionButton(
            text = stringResource(R.string.keywords_export),
            onClick = onExportClick,
            style = VartovyiActionButtonStyle.Outlined,
            enabled = isExportEnabled,
            borderColor = VartovyiTheme.colors.primary,
            icon = ImageVector.vectorResource(R.drawable.export),
            modifier = Modifier.weight(1f)
        )

        VartovyiActionButton(
            text = stringResource(R.string.keywords_import),
            onClick = onImportClick,
            style = VartovyiActionButtonStyle.Outlined,
            borderColor = VartovyiTheme.colors.primary,
            icon = ImageVector.vectorResource(R.drawable.resource_import),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(name = "Keywords backup row — export enabled")
@Composable
private fun KeywordsBackupRowEnabledPreview() {
    VartovyiTheme {
        KeywordsBackupRow(
            isExportEnabled = true,
            onExportClick = {},
            onImportClick = {},
        )
    }
}

@Preview(name = "Keywords backup row — export disabled")
@Composable
private fun KeywordsBackupRowDisabledPreview() {
    VartovyiTheme {
        KeywordsBackupRow(
            isExportEnabled = false,
            onExportClick = {},
            onImportClick = {},
        )
    }
}
