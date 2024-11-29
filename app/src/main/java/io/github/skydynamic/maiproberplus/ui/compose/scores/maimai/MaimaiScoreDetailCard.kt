package io.github.skydynamic.maiproberplus.ui.compose.scores.maimai

import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import io.github.skydynamic.maiproberplus.ui.compose.scores.common.LevelBox

@Composable
fun MaimaiScoreDetailCard(
    modifier: Modifier,
    scoreDetail: MaimaiScoreEntity,
    onClick: () -> Unit
) {
    val title = scoreDetail.title
    val level = scoreDetail.level
    val color = scoreDetail.diff.color
    val rating = scoreDetail.rating

    ElevatedCard(
        modifier = modifier
            .fillMaxSize(),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://assets2.lxns.net/maimai/jacket/${MaimaiData.getSongIdFromTitle(title)}.png")
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = null,
                onError = { error ->
                    Log.e("Image", "Error loading image", error.result.throwable)
                },
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color.copy(alpha = 0.4f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
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
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${DecimalFormat("#." + "0".repeat(4)).format(scoreDetail.achievement)}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "DX Rating: $rating",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
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
}
