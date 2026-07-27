package com.revakovskyi.vartovyi.ui.alarm.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
internal fun AlarmDetails(
    modifier: Modifier = Modifier,
    sourceChannelName: String,
    sourceMessageText: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.alarm_title),
            style = VartovyiTheme.typography.headlineLarge,
            color = VartovyiTheme.colors.error,
            textAlign = TextAlign.Center,
        )

        if (sourceChannelName.isNotBlank() || sourceMessageText.isNotBlank()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = VartovyiTheme.spacing.medium)
                    .verticalScroll(rememberScrollState())
            ) {
                if (sourceChannelName.isNotBlank()) {
                    Text(
                        text = sourceChannelName,
                        style = VartovyiTheme.typography.titleLarge,
                        color = VartovyiTheme.colors.onPrimary,
                        textAlign = TextAlign.Center,
                    )
                }

                if (sourceMessageText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(VartovyiTheme.spacing.medium))

                    Text(
                        text = sourceMessageText,
                        style = VartovyiTheme.typography.bodyLarge,
                        color = VartovyiTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Alarm details — channel + message",
    showBackground = true,
    backgroundColor = 0xFF0D1117
)
@Composable
private fun AlarmDetailsPreview() {
    VartovyiTheme {
        AlarmDetails(
            sourceChannelName = "Тривога • Харків",
            sourceMessageText = "Увага! Повітряна небезпека в області, пройдіть в укриття.",
        )
    }
}

@Preview(name = "Alarm details — title only", showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
private fun AlarmDetailsTitleOnlyPreview() {
    VartovyiTheme {
        AlarmDetails(
            sourceChannelName = "",
            sourceMessageText = "",
        )
    }
}
