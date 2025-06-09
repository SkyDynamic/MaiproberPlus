package io.github.skydynamic.maiproberplus.ui.compose.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiproberplus.ui.theme.getCardColor

@Composable
fun SettingItemGroup(
    modifier: Modifier,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = getCardColor())
    ) {
        Column(
            modifier = modifier
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.W600,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            color = Color(135, 206, 250)
                        )
                    ) {
                        append(title)
                    }
                }
            )
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.Start,
                content = content
            )
        }
    }
}