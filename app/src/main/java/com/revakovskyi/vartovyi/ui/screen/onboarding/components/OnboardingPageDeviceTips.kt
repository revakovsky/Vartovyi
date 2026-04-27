package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun OnboardingPageDeviceTips(
    modifier: Modifier = Modifier,
) {
    val body = stringResource(R.string.onboarding_device_tips_body)

    OnboardingPageLayout(
        visual = OnboardingVisual.VectorIcon(
            imageVector = ImageVector.vectorResource(R.drawable.devices),
            tint = VartovyiTheme.colors.primary,
        ),
        title = stringResource(R.string.onboarding_device_tips_title),
        bodyContent = { DeviceTipsBody(body = body) },
        modifier = modifier
    )
}

@Composable
private fun DeviceTipsBody(body: String) {
    val headerSpanStyle = SpanStyle(
        fontWeight = VartovyiTheme.typography.titleMedium.fontWeight,
    )
    val stepFontSize = VartovyiTheme.typography.bodyMedium.fontSize

    val annotatedBody = buildAnnotatedString {
        val blocks = body.split("\n\n")

        blocks.forEachIndexed { blockIndex, block ->
            if (blockIndex > 0) append("\n\n")

            val isIntroBlock = blockIndex == 0
            val isTipBlock = block.startsWith("💡")

            if (isIntroBlock || isTipBlock) {
                append(block)
            } else {
                val lines = block.split("\n")

                lines.forEachIndexed { lineIndex, line ->
                    if (lineIndex > 0) append("\n")

                    when {
                        lineIndex == 0 -> withStyle(style = headerSpanStyle) { append(line) }
                        line.startsWith("•") -> withStyle(style = SpanStyle(fontSize = stepFontSize)) {
                            append(
                                line
                            )
                        }

                        else -> append(line)
                    }
                }
            }
        }
    }

    Text(
        text = annotatedBody,
        style = VartovyiTheme.typography.bodyLarge,
        color = VartovyiTheme.colors.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPageDeviceTipsPreview() {
    VartovyiTheme {
        OnboardingPageDeviceTips(
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}
