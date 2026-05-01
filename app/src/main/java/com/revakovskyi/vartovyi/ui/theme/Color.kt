package com.revakovskyi.vartovyi.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val BackgroundDark = Color(0xFF0D1117)
val SurfaceDark = Color(0xFF161B22)
val SurfaceVariantDark = Color(0xFF21262D)

val PrimaryDark = Color(0xFF2EA043)
val PrimaryContainerDark = Color(0xFF1B4D2E)
val OnPrimaryDark = Color(0xFFFFFFFF)
val OnPrimaryContainerDark = Color(0xFFB7E4C7)

val SecondaryDark = Color(0xFFD29922)
val SecondaryContainerDark = Color(0xFF3B2A00)
val OnSecondaryDark = Color(0xFF1A1200)
val OnSecondaryContainerDark = Color(0xFFFFD95A)

val TertiaryDark = Color(0xFF58A6FF)
val TertiaryContainerDark = Color(0xFF0D2A4A)
val OnTertiaryDark = Color(0xFF003060)
val OnTertiaryContainerDark = Color(0xFFADD6FF)

val ErrorDark = Color(0xFFF85149)
val ErrorContainerDark = Color(0xFF5C1D1D)
val OnErrorDark = Color(0xFFFFFFFF)
val OnErrorContainerDark = Color(0xFFFFBAB5)

val OnBackgroundDark = Color(0xFFE6EDF3)
val OnSurfaceDark = Color(0xFFE6EDF3)
val OnSurfaceVariantDark = Color(0xFF8B949E)

val OutlineDark = Color(0xFF30363D)
val OutlineVariantDark = Color(0xFF21262D)

@Preview(
    name = "Color scale",
    widthDp = 360,
    heightDp = 960,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun ColorPreview() {
    VartovyiTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
            modifier = Modifier
                .background(VartovyiTheme.colors.background)
                .padding(VartovyiTheme.spacing.standard),
        ) {
            ColorGroupTitle("Primary")

            Row(horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small)) {
                ColorSwatch(
                    name = "primary",
                    color = VartovyiTheme.colors.primary,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "onPrimary",
                    color = VartovyiTheme.colors.onPrimary,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "primaryContainer",
                    color = VartovyiTheme.colors.primaryContainer,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "onPrimaryContainer",
                    color = VartovyiTheme.colors.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            ColorGroupTitle("Secondary")

            Row(horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small)) {
                ColorSwatch(
                    name = "secondary",
                    color = VartovyiTheme.colors.secondary,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "onSecondary",
                    color = VartovyiTheme.colors.onSecondary,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "secondaryContainer",
                    color = VartovyiTheme.colors.secondaryContainer,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "onSecondaryContainer",
                    color = VartovyiTheme.colors.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            ColorGroupTitle("Tertiary")

            Row(horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small)) {
                ColorSwatch(
                    name = "tertiary",
                    color = VartovyiTheme.colors.tertiary,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "onTertiary",
                    color = VartovyiTheme.colors.onTertiary,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "tertiaryContainer",
                    color = VartovyiTheme.colors.tertiaryContainer,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "onTertiaryContainer",
                    color = VartovyiTheme.colors.onTertiaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            ColorGroupTitle("Error")

            Row(horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small)) {
                ColorSwatch(
                    name = "error",
                    color = VartovyiTheme.colors.error,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "onError",
                    color = VartovyiTheme.colors.onError,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "errorContainer",
                    color = VartovyiTheme.colors.errorContainer,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "onErrorContainer",
                    color = VartovyiTheme.colors.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            ColorGroupTitle("Surface & Background")

            Row(horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small)) {
                ColorSwatch(
                    name = "background",
                    color = VartovyiTheme.colors.background,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "onBackground",
                    color = VartovyiTheme.colors.onBackground,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "surface",
                    color = VartovyiTheme.colors.surface,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small)) {
                ColorSwatch(
                    name = "onSurface",
                    color = VartovyiTheme.colors.onSurface,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "surfaceVariant",
                    color = VartovyiTheme.colors.surfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "onSurfaceVariant",
                    color = VartovyiTheme.colors.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }

            ColorGroupTitle("Outline")

            Row(horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small)) {
                ColorSwatch(
                    name = "outline",
                    color = VartovyiTheme.colors.outline,
                    modifier = Modifier.weight(1f)
                )

                ColorSwatch(
                    name = "outlineVariant",
                    color = VartovyiTheme.colors.outlineVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ColorGroupTitle(title: String) {
    Text(
        text = title,
        style = VartovyiTheme.typography.labelMedium,
        color = VartovyiTheme.colors.onSurfaceVariant,
    )
}

@Composable
fun ColorSwatch(
    modifier: Modifier = Modifier,
    name: String,
    color: Color,
) {
    val hex = "#%06X".format(color.toArgb() and 0xFFFFFF)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Surface(
            color = color,
            shape = VartovyiTheme.shapes.medium,
            border = BorderStroke(
                width = 1.dp,
                color = VartovyiTheme.colors.outline,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(VartovyiTheme.spacing.massive)
        ) {}

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraSmall))

        Text(
            text = name,
            style = VartovyiTheme.typography.labelSmall,
            color = VartovyiTheme.colors.onBackground,
            textAlign = TextAlign.Center,
        )

        Text(
            text = hex,
            style = VartovyiTheme.typography.labelSmall,
            color = VartovyiTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
