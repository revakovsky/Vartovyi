package com.revakovskyi.vartovyi.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.revakovskyi.vartovyi.R

val RobotoFontFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold),
)

val RobotoMonoFontFamily = FontFamily(
    Font(R.font.robotomono_regular, FontWeight.Normal),
)

val VartovyiTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
)

@Preview(
    name = "Typography scale",
    widthDp = 360,
    heightDp = 800,
    showBackground = true,
    backgroundColor = 0xFF0D1117,
)
@Composable
private fun TypographyPreview() {
    VartovyiTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraSmall),
            modifier = Modifier
                .background(VartovyiTheme.colors.background)
                .padding(VartovyiTheme.spacing.standard)
        ) {
            TypographyRow(
                name = "headlineLarge",
                sample = "Ракета  32sp Bold",
                style = @Composable {
                    Text(
                        text = "Ракета",
                        style = VartovyiTheme.typography.headlineLarge,
                        color = VartovyiTheme.colors.onBackground
                    )
                },
            )

            HorizontalDivider(color = VartovyiTheme.colors.outline)

            TypographyRow(
                name = "headlineSmall",
                sample = "24sp Bold",
                style = @Composable {
                    Text(
                        text = "Вибух",
                        style = VartovyiTheme.typography.headlineSmall,
                        color = VartovyiTheme.colors.onBackground
                    )
                },
            )

            HorizontalDivider(color = VartovyiTheme.colors.outline)

            TypographyRow(
                name = "titleLarge",
                sample = "20sp Medium",
                style = @Composable {
                    Text(
                        text = "Моніторинг",
                        style = VartovyiTheme.typography.titleLarge,
                        color = VartovyiTheme.colors.onBackground
                    )
                },
            )

            HorizontalDivider(color = VartovyiTheme.colors.outline)

            TypographyRow(
                name = "titleMedium",
                sample = "16sp Medium",
                style = @Composable {
                    Text(
                        text = "Ключові слова",
                        style = VartovyiTheme.typography.titleMedium,
                        color = VartovyiTheme.colors.onBackground
                    )
                },
            )

            HorizontalDivider(color = VartovyiTheme.colors.outline)

            TypographyRow(
                name = "bodyLarge",
                sample = "16sp Normal",
                style = @Composable {
                    Text(
                        text = "Основний текст",
                        style = VartovyiTheme.typography.bodyLarge,
                        color = VartovyiTheme.colors.onBackground
                    )
                },
            )

            HorizontalDivider(color = VartovyiTheme.colors.outline)

            TypographyRow(
                name = "bodyMedium",
                sample = "14sp Normal",
                style = @Composable {
                    Text(
                        text = "Повідомлення",
                        style = VartovyiTheme.typography.bodyMedium,
                        color = VartovyiTheme.colors.onBackground
                    )
                },
            )

            HorizontalDivider(color = VartovyiTheme.colors.outline)

            TypographyRow(
                name = "bodySmall",
                sample = "12sp Normal",
                style = @Composable {
                    Text(
                        text = "Деталі події",
                        style = VartovyiTheme.typography.bodySmall,
                        color = VartovyiTheme.colors.onBackground
                    )
                },
            )

            HorizontalDivider(color = VartovyiTheme.colors.outline)

            TypographyRow(
                name = "labelLarge",
                sample = "14sp Medium",
                style = @Composable {
                    Text(
                        text = "Відправник",
                        style = VartovyiTheme.typography.labelLarge,
                        color = VartovyiTheme.colors.onBackground
                    )
                },
            )

            HorizontalDivider(color = VartovyiTheme.colors.outline)

            TypographyRow(
                name = "labelMedium",
                sample = "13sp Medium",
                style = @Composable {
                    Text(
                        text = "тривога",
                        style = VartovyiTheme.typography.labelMedium,
                        color = VartovyiTheme.colors.onBackground
                    )
                },
            )

            HorizontalDivider(color = VartovyiTheme.colors.outline)

            TypographyRow(
                name = "labelSmall",
                sample = "12sp Mono",
                style = @Composable {
                    Text(
                        text = "22:15:04",
                        style = VartovyiTheme.typography.labelSmall,
                        color = VartovyiTheme.colors.onBackground
                    )
                },
            )
        }
    }
}

@Composable
private fun TypographyRow(
    name: String,
    sample: String,
    style: @Composable () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = VartovyiTheme.spacing.extraSmall)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            style()
        }

        Text(
            text = "$name\n$sample",
            style = VartovyiTheme.typography.labelSmall,
            color = VartovyiTheme.colors.onSurfaceVariant,
        )
    }
}
