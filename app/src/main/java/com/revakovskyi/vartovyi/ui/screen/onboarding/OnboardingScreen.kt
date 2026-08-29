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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.model.OnboardingPage
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.screen.onboarding.OnboardingUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingPageCity
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingPageTelegram
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingPageWelcome
import com.revakovskyi.vartovyi.ui.screen.onboarding.components.OnboardingProgressDots
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun OnboardingScreen(
    startPage: Int,
    viewModel: OnboardingViewModel = koinViewModel { parametersOf(startPage) },
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val skipHintMessage = stringResource(R.string.onboarding_skip_hint)

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is OnboardingUiContract.Event.Close -> onClose()
            is OnboardingUiContract.Event.ShowSkipHint -> {
                SnackbarController.sendEvent(SnackbarEvent(message = skipHintMessage))
            }
        }
    }

    OnboardingContent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun OnboardingContent(
    modifier: Modifier = Modifier,
    state: OnboardingUiContract.State,
    onAction: (action: Action) -> Unit,
) {
    val windowSize = LocalWindowInfo.current.containerSize
    val focusManager = LocalFocusManager.current

    val isLandscape = windowSize.width > windowSize.height

    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.totalPages },
    )

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(Action.PageChanged(pagerState.currentPage))

        if (state.pages.getOrNull(pagerState.currentPage) != OnboardingPage.CITY) {
            focusManager.clearFocus()
        }
    }

    // Portrait keeps the tested-working behavior: the whole screen shrinks for the keyboard via
    // imePadding, so nav buttons stay visible above it. Landscape has too little height for that
    // to leave room for the input field, so there imePadding is applied locally, only inside the
    // city page (see OnboardingPageCity) — the rest of this screen stays put and ends up covered
    // by the keyboard instead, which is fine since none of it is needed while typing a city name
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxSize()
            .background(VartovyiTheme.colors.background)
            .systemBarsPadding()
            .then(if (isLandscape) Modifier else Modifier.imePadding())
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                when (state.pages.getOrNull(page)) {
                    OnboardingPage.WELCOME -> OnboardingPageWelcome()

                    OnboardingPage.CITY -> {
                        OnboardingPageCity(
                            cityInput = state.cityInput,
                            onValueChange = { value ->
                                onAction(Action.UpdateCityInput(value))
                            },
                        )
                    }

                    OnboardingPage.TELEGRAM -> OnboardingPageTelegram()

                    null -> OnboardingPageWelcome()
                }
            }

            OnboardingProgressDots(
                currentPage = state.currentPage,
                totalPages = state.totalPages,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        top =
                            if (isLandscape) VartovyiTheme.spacing.small
                            else VartovyiTheme.spacing.large,
                        bottom =
                            if (isLandscape) VartovyiTheme.spacing.medium
                            else VartovyiTheme.spacing.huge,
                    ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                    .fillMaxWidth()
                    .padding(horizontal = VartovyiTheme.spacing.medium),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (state.currentPage > 0) {
                        VartovyiActionButton(
                            text = stringResource(R.string.onboarding_back),
                            onClick = { onAction(Action.PreviousPage) },
                            style = VartovyiActionButtonStyle.Outlined,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(VartovyiTheme.spacing.medium))

                Box(modifier = Modifier.weight(1f)) {
                    VartovyiActionButton(
                        text = stringResource(state.primaryAction.labelResId()),
                        onClick = { onAction(state.primaryAction.toAction()) },
                        style = VartovyiActionButtonStyle.Filled,
                    )
                }
            }

            Text(
                text = stringResource(R.string.onboarding_skip),
                style = VartovyiTheme.typography.bodyMedium,
                color = VartovyiTheme.colors.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        top =
                            if (isLandscape) VartovyiTheme.spacing.small
                            else VartovyiTheme.spacing.standard
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onAction(Action.Skip) }
                    ),
            )

            Spacer(
                modifier = Modifier
                    .height(
                        if (isLandscape) VartovyiTheme.spacing.small
                        else VartovyiTheme.spacing.extraLarge
                    ),
            )
        }
    }
}

private fun OnboardingUiContract.PrimaryAction.labelResId(): Int =
    when (this) {
        OnboardingUiContract.PrimaryAction.COMPLETE -> R.string.onboarding_complete
        OnboardingUiContract.PrimaryAction.SKIP_CITY -> R.string.onboarding_skip
        OnboardingUiContract.PrimaryAction.NEXT,
        OnboardingUiContract.PrimaryAction.SUBMIT_CITY,
            -> R.string.onboarding_next
    }

private fun OnboardingUiContract.PrimaryAction.toAction(): Action =
    when (this) {
        OnboardingUiContract.PrimaryAction.NEXT -> Action.NextPage
        OnboardingUiContract.PrimaryAction.COMPLETE -> Action.Complete
        OnboardingUiContract.PrimaryAction.SUBMIT_CITY -> Action.SubmitCity
        OnboardingUiContract.PrimaryAction.SKIP_CITY -> Action.SkipCity
    }

@Preview(showBackground = true)
@Composable
private fun OnboardingContentFirstPagePreview() {
    VartovyiTheme {
        OnboardingContent(
            state = OnboardingUiContract.State(currentPage = 0),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentCityPagePreview() {
    VartovyiTheme {
        OnboardingContent(
            state = OnboardingUiContract.State(currentPage = 1),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentLastPagePreview() {
    VartovyiTheme {
        OnboardingContent(
            state = OnboardingUiContract.State(currentPage = 2),
            onAction = {},
        )
    }
}
