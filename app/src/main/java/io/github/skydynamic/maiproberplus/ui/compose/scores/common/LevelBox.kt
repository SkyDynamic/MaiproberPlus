package io.github.skydynamic.maiproberplus.ui.compose.scores.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LevelBox(
    level: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(40.dp)
            .height(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(43, 38, 38, 255))
    ) {
        Text(
            text = "$level",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(139, 132, 132, 255),
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center)
                .padding(horizontal = 2.dp),
        )
    }
}
