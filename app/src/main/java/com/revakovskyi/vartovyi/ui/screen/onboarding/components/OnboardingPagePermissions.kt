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
import com.revakovskyi.vartovyi.model.PermissionsStatus
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun OnboardingPagePermissions(
    modifier: Modifier = Modifier,
    permissionsStatus: PermissionsStatus,
    onOpenPermissions: () -> Unit,
) {
    val imageVector = ImageVector.vectorResource(
        when (permissionsStatus) {
            PermissionsStatus.MANDATORY_MISSING -> R.drawable.security_red
            PermissionsStatus.RECOMMENDED_MISSING,
            PermissionsStatus.GRANTED -> R.drawable.security_green
        }
    )

    val imageTint = when (permissionsStatus) {
        PermissionsStatus.GRANTED -> VartovyiTheme.colors.primary
        PermissionsStatus.RECOMMENDED_MISSING -> VartovyiTheme.colors.secondary
        PermissionsStatus.MANDATORY_MISSING -> VartovyiTheme.colors.error
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

@Preview(showBackground = true, name = "Mandatory missing (red)")
@Composable
private fun OnboardingPagePermissionsMandatoryMissingPreview() {
    VartovyiTheme {
        OnboardingPagePermissions(
            permissionsStatus = PermissionsStatus.MANDATORY_MISSING,
            onOpenPermissions = {},
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}

@Preview(showBackground = true, name = "Recommended missing (orange)")
@Composable
private fun OnboardingPagePermissionsRecommendedMissingPreview() {
    VartovyiTheme {
        OnboardingPagePermissions(
            permissionsStatus = PermissionsStatus.RECOMMENDED_MISSING,
            onOpenPermissions = {},
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}

@Preview(showBackground = true, name = "All granted (green)")
@Composable
private fun OnboardingPagePermissionsGrantedPreview() {
    VartovyiTheme {
        OnboardingPagePermissions(
            permissionsStatus = PermissionsStatus.GRANTED,
            onOpenPermissions = {},
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}
