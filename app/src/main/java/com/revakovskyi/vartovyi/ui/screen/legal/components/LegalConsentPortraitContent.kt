package com.revakovskyi.vartovyi.ui.screen.legal.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
internal fun LegalConsentPortraitContent(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    scrollState: ScrollState,
    onAction: (action: LegalConsentUiContract.Action) -> Unit,
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.weight(1f)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
                    modifier = Modifier
                        .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                        .padding(horizontal = VartovyiTheme.spacing.medium)
                        .padding(
                            top = VartovyiTheme.spacing.massive,
                            bottom = VartovyiTheme.spacing.standard,
                        )
                ) {
                    LegalConsentTexts()

                    LegalConsentDocumentButtons(
                        isEnabled = isEnabled,
                        onAction = onAction,
                    )
                }
            }

            ScrollProgressBar(
                scrollState = scrollState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        LegalConsentDecisionButtons(
            isEnabled = isEnabled,
            onAction = onAction,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                .fillMaxWidth()
                .padding(horizontal = VartovyiTheme.spacing.medium)
                .padding(bottom = VartovyiTheme.spacing.medium)
        )
    }
}

@Preview(name = "Legal consent portrait", widthDp = 360, heightDp = 800)
@Composable
private fun LegalConsentPortraitContentPreview() {
    VartovyiTheme {
        LegalConsentPortraitContent(
            isEnabled = true,
            scrollState = rememberScrollState(),
            onAction = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
