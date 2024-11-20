package io.github.skydynamic.maiproberplus.ui.compose.setting

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiproberplus.R

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
}