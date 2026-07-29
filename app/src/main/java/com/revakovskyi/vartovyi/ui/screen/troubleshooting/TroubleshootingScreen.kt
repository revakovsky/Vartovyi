package com.revakovskyi.vartovyi.ui.screen.troubleshooting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiBackTopBar
import com.revakovskyi.vartovyi.ui.screen.troubleshooting.models.TroubleshootingGroup
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val COLLAPSED_CHEVRON_ROTATION_DEGREES = 0f
private const val EXPANDED_CHEVRON_ROTATION_DEGREES = 180f
private const val ANIMATION_DURATION_MILLIS = 220
private const val BULLET_PREFIX = "•"

@Composable
fun TroubleshootingScreen(
    onNavigateBack: () -> Unit,
) {
    TroubleshootingContent(onNavigateBack = onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TroubleshootingContent(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    val topBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(),
    )

    val listState = rememberLazyListState()

    val expandedGroupKeys = remember { mutableStateMapOf<Int, Boolean>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
    ) {
        VartovyiBackTopBar(
            title = stringResource(R.string.settings_open_troubleshooting),
            backContentDescription = stringResource(R.string.troubleshooting_back),
            scrollBehavior = topBarScrollBehavior,
            onNavigateBack = onNavigateBack,
        )

        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
            contentPadding = PaddingValues(bottom = VartovyiTheme.spacing.medium),
            modifier = Modifier.fillMaxSize()
        ) {
            item(contentType = "description") {
                Text(
                    text = stringResource(R.string.troubleshooting_lead),
                    style = VartovyiTheme.typography.bodyLarge,
                    color = VartovyiTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                        .fillMaxWidth()
                        .padding(
                            horizontal = VartovyiTheme.spacing.standard,
                            vertical = VartovyiTheme.spacing.medium,
                        )
                )
            }

            itemsIndexed(
                items = TroubleshootingGroup.all,
                contentType = { _, _ -> "tip" },
                itemContent = { index, group ->
                    TroubleshootingExpandableItem(
                        title = stringResource(group.titleResId),
                        body = stringResource(group.bodyResId),
                        isExpanded = expandedGroupKeys[index] == true,
                        onClick = {
                            expandedGroupKeys[index] = expandedGroupKeys[index] != true
                        },
                    )
                },
            )
        }
    }
}

@Composable
private fun TroubleshootingExpandableItem(
    modifier: Modifier = Modifier,
    title: String,
    body: String,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    val chevronRotationDegrees by animateFloatAsState(
        targetValue =
            if (isExpanded) EXPANDED_CHEVRON_ROTATION_DEGREES
            else COLLAPSED_CHEVRON_ROTATION_DEGREES,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MILLIS),
        label = "troubleshootingChevronRotation",
    )

    Surface(
        color = VartovyiTheme.colors.surfaceVariant,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier
            .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
            .fillMaxWidth()
            .padding(horizontal = VartovyiTheme.spacing.standard),
    ) {
        Column(
            modifier = Modifier.padding(VartovyiTheme.spacing.standard)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
            ) {
                Text(
                    text = title,
                    style = VartovyiTheme.typography.titleMedium,
                    color = VartovyiTheme.colors.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(VartovyiTheme.spacing.small))

                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.down),
                    contentDescription = null,
                    tint = VartovyiTheme.colors.onSurfaceVariant,
                    modifier = Modifier
                        .size(VartovyiTheme.spacing.large)
                        .graphicsLayer { rotationZ = chevronRotationDegrees },
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(durationMillis = ANIMATION_DURATION_MILLIS)) +
                        expandVertically(animationSpec = tween(durationMillis = ANIMATION_DURATION_MILLIS)),
                exit = fadeOut(animationSpec = tween(durationMillis = ANIMATION_DURATION_MILLIS)) +
                        shrinkVertically(animationSpec = tween(durationMillis = ANIMATION_DURATION_MILLIS)),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

                    HorizontalDivider(color = VartovyiTheme.colors.outline)

                    Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

                    TroubleshootingBodyLines(body = body)
                }
            }
        }
    }
}

@Composable
private fun TroubleshootingBodyLines(body: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
        modifier = Modifier.fillMaxWidth()
    ) {
        body.split("\n").forEach { line ->
            val isBullet = line.startsWith(BULLET_PREFIX)

            Text(
                text = line,
                style = if (isBullet) VartovyiTheme.typography.bodyMedium else VartovyiTheme.typography.bodySmall,
                color = if (isBullet) VartovyiTheme.colors.onSurface else VartovyiTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TroubleshootingContentPreview() {
    VartovyiTheme {
        Box(
            modifier = Modifier.background(VartovyiTheme.colors.background)
        ) {
            TroubleshootingContent(onNavigateBack = {})
        }
    }
}
