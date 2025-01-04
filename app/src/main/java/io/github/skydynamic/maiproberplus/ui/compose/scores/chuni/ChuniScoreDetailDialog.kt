package io.github.skydynamic.maiproberplus.ui.compose.scores.chuni

import android.icu.math.BigDecimal
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
import io.github.skydynamic.maiproberplus.R
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniData
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniEnums
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniScoreManager.deleteScore
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.ui.compose.ConfirmDialog
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerViewModel
import io.github.skydynamic.maiproberplus.ui.compose.scores.common.ColorLevelBox
import io.github.skydynamic.maiproberplus.ui.theme.getCardColor
import java.text.NumberFormat

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChuniScoreDetailDialog(
    scoreDetail: ChuniScoreEntity,
    onDismissRequest: () -> Unit
) {
    var openDeleteConfirmDialog by remember { mutableStateOf(false) }

    val title = scoreDetail.title

    when {
        openDeleteConfirmDialog -> {
            ConfirmDialog(
                info = "你确定要删除该成绩吗？",
                onDismiss = {
                    openDeleteConfirmDialog = false
                },
                onRequest = {
                    deleteScore(scoreDetail)
                    ScoreManagerViewModel.showChuniScoreSelectionDialog = false
                    ScoreManagerViewModel.chuniScoreSelection = null
                    ScoreManagerViewModel.chuniSearchScores.remove(scoreDetail)
                    ScoreManagerViewModel.chuniSearchCache.clear()
                }
            )
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth().height(350.dp).padding(start = 16.dp, end = 16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(getCardColor())
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth().height(30.dp)

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

                Row(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(
                                "https://assets2.lxns.net/chunithm/jacket/${scoreDetail.songId}.png"
                            )
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = null,
                        onError = { error ->
                            Log.e("Image", "Error loading image", error.result.throwable)
                        },
                        modifier = Modifier.height(95.dp).width(95.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier.weight(0.7f).padding(start = 12.dp)
                    ) {
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
                            text = "曲目 ID: ${ChuniData.getSongIdFromTitle(title)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                        )

                        if (scoreDetail.fullComboType != ChuniEnums.FullComboType.NULL) {
                            AsyncImage(
                                model = scoreDetail.fullComboType.imageId,
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        if (scoreDetail.fullChainType != ChuniEnums.FullChainType.NULL) {
                            AsyncImage(
                                model = scoreDetail.fullChainType.imageId,
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxHeight().weight(0.3f)
                    ) {
                        ColorLevelBox(
                            level = ChuniData.getLevelValue(title, scoreDetail.diff),
                            modifier = Modifier.align(Alignment.CenterEnd),
                            color = scoreDetail.diff.color,
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(start = 12.dp).fillMaxWidth().height(60.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(0.3f)
                    ) {
                        AsyncImage(
                            model = scoreDetail.rankType.imageId,
                            contentDescription = null,
                            modifier = Modifier.height(30.dp).align(Alignment.CenterHorizontally)
                        )

                        val clearImageId = if (
                            scoreDetail.clearType != ChuniEnums.ClearType.FAILED
                        ) {
                            R.drawable.ic_chuni_clear
                        } else {
                            R.drawable.ic_chuni_failed
                        }

                        AsyncImage(
                            model = clearImageId,
                            contentDescription = null,
                            modifier = Modifier.height(30.dp).align(Alignment.CenterHorizontally)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.7f).padding(start = 12.dp)
                    ) {
                        Text(
                            text = "成绩",
                            fontSize = 10.sp,
                            color = Color(118, 115, 115, 255)
                        )
                        Text(
                            text = "${NumberFormat.getNumberInstance().format(scoreDetail.score)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(0.5f).padding(end = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Rating",
                                fontSize = 10.sp,
                                color = Color(118, 115, 115, 255),
                                fontWeight = FontWeight.Light,
                            )
                            Text(
                                text = "${
                                    BigDecimal(scoreDetail.rating.toDouble())
                                        .setScale(2, BigDecimal.ROUND_UP)
                                        .toDouble()
                                }",
                                fontSize = 14.sp,
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(0.5f).padding(start = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(start = 8.dp)
                        ) {
                            Row {
                                Text(
                                    text = "Over Power",
                                    fontSize = 10.sp,
                                    color = Color(118, 115, 115, 255),
                                    fontWeight = FontWeight.Light,
                                )
                            }
                            Text(
                                text = "${ChuniData.calcOverPower(scoreDetail)}",
                                fontSize = 14.sp,
                            )
                        }
                    }
                }

                Button(
                    modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally),
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