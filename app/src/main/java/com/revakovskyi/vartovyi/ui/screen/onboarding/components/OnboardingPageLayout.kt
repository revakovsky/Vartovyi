package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
fun OnboardingPageLayout(
    modifier: Modifier = Modifier,
    visual: OnboardingVisual,
    title: String,
    body: String = "",
    bodyContent: (@Composable () -> Unit)? = null,
    actionContent: (@Composable () -> Unit)? = null,
) {
    val windowSize = LocalWindowInfo.current.containerSize

    val isTwoPaneLayout = windowSize.width > windowSize.height

    if (isTwoPaneLayout) {
        OnboardingPageLandscapeLayout(
            visual = visual,
            title = title,
            body = body,
            bodyContent = bodyContent,
            actionContent = actionContent,
            modifier = modifier
        )
    } else {
        OnboardingPagePortraitLayout(
            visual = visual,
            title = title,
            body = body,
            bodyContent = bodyContent,
            actionContent = actionContent,
            modifier = modifier
        )
    }
}
