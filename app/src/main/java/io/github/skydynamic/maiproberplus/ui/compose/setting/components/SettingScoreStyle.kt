package io.github.skydynamic.maiproberplus.ui.compose.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums

@Preview
@Composable
fun SettingScoreStyleExampleColorOverlay(
    modifier: Modifier = Modifier
) {
    val color = MaimaiEnums.Difficulty.REMASTER.color
    Card(
        modifier = modifier
            .aspectRatio(2f)
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color.copy(alpha = 0.4f))
            )

            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .height(25.dp)
                    .fillMaxWidth()
                    .background(color.copy(alpha = 0.8f))
            ) {
                Text(
                    text = "X".repeat(6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 5.dp, end = 40.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
            }

            Text(
                text = "000000",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
fun SettingScoreStyleExampleTextShadow(
    modifier: Modifier = Modifier
) {
    val color = MaimaiEnums.Difficulty.REMASTER.color
    Card(
        modifier = modifier
            .aspectRatio(2f)
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .height(25.dp)
                    .fillMaxWidth()
                    .background(color.copy(alpha = 0.8f))
            ) {
                Text(
                    text = "X".repeat(6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 5.dp, end = 40.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
            }

            Text(
                text = "000000",
                style = MaterialTheme.typography.titleMedium.copy(
                    shadow = Shadow(
                        color = color,
                        blurRadius = 10f,
                    )
                ),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}
