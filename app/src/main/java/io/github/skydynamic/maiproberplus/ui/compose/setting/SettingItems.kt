package io.github.skydynamic.maiproberplus.ui.compose.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.skydynamic.maiproberplus.R
import io.github.skydynamic.maiproberplus.ui.theme.getDescFontColor
import io.github.skydynamic.maiproberplus.ui.theme.getTitleFontColor

val horizontalDivider = @Composable {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.LightGray,
        thickness = 1.dp
    )
}

@Composable
fun BaseTextItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String = "",
) {
    Column(
        modifier = modifier
    ) {
        Text(text = title, fontSize = 18.sp, color = getTitleFontColor())
        Text(
            text = description,
            fontSize = 12.sp,
            color = getDescFontColor(),
            style = TextStyle.Default
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PasswordTextFiled(
    modifier: Modifier,
    label: @Composable (() -> Unit),
    icon: @Composable (() -> Unit),
    hidden: Boolean,
    value: String,
    onTrailingIconClick: () -> Unit,
    onValueChange: (String) -> Unit,
    enable: Boolean = true
) {
    OutlinedTextField(
        modifier = modifier,
        enabled = enable,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = if (hidden) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            Row {
                if (!value.isEmpty()) {
                    IconButton(
                        onClick = {
                            onValueChange("")
                        }
                    ) {
                        Icon(Icons.Default.Clear, null)
                    }
                }
                IconButton(
                    onClick = onTrailingIconClick
                ) {
                    if (hidden) Icon(
                        painterResource(R.drawable.visibility_24px),
                        null,
                        modifier = Modifier.size(28.dp)
                    )
                    else Icon(
                        painterResource(R.drawable.visibility_off_24px),
                        null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        label = label,
        leadingIcon = icon
    )

    horizontalDivider()
}

@Composable
fun TextButtonItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(
                onClick = onClick
            )
    ) {
        BaseTextItem(
            title = title,
            description = description
        )
    }

    horizontalDivider()
}

@Composable
fun SwitchSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(checked) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = 15.dp, top = 8.dp, end = 8.dp, bottom = 5.dp)
    ) {
        BaseTextItem(
            modifier = Modifier.weight(3f),
            title = title,
            description = description
        )

        Spacer(modifier = Modifier.weight(1.0f))

        Switch(
            checked = checked,
            onCheckedChange = {
                checked = !checked
                onCheckedChange(checked)
            },
            thumbContent = if (checked) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else {
                null
            }
        )
    }

    horizontalDivider()
}