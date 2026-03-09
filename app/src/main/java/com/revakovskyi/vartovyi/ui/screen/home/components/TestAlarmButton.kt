package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val TEST_BUTTON_MAX_WIDTH_DP = 450
private const val BORDER_STROKE_WIDTH_DP = 1

@Composable
fun TestAlarmButton(
    modifier: Modifier = Modifier,
    isAlarmRunning: Boolean,
    onClick: () -> Unit,
) {
    val buttonModifier = modifier
        .widthIn(max = TEST_BUTTON_MAX_WIDTH_DP.dp)
        .fillMaxWidth()
        .height(VartovyiTheme.spacing.massive)

    if (isAlarmRunning) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = VartovyiTheme.colors.errorContainer,
                contentColor = VartovyiTheme.colors.onErrorContainer,
            ),
            contentPadding = PaddingValues(VartovyiTheme.spacing.small),
            modifier = buttonModifier,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.alarm),
                contentDescription = null,
                modifier = Modifier.size(VartovyiTheme.spacing.large),
            )

            Spacer(modifier = Modifier.width(VartovyiTheme.spacing.medium))

            Text(
                text = stringResource(R.string.home_stop_test_alarm),
                style = VartovyiTheme.typography.titleMedium,
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            contentPadding = PaddingValues(VartovyiTheme.spacing.small),
            border = BorderStroke(
                width = BORDER_STROKE_WIDTH_DP.dp,
                color = VartovyiTheme.colors.error,
            ),
            modifier = buttonModifier,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.alarm),
                contentDescription = null,
                tint = VartovyiTheme.colors.error,
                modifier = Modifier.size(VartovyiTheme.spacing.large),
            )

            Spacer(modifier = Modifier.width(VartovyiTheme.spacing.medium))

            Text(
                text = stringResource(R.string.home_test_alarm),
                style = VartovyiTheme.typography.titleMedium,
                color = VartovyiTheme.colors.error,
            )
        }
    }
}

@Preview(name = "Idle")
@Composable
private fun PreviewTestAlarmButtonIdle() {
    VartovyiTheme {
        TestAlarmButton(
            isAlarmRunning = false,
            onClick = {},
        )
    }
}

@Preview(name = "Running")
@Composable
private fun PreviewTestAlarmButtonRunning() {
    VartovyiTheme {
        TestAlarmButton(
            isAlarmRunning = true,
            onClick = {},
        )
    }
}
