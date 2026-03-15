package com.revakovskyi.vartovyi.ui.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun VartovyiSwitch(
    checked: Boolean,
    onCheckedChange: (isChecked: Boolean) -> Unit,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            uncheckedThumbColor = VartovyiTheme.colors.outline,
            uncheckedTrackColor = VartovyiTheme.colors.surfaceVariant,
            uncheckedBorderColor = VartovyiTheme.colors.outline,
        ),
    )
}

@Preview(name = "VartovyiSwitch - checked")
@Composable
private fun VartovyiSwitchCheckedPreview() {
    VartovyiTheme {
        VartovyiSwitch(
            checked = true,
            onCheckedChange = {},
        )
    }
}

@Preview(name = "VartovyiSwitch - unchecked")
@Composable
private fun VartovyiSwitchUncheckedPreview() {
    VartovyiTheme {
        VartovyiSwitch(
            checked = false,
            onCheckedChange = {},
        )
    }
}
