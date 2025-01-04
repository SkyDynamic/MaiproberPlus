package io.github.skydynamic.maiproberplus.ui.compose.scores.maimai

import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiScoreManager.deleteScore
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import io.github.skydynamic.maiproberplus.ui.compose.ConfirmDialog
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerViewModel
import io.github.skydynamic.maiproberplus.ui.compose.scores.common.ColorLevelBox
import io.github.skydynamic.maiproberplus.ui.theme.getCardColor

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MaimaiScoreDetailDialog(
    scoreDetail: MaimaiScoreEntity,
    onDismissRequest: () -> Unit
) {
    var openDeleteConfirmDialog by remember { mutableStateOf(false) }

    val title = scoreDetail.title
    val dxScore = scoreDetail.dxScore
    val noteTotal = MaimaiData.getNoteTotal(title, scoreDetail.diff, scoreDetail.type)
    val dxStars = MaimaiData.getDxStar(noteTotal, dxScore)

    when {
        openDeleteConfirmDialog -> {
            ConfirmDialog(
                info = "你确定要删除该成绩吗？",
                onDismiss = {
                    openDeleteConfirmDialog = false
                },
                onRequest = {
                    deleteScore(scoreDetail)
                    ScoreManagerViewModel.showMaimaiScoreSelectionDialog = false
                    ScoreManagerViewModel.maimaiScoreSelection = null
                    ScoreManagerViewModel.maimaiSearchScores.remove(scoreDetail)
                    ScoreManagerViewModel.maimaiSearchCache.clear()
                }
            )
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(start = 16.dp, end = 16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(getCardColor())
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .height(30.dp)

                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "成绩详情",
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = onDismissRequest
                        ) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                }

                // Song Info
                Row(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                        .fillMaxWidth()
                        .height(100.dp)
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
                        modifier = Modifier
                            .height(95.dp)
                            .width(95.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .weight(0.7f)
                            .padding(start = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(25.dp)
                        ) {
                            MaimaiSongTypeIco(
                                type = scoreDetail.type,
                                modifier = Modifier.align(Alignment.CenterStart),
                            )
                        }

                        val scoreTitleScrollState = rememberScrollState()
                        Box(
                            Modifier
                                .height(IntrinsicSize.Min)
                                .fillMaxWidth()
                        ) {
                            SelectionContainer(
                                Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth()
                                    .horizontalScroll(scoreTitleScrollState)
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W700,
                                    maxLines = 1
                                )
                            }
                            androidx.compose.animation.AnimatedVisibility(
                                visible = scoreTitleScrollState.canScrollBackward,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(16.dp)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                listOf(
                                                    getCardColor(),
                                                    Color.Transparent,
                                                )
                                            )
                                        )
                                )
                            }
                            androidx.compose.animation.AnimatedVisibility(
                                visible = scoreTitleScrollState.canScrollForward,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(16.dp)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                listOf(
                                                    Color.Transparent,
                                                    getCardColor(),
                                                )
                                            )
                                        )
                                )
                            }
                        }

                        Text(
                            text = "曲目 ID: ${MaimaiData.getSongIdFromTitle(title)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                        )

                        Row {
                            AsyncImage(
                                model = scoreDetail.fullComboType.imageId,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(25.dp)
                                    .width(25.dp),
                            )

                            AsyncImage(
                                model = scoreDetail.syncType.imageId,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(25.dp)
                                    .width(25.dp),
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.3f)
                    ) {
                        ColorLevelBox(
                            level = MaimaiData.getLevelValue(title, scoreDetail.diff, scoreDetail.type),
                            modifier = Modifier.align(Alignment.CenterEnd),
                            color = scoreDetail.diff.color,
                        )
                    }
                }

                // achievement
                Row(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    AsyncImage(
                        model = scoreDetail.rankType.imageId,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(0.3f)
                    )

                    Column(
                        modifier = Modifier
                            .weight(0.7f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = "达成率",
                            fontSize = 10.sp,
                            color = Color(118, 115, 115, 255)
                        )
                        Text(
                            text = "${DecimalFormat("#." + "0".repeat(4)).format(scoreDetail.achievement)}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Rating and DxScore
                Row(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = "DX Rating",
                                fontSize = 10.sp,
                                color = Color(118, 115, 115, 255),
                                fontWeight = FontWeight.Light,
                            )
                            Text(
                                text = "${scoreDetail.rating}",
                                fontSize = 14.sp,
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(start = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 8.dp)
                        ) {
                            Row {
                                Text(
                                    text = "DX 分数",
                                    fontSize = 10.sp,
                                    color = Color(118, 115, 115, 255),
                                    fontWeight = FontWeight.Light,
                                )

                                if (dxStars > 0) {
                                    AsyncImage(
                                        model = MaimaiData.getDxStarBitmap(dxStars)!!,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .height(24.dp)
                                            .width(24.dp)
                                    )
                                }
                            }
                            Text(
                                text = "$dxScore / ${noteTotal * 3}",
                                fontSize = 14.sp,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Button(
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f),
                        onClick = {
                            openDeleteConfirmDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(Color.Red)
                    ) {
                        Icon(Icons.Default.Delete, null, tint = Color.White)
                        Text("删除该成绩", color = Color.White)
                    }
                }
            }
        }
    }
}