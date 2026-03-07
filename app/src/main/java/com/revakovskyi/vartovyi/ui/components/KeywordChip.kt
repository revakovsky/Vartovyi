package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun KeywordChip(
    modifier: Modifier = Modifier,
    keyword: String,
    onRemove: (keyword: String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(VartovyiTheme.spacing.extraSmall)
    ) {
        Text(
            text = keyword,
            style = VartovyiTheme.typography.bodyMedium,
            color = VartovyiTheme.colors.onSurface,
        )

        IconButton(onClick = { onRemove(keyword) }) {
            Text(
                text = "×",
                color = VartovyiTheme.colors.error,
            )
        }
    }
}

@Preview
@Composable
private fun KeywordChipPreview() {
    VartovyiTheme {
        KeywordChip(
            keyword = "ракета",
            onRemove = {},
        )
    }
}
