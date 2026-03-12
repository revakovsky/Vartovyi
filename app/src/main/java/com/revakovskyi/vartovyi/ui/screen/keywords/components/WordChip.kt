package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val CLOSE_ICON_ALPHA = 0.7f

@Composable
fun WordChip(
    modifier: Modifier = Modifier,
    text: String,
    containerColor: Color,
    contentColor: Color,
    onRemove: () -> Unit,
) {
    Surface(
        color = containerColor,
        shape = VartovyiTheme.shapes.largeIncreased,
        modifier = modifier
            .clip(VartovyiTheme.shapes.largeIncreased)
            .clickable { onRemove() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraSmall),
            modifier = Modifier.padding(
                start = VartovyiTheme.spacing.small,
                end = VartovyiTheme.spacing.small,
                top = VartovyiTheme.spacing.extraSmall,
                bottom = VartovyiTheme.spacing.extraSmall,
            ),
        ) {
            Text(
                text = text,
                style = VartovyiTheme.typography.bodyMedium,
                color = contentColor,
            )

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.close),
                contentDescription = null,
                tint = contentColor.copy(alpha = CLOSE_ICON_ALPHA),
                modifier = Modifier.size(VartovyiTheme.spacing.medium),
            )
        }
    }
}

@Preview(name = "Keyword chip")
@Composable
private fun PreviewWordChipKeyword() {
    VartovyiTheme {
        WordChip(
            text = "Салтівка",
            containerColor = VartovyiTheme.colors.surfaceVariant,
            contentColor = VartovyiTheme.colors.onSurfaceVariant,
            onRemove = {},
        )
    }
}

@Preview(name = "Stop word chip")
@Composable
private fun PreviewWordChipStopWord() {
    VartovyiTheme {
        WordChip(
            text = "відбій",
            containerColor = VartovyiTheme.colors.tertiaryContainer,
            contentColor = VartovyiTheme.colors.onTertiaryContainer,
            onRemove = {},
        )
    }
}
