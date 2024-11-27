package io.github.skydynamic.maiproberplus.ui.compose.scores.maimai

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.skydynamic.maiproberplus.Application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiScoreManager.createMaimaiScore
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import io.github.skydynamic.maiproberplus.core.prober.sendMessageToUi
import io.github.skydynamic.maiproberplus.core.utils.calcMaimaiRating
import io.github.skydynamic.maiproberplus.ui.theme.getCardColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MaimaiCreateScoreDialog(
    onDismissRequest: () -> Unit
) {
    var openSearchSongDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var songInfo: MaimaiData.SongInfo? by remember { mutableStateOf(null) }
    var type: MaimaiEnums.SongType? by remember { mutableStateOf(null) }
    var selectionDiff: MaimaiEnums.Difficulty? by remember { mutableStateOf(null) }
    var diffLevel by remember { mutableStateOf("") }
    var difficulties: List<MaimaiData.SongDiffculty> by remember { mutableStateOf(emptyList()) }
    var achievement: String by remember { mutableStateOf("") }
    var fullComboType: MaimaiEnums.FullComboType? by remember { mutableStateOf(null) }
    var fullSyncType: MaimaiEnums.SyncType? by remember { mutableStateOf(null) }
    var dxScore: String by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false)}
    var isOutOfRange by remember { mutableStateOf(false) }

    when {
        openSearchSongDialog -> {
            MaimaiSearchSongDialog(
                onConfirm = {
                    songInfo = it
                    title = songInfo!!.title
                },
                onDismissRequest = {
                    openSearchSongDialog = false
                }
            )
        }

        type == MaimaiEnums.SongType.STANDARD -> {
            diffLevel = ""
            selectionDiff = null
            difficulties = songInfo!!.difficulties.standard
        }

        type == MaimaiEnums.SongType.DX -> {
            diffLevel = ""
            selectionDiff = null
            difficulties = songInfo!!.difficulties.dx
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth().height(580.dp).padding(start = 16.dp, end = 16.dp),
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
                            text = "创建成绩",
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
                        .height(110.dp)
                ) {
                    // Image
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(54, 53, 53, 255))
                    ) {
                        if (title.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(
                                        "https://assets2.lxns.net/maimai/jacket/${
                                            MaimaiData.getSongIdFromTitle(
                                                title
                                            )
                                        }.png"
                                    )
                                    .crossfade(true)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = null,
                                onError = { error ->
                                    Log.e("Image", "Error loading image", error.result.throwable)
                                },
                                modifier = Modifier.height(100.dp).width(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = "请选择曲目",
                                fontSize = 12.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().height(110.dp).padding(8.dp)
                    ) {
                        Button(
                            onClick = {
                                openSearchSongDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(35.dp)
                        ) {
                            Icon(Icons.Default.Search, null)
                            Text(
                                text = "选择歌曲"
                            )
                        }

                        Text("谱面类型", fontSize = 12.sp)
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth().height(35.dp),
                        ) {
                            MaimaiEnums.SongType.entries.forEach {
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = it.ordinal,
                                        count = MaimaiEnums.SongType.entries.size,
                                    ),
                                    selected = type == it,
                                    onClick = {
                                        type = it
                                    },
                                    enabled = MaimaiData.songHasTypeDifficulty(title, it)
                                ) {
                                    Text(it.type2)
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                        .fillMaxWidth()
                        .height(75.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(0.5f).padding(end = 8.dp)
                    ) {
                        Text("难度", fontSize = 12.sp)
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                expanded = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                if (selectionDiff != null) {
                                    selectionDiff!!.color
                                } else {
                                    Color.Unspecified
                                }
                            ),
                            enabled = type != null
                        ) {
                            if (type == null || diffLevel.isEmpty()) {
                                Text("请选择难度")
                            } else {
                                Text("${MaimaiEnums.Difficulty.BASIC.name} $diffLevel", color = Color.White)
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.wrapContentWidth().wrapContentHeight()
                        ) {
                            difficulties.forEachIndexed { index, diff ->
                                val difficulty = MaimaiEnums.Difficulty
                                    .getDifficultyWithIndex(diff.difficulty)
                                DropdownMenuItem(
                                    text = {
                                        Text("${difficulty.name} ${diff.level}", modifier = Modifier.padding(start = 8.dp))
                                    },
                                    onClick = {
                                        selectionDiff = difficulty
                                        diffLevel = diff.level
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text("达成率", fontSize = 12.sp)
                        OutlinedTextField(
                            modifier = Modifier.height(56.dp),
                            value = achievement.toString(),
                            onValueChange = { newValue ->
                                val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                                val parsedValue = filteredValue.toDoubleOrNull()
                                if (newValue.isEmpty()) {
                                    achievement = newValue
                                    isOutOfRange = true
                                }
                                if (parsedValue != null && parsedValue >= 0.0 && parsedValue <= 101.0) {
                                    val decimalPlaces = filteredValue.split(".").getOrNull(1)?.length ?: 0
                                    if (decimalPlaces <= 4) {
                                        achievement = filteredValue
                                        isOutOfRange = false
                                    } else {
                                        isOutOfRange = true
                                    }
                                } else {
                                    isOutOfRange = true
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            visualTransformation = VisualTransformation.None,
                            isError = isOutOfRange
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text("Full Combo", fontSize = 12.sp)
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp),
                    ) {
                        MaimaiEnums.FullComboType.entries.forEachIndexed { index, it ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = it.ordinal,
                                    count = MaimaiEnums.FullComboType.entries.size,
                                ),
                                selected = fullComboType == it,
                                onClick = {
                                    fullComboType = it
                                }
                            ) {
                                Text(it.typeName2)
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text("Full Sync", fontSize = 12.sp)
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp),
                    ) {
                        MaimaiEnums.SyncType.entries.forEachIndexed { index, it ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = it.ordinal,
                                    count = MaimaiEnums.SyncType.entries.size,
                                ),
                                selected = fullSyncType == it,
                                onClick = {
                                    fullSyncType = it
                                }
                            ) {
                                Text(it.typeName2)
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.LightGray,
                    thickness = 1.dp
                )

                Text(
                    "DX分数(可选)",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp)
                )
                OutlinedTextField(
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp).height(52.dp),
                    value = dxScore,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.length <= 4) {
                            dxScore = it
                        } else if (it.isEmpty()) {
                            dxScore = ""
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    visualTransformation = VisualTransformation.None,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        onClick = {
                            onDismissRequest()
                        }
                    ) {
                        Text("取消")
                    }

                    Button(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        onClick = {
                            if (
                                songInfo != null &&
                                selectionDiff != null &&
                                type != null &&
                                fullComboType != null &&
                                fullSyncType != null
                            ) {
                                GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                                    createMaimaiScore(
                                        MaimaiScoreEntity(
                                            songId = songInfo!!.id,
                                            title = title,
                                            level = difficulties[selectionDiff!!.diffIndex].levelValue,
                                            achievement = achievement.toFloat(),
                                            dxScore = if (dxScore.isNotEmpty()) dxScore.toInt() else 0,
                                            rating = calcMaimaiRating(achievement, difficulties[selectionDiff!!.diffIndex].levelValue),
                                            version = difficulties[selectionDiff!!.diffIndex].version,
                                            type = type!!,
                                            diff = selectionDiff!!,
                                            rankType = MaimaiEnums.RankType.getRankTypeByScore(achievement.toFloat()),
                                            syncType = fullSyncType!!,
                                            fullComboType = fullComboType!!
                                        )
                                    )
                                }
                                sendMessageToUi("成绩添加成功")
                                onDismissRequest()
                            } else {
                                sendMessageToUi("请检查完整信息是否完整")
                            }
                        }
                    ) {
                        Text("确定")
                    }
                }
            }
        }
    }
}