package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun OnboardingPagePermissions(
    modifier: Modifier = Modifier,
    arePermissionsGranted: Boolean,
    onOpenPermissions: () -> Unit,
) {
    val imageVector = ImageVector.vectorResource(
        if (arePermissionsGranted) R.drawable.security_green
        else R.drawable.security_red
    )

    val imageTint = if (arePermissionsGranted) {
        VartovyiTheme.colors.primary
    } else {
        VartovyiTheme.colors.error
    }

    OnboardingPageLayout(
        visual = OnboardingVisual.VectorIcon(
            imageVector = imageVector,
            tint = imageTint,
        ),
        title = stringResource(R.string.onboarding_permissions_title),
        body = stringResource(R.string.onboarding_permissions_body),
        actionContent = {
            VartovyiActionButton(
                text = stringResource(R.string.onboarding_open_permissions),
                onClick = onOpenPermissions,
                style = VartovyiActionButtonStyle.Outlined,
            )
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPagePermissionsNotGrantedPreview() {
    VartovyiTheme {
        OnboardingPagePermissions(
            arePermissionsGranted = false,
            onOpenPermissions = {},
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPagePermissionsGrantedPreview() {
    VartovyiTheme {
        OnboardingPagePermissions(
            arePermissionsGranted = true,
            onOpenPermissions = {},
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}
