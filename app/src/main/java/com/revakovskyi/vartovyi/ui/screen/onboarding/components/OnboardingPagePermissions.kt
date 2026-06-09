package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.model.PermissionsStatus
import com.revakovskyi.vartovyi.ui.components.PulsingDot
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val GRANTED_CHECK_MARK = "✓"

@Composable
fun OnboardingPagePermissions(
    modifier: Modifier = Modifier,
    permissionsStatus: PermissionsStatus,
    isRecommendedGranted: Boolean,
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

    val isMandatoryGranted = permissionsStatus != PermissionsStatus.MANDATORY_MISSING

    OnboardingPageLayout(
        visual = OnboardingVisual.VectorIcon(
            imageVector = imageVector,
            tint = imageTint,
        ),
        title = stringResource(R.string.onboarding_permissions_title),
        bodyContent = {
            PermissionsListContent(
                isMandatoryGranted = isMandatoryGranted,
                isRecommendedGranted = isRecommendedGranted,
            )
        },
        actionContent = {
            VartovyiActionButton(
                text = stringResource(R.string.onboarding_open_permissions),
                onClick = onOpenPermissions,
                style = VartovyiActionButtonStyle.Outlined,
                borderColor = VartovyiTheme.colors.primary,
            )
        },
        modifier = modifier
    )
}

@Composable
private fun PermissionsListContent(
    modifier: Modifier = Modifier,
    isMandatoryGranted: Boolean,
    isRecommendedGranted: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraSmall),
        modifier = modifier.fillMaxWidth()
    ) {
        if (isMandatoryGranted && isRecommendedGranted) {
            PermissionsGrantedRow(
                text = stringResource(R.string.onboarding_permissions_all_granted),
            )
        } else {
            Text(
                text = stringResource(R.string.onboarding_permissions_footer),
                style = VartovyiTheme.typography.bodyLarge,
                color = VartovyiTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

            MandatorySection(isGranted = isMandatoryGranted)

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

            RecommendedSection(isGranted = isRecommendedGranted)
        }
    }
}

@Composable
private fun MandatorySection(isGranted: Boolean) {
    if (isGranted) {
        PermissionsGrantedRow(
            text = stringResource(R.string.onboarding_permissions_mandatory_granted),
        )
    } else {
        PermissionsSectionHeader(
            text = stringResource(R.string.onboarding_permissions_mandatory_header),
            color = VartovyiTheme.colors.error,
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraSmall))

        PermissionBulletItem(text = stringResource(R.string.permissions_listener_title))

        PermissionBulletItem(text = stringResource(R.string.permissions_battery_title))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionBulletItem(text = stringResource(R.string.permissions_post_notifications_title))
        }
    }
}

@Composable
private fun RecommendedSection(isGranted: Boolean) {
    if (isGranted) {
        PermissionsGrantedRow(
            text = stringResource(R.string.onboarding_permissions_recommended_granted),
        )
    } else {
        PermissionsSectionHeader(
            text = stringResource(R.string.onboarding_permissions_recommended_header),
            color = VartovyiTheme.colors.secondary,
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraSmall))

        PermissionBulletItem(text = stringResource(R.string.permissions_dnd_title))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            PermissionBulletItem(text = stringResource(R.string.permissions_full_screen_title))
        }
    }
}

@Composable
private fun PermissionsSectionHeader(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        PulsingDot(color = color)

        Text(
            text = text,
            style = VartovyiTheme.typography.titleMedium,
            color = color,
        )
    }
}

@Composable
private fun PermissionsGrantedRow(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        text = "$GRANTED_CHECK_MARK $text",
        style = VartovyiTheme.typography.titleMedium,
        color = VartovyiTheme.colors.primary,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun PermissionBulletItem(
    text: String,
) {
    Text(
        text = "• $text",
        style = VartovyiTheme.typography.bodyLarge,
        color = VartovyiTheme.colors.onSurface,
    )
}

@Preview(showBackground = true, name = "Mandatory missing (red)")
@Composable
private fun OnboardingPagePermissionsMandatoryMissingPreview() {
    VartovyiTheme {
        OnboardingPagePermissions(
            permissionsStatus = PermissionsStatus.MANDATORY_MISSING,
            isRecommendedGranted = false,
            onOpenPermissions = {},
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}

@Preview(showBackground = true, name = "Mandatory missing, recommended granted")
@Composable
private fun OnboardingPagePermissionsMandatoryMissingRecommendedGrantedPreview() {
    VartovyiTheme {
        OnboardingPagePermissions(
            permissionsStatus = PermissionsStatus.MANDATORY_MISSING,
            isRecommendedGranted = true,
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
            isRecommendedGranted = false,
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
            isRecommendedGranted = true,
            onOpenPermissions = {},
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}
