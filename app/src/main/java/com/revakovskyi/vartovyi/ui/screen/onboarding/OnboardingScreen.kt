package com.revakovskyi.vartovyi.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingPageDeviceTips
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingPageKeywords
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingPageLaunch
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingPagePermissions
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingPageTelegram
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingPageWelcome
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingProgressDots
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import org.koin.compose.viewmodel.koinViewModel

private enum class OnboardingPage {
    WELCOME, TELEGRAM, PERMISSIONS, KEYWORDS, DEVICE_TIPS, LAUNCH,
}

@Composable
fun OnboardingScreen(
    isRequiredPermissionsGranted: Boolean,
    viewModel: OnboardingViewModel = koinViewModel(),
    onClose: () -> Unit,
    onOpenPermissions: () -> Unit,
    onOpenKeywords: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is OnboardingUiContract.Event.Close -> onClose()
            is OnboardingUiContract.Event.OpenPermissions -> onOpenPermissions()
            is OnboardingUiContract.Event.OpenKeywords -> onOpenKeywords()
        }
    }

    OnboardingContent(
        state = state,
        isRequiredPermissionsGranted = isRequiredPermissionsGranted,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun OnboardingContent(
    modifier: Modifier = Modifier,
    state: OnboardingUiContract.State,
    isRequiredPermissionsGranted: Boolean,
    onAction: (action: OnboardingUiContract.Action) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { state.totalPages })

    val isLastPage = state.currentPage == state.totalPages - 1

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(OnboardingUiContract.Action.PageChanged(pagerState.currentPage))
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxSize()
            .background(VartovyiTheme.colors.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = modifier
                .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                .fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                when (OnboardingPage.entries.getOrNull(page)) {
                    OnboardingPage.WELCOME -> OnboardingPageWelcome()
                    OnboardingPage.TELEGRAM -> OnboardingPageTelegram()

                    OnboardingPage.PERMISSIONS -> {
                        OnboardingPagePermissions(
                            arePermissionsGranted = isRequiredPermissionsGranted,
                            onOpenPermissions = { onAction(OnboardingUiContract.Action.OpenPermissions) },
                        )
                    }

                    OnboardingPage.DEVICE_TIPS -> OnboardingPageDeviceTips()

                    OnboardingPage.KEYWORDS -> {
                        OnboardingPageKeywords(
                            onOpenKeywords = { onAction(OnboardingUiContract.Action.OpenKeywords) }
                        )
                    }

                    OnboardingPage.LAUNCH -> OnboardingPageLaunch()
                    null -> OnboardingPageWelcome()
                }
            }

            OnboardingProgressDots(
                currentPage = state.currentPage,
                totalPages = state.totalPages,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        top = VartovyiTheme.spacing.large,
                        bottom = VartovyiTheme.spacing.huge,
                    ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = VartovyiTheme.spacing.medium),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (state.currentPage > 0) {
                        VartovyiActionButton(
                            text = stringResource(R.string.onboarding_back),
                            onClick = { onAction(OnboardingUiContract.Action.PreviousPage) },
                            style = VartovyiActionButtonStyle.Outlined,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(VartovyiTheme.spacing.medium))

                Box(modifier = Modifier.weight(1f)) {
                    VartovyiActionButton(
                        text = stringResource(
                            if (isLastPage) R.string.onboarding_complete
                            else R.string.onboarding_next
                        ),
                        onClick = {
                            if (isLastPage) onAction(OnboardingUiContract.Action.Complete)
                            else onAction(OnboardingUiContract.Action.NextPage)
                        },
                        style = VartovyiActionButtonStyle.Filled,
                    )
                }
            }

            if (state.canSkip) {
                Text(
                    text = stringResource(R.string.onboarding_skip),
                    style = VartovyiTheme.typography.bodyMedium,
                    color = VartovyiTheme.colors.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = VartovyiTheme.spacing.standard)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onAction(OnboardingUiContract.Action.Skip) }
                        ),
                )
            }

            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(VartovyiTheme.spacing.extraLarge),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentFirstPagePreview() {
    VartovyiTheme {
        OnboardingContent(
            state = OnboardingUiContract.State(currentPage = 0),
            isRequiredPermissionsGranted = false,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentPermissionsNotGrantedPreview() {
    VartovyiTheme {
        OnboardingContent(
            state = OnboardingUiContract.State(currentPage = 2),
            isRequiredPermissionsGranted = false,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentPermissionsGrantedPreview() {
    VartovyiTheme {
        OnboardingContent(
            state = OnboardingUiContract.State(currentPage = 2),
            isRequiredPermissionsGranted = true,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentDeviceTipsPreview() {
    VartovyiTheme {
        OnboardingContent(
            state = OnboardingUiContract.State(currentPage = 4),
            isRequiredPermissionsGranted = true,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentLastPagePreview() {
    VartovyiTheme {
        OnboardingContent(
            state = OnboardingUiContract.State(currentPage = 5),
            isRequiredPermissionsGranted = true,
            onAction = {},
        )
    }
}
