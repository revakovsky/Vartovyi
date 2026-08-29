package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.WordInputRow
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun OnboardingPageCity(
    cityInput: String,
    modifier: Modifier = Modifier,
    onValueChange: (value: String) -> Unit,
) {
    OnboardingPageLayout(
        visual = OnboardingVisual.VectorIcon(
            imageVector = ImageVector.vectorResource(R.drawable.location),
            tint = VartovyiTheme.colors.primary,
        ),
        title = stringResource(R.string.onboarding_city_title),
        bodyContent = {
            CityBodyContent(
                cityInput = cityInput,
                onValueChange = onValueChange,
            )
        },
        modifier = modifier,
    )
}

@Composable
private fun CityBodyContent(
    modifier: Modifier = Modifier,
    cityInput: String,
    onValueChange: (value: String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Scoped to this page only: lets the input field ride up above the keyboard without
    // affecting the rest of the onboarding screen (nav buttons, progress dots), which stays put
    // and ends up covered by the keyboard instead
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraLarge),
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        Text(
            text = stringResource(R.string.onboarding_city_explanation),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        WordInputRow(
            value = cityInput,
            hint = stringResource(R.string.onboarding_city_hint),
            showAddButton = false,
            onAdd = {
                keyboardController?.hide()
                focusManager.clearFocus()
            },
            onClear = { onValueChange("") },
            onValueChange = { value -> onValueChange(value.capitalizeFirstChar()) },
        )
    }
}

private fun String.capitalizeFirstChar(): String =
    replaceFirstChar { firstChar -> firstChar.uppercase() }

@Preview(showBackground = true)
@Composable
private fun OnboardingPageCityEmptyPreview() {
    VartovyiTheme {
        OnboardingPageCity(
            cityInput = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPageCityFilledPreview() {
    VartovyiTheme {
        OnboardingPageCity(
            cityInput = "Харків",
            onValueChange = {},
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}
