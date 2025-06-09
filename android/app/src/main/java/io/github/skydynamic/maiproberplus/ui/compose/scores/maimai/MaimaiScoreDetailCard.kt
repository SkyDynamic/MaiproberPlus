package io.github.skydynamic.maiproberplus.ui.compose.scores.maimai

import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.skydynamic.maiproberplus.core.config.ScoreDisplayType
import io.github.skydynamic.maiproberplus.core.config.ScoreStyleType
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import io.github.skydynamic.maiproberplus.core.utils.NetworkImageRequestUtil
import io.github.skydynamic.maiproberplus.ui.compose.scores.common.LevelBox

@Composable
fun MaimaiScoreDetailCard(
    modifier: Modifier,
    scoreDisplayType: ScoreDisplayType,
    scoreStyleType: ScoreStyleType,
    scoreDetail: MaimaiScoreEntity,
    onClick: () -> Unit,
) {
    val title = scoreDetail.title
    val level = scoreDetail.level
    val color = scoreDetail.diff.color
    val rating = scoreDetail.rating

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(
                when (scoreDisplayType) {
                    ScoreDisplayType.Middle -> 1f
                    else -> 2f
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // music cover
            AsyncImage(
                model = NetworkImageRequestUtil.getImageRequest(
                    "https://assets2.lxns.net/maimai/jacket/${
                        MaimaiData.getSongIdFromTitle(title)
                    }.png"
                ),
                contentDescription = null,
                onError = { error ->
                    Log.e("Image", "Error loading image", error.result.throwable)
                },
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // color overlay
            if (scoreStyleType == ScoreStyleType.ColorOverlay) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color.copy(alpha = 0.4f))
                )
            }

            // title
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .height(25.dp)
                    .fillMaxWidth()
                    .background(color.copy(alpha = 0.8f))
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 5.dp, end = 40.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )

                MaimaiSongTypeIco(
                    type = scoreDetail.type,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                )
            }

            // rating
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 8.dp, bottom = 8.dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${DecimalFormat("#." + "0".repeat(4)).format(scoreDetail.achievement)}%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = when (scoreStyleType) {
                                ScoreStyleType.TextShadow ->
                                    Shadow(
                                        color = color,
                                        blurRadius = 10f,
                                    )
                                else -> null
                            }
                        ),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "DX Rating: $rating",
                        style = MaterialTheme.typography.labelSmall.copy(
                            shadow = when (scoreStyleType) {
                                ScoreStyleType.TextShadow ->
                                    Shadow(
                                        color = color,
                                        blurRadius = 10f,
                                    )
                                else -> null
                            }
                        ),
                        color = Color.White,
                    )
                }
                LevelBox(
                    level,
                    Modifier
                        .padding(end = 8.dp)
                )
            }
        }
    }
}
