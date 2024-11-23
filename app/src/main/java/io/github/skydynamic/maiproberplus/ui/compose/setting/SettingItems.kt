package io.github.skydynamic.maiproberplus.ui.compose.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
@OptIn(ExperimentalMaterial3Api::class)
fun PasswordTextFiled(
    modifier: Modifier,
    label: @Composable (() -> Unit),
    icon: @Composable (() -> Unit),
    hidden: Boolean,
    value: String,
    onTrailingIconClick: () -> Unit,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = if (hidden) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
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
        },
        label = label,
        leadingIcon = icon
    )

    horizontalDivider()
}

@Composable
fun TextButtonItem(
    title: String,
    description: String = "",
    onClick: () -> Unit
) {
    TextButton(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        onClick = onClick
    ) {
        Column {
            Text(text = title, fontSize = 18.sp, color = getTitleFontColor())
            Text(text = description, fontSize = 12.sp, color = getDescFontColor())
        }
    }

    horizontalDivider()
}