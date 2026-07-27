package com.revakovskyi.vartovyi.ui.screen.legal.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.components.ScrollProgressBar
import com.revakovskyi.vartovyi.ui.screen.legal.LegalConsentUiContract
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
internal fun LegalConsentLandscapeContent(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    scrollState: ScrollState,
    actionsScrollState: ScrollState,
    onAction: (action: LegalConsentUiContract.Action) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        modifier = modifier
            .displayCutoutPadding()
            .padding(horizontal = VartovyiTheme.spacing.standard)
    ) {
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .heightIn(min = maxHeight)
            ) {
                LegalConsentTexts()
            }

            ScrollProgressBar(
                scrollState = scrollState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(actionsScrollState)
        ) {
            LegalConsentDocumentButtons(
                isEnabled = isEnabled,
                onAction = onAction,
            )

            LegalConsentDecisionButtons(
                isEnabled = isEnabled,
                onAction = onAction,
                modifier = Modifier.padding(top = VartovyiTheme.spacing.huge)
            )
        }
    }
}

@Preview(
    name = "Legal consent landscape — small phone",
    widthDp = 640,
    heightDp = 320,
)
@Composable
private fun LegalConsentLandscapeContentSmallPhonePreview() {
    VartovyiTheme {
        LegalConsentLandscapeContent(
            isEnabled = true,
            scrollState = rememberScrollState(),
            actionsScrollState = rememberScrollState(),
            onAction = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(
    name = "Legal consent landscape — tablet",
    widthDp = 1280,
    heightDp = 800,
)
@Composable
private fun LegalConsentLandscapeContentTabletPreview() {
    VartovyiTheme {
        LegalConsentLandscapeContent(
            isEnabled = true,
            scrollState = rememberScrollState(),
            actionsScrollState = rememberScrollState(),
            onAction = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
