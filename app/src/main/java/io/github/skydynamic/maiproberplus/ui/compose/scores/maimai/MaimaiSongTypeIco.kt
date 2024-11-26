package io.github.skydynamic.maiproberplus.ui.compose.scores.maimai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
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
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums

@Composable
fun MaimaiSongTypeIco(
    type: MaimaiEnums.SongType,
    modifier: Modifier = Modifier,
) {
    val color = if (type == MaimaiEnums.SongType.DX) {
        Color(255, 87, 34, 255)
    } else {
        Color(63, 81, 181, 255)
    }
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.align(Alignment.Center)
                .width(30.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )

        Text(
            text = if (type == MaimaiEnums.SongType.DX) "DX" else "标准",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            color = Color.White
        )
    }
}
