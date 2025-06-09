package io.github.skydynamic.maiproberplus.ui.compose.scores.chuni

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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniData
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniEnums
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniScoreManager.createChuniScore
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.core.prober.sendMessageToUi
import io.github.skydynamic.maiproberplus.core.utils.NetworkImageRequestUtil
import io.github.skydynamic.maiproberplus.core.utils.calcChuniRating
import io.github.skydynamic.maiproberplus.ui.theme.getButtonSelectedColor
import io.github.skydynamic.maiproberplus.ui.theme.getCardColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChuniCreateScoreDialog(
    onDismissRequest: () -> Unit
) {
    var openSearchSongDialog by remember { mutableStateOf(false) }

    var songInfo: ChuniData.SongInfo? by remember { mutableStateOf(null) }
    var title by remember { mutableStateOf("") }
    var selectionDiff: ChuniEnums.Difficulty? by remember { mutableStateOf(null) }
    var diffLevel by remember { mutableStateOf("") }
    var difficulties: List<ChuniData.SongDifficulty> by remember { mutableStateOf(emptyList()) }
    var levelValue: Float by remember { mutableFloatStateOf(0F) }
    var score: String by remember { mutableStateOf("") }
    var clearType: ChuniEnums.ClearType? by remember { mutableStateOf(null) }
    var fullComboType: ChuniEnums.FullComboType? by remember { mutableStateOf(null) }
    var fullChainType: ChuniEnums.FullChainType? by remember { mutableStateOf(null) }

    var clearExpanded by remember { mutableStateOf(false) }
    var diffExpanded by remember { mutableStateOf(false)}
    var isOutOfRange by remember { mutableStateOf(false) }

    when {
        openSearchSongDialog -> {
            ChuniSearchSongDialog(
                onConfirm = {
                    songInfo = it
                    title = it.title
                    difficulties = it.difficulties
                },
                onDismissRequest = {
                    openSearchSongDialog = false
                }
            )
        }
    }
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp)
            .padding(start = 16.dp, end = 16.dp),
    ) {

        Card(
            modifier = Modifier
                .fillMaxSize(),
            colors = CardDefaults.cardColors(getCardColor())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .height(30.dp)

                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = "创建成绩",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                        )
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.CenterEnd),
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
                            .background(getButtonSelectedColor(false))
                    ) {
                        if (title.isNotEmpty()) {
                            AsyncImage(
                                model = NetworkImageRequestUtil.getImageRequest(
                                    "https://assets2.lxns.net/chunithm/jacket/${
                                        ChuniData.getSongIdFromTitle(
                                            title
                                        )
                                    }.png"
                                ),
                                contentDescription = null,
                                onError = { error ->
                                    Log.e("Image", "Error loading image", error.result.throwable)
                                },
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = "请选择曲目",
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .padding(8.dp)
                    ) {
                        Button(
                            onClick = {
                                openSearchSongDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(35.dp)
                        ) {
                            Icon(Icons.Default.Search, null)
                            Text(
                                text = "选择歌曲"
                            )
                        }

                        Text("难度", fontSize = 12.sp)
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                diffExpanded = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                if (selectionDiff != null) {
                                    selectionDiff!!.color
                                } else {
                                    Color.Unspecified
                                }
                            ),
                            enabled = songInfo != null
                        ) {
                            if (diffLevel.isEmpty()) {
                                Text("请选择难度")
                            } else {
                                Text(
                                    "${selectionDiff!!.name} $diffLevel",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = diffExpanded,
                            onDismissRequest = { diffExpanded = false },
                            modifier = Modifier
                                .wrapContentWidth()
                                .wrapContentHeight()
                        ) {
                            difficulties.forEachIndexed { index, diff ->
                                val difficulty = ChuniEnums.Difficulty
                                    .getDifficultyWithIndex(diff.difficulty)
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "${difficulty.name} ${diff.level}",
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                        )
                                    },
                                    onClick = {
                                        selectionDiff = difficulty
                                        diffLevel = diff.level
                                        levelValue = diff.levelValue
                                        diffExpanded = false
                                    }
                                )
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
                        modifier = Modifier
                            .weight(0.5f)
                    ) {
                        Text("成绩", fontSize = 12.sp)
                        OutlinedTextField(
                            modifier = Modifier
                                .height(56.dp),
                            value = score,
                            onValueChange = { newValue ->
                                val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                                val parsedValue = filteredValue.toIntOrNull()
                                if (newValue.isEmpty()) {
                                    score = newValue
                                    isOutOfRange = true
                                }
                                if (parsedValue != null && parsedValue >= 0 && parsedValue <= 1010000) {
                                    score = filteredValue
                                    isOutOfRange = false
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

                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(0.5f)
                    ) {
                        Text("Clear", fontSize = 12.sp)
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = { clearExpanded = true }
                        ) {
                            if (clearType == null) {
                                Text(
                                    "请选择Clear类型",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    clearType!!.type.replaceFirstChar {
                                        if (it.isLowerCase()) {
                                            it.titlecase(Locale.ROOT)
                                        } else {
                                            it.toString()
                                        }
                                    },
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = clearExpanded,
                            onDismissRequest = { clearExpanded = false },
                            modifier = Modifier
                                .wrapContentWidth()
                                .wrapContentHeight()
                        ) {
                            ChuniEnums.ClearType.entries.forEach { clear ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            clear.type,
                                            modifier = Modifier
                                               .padding(start = 8.dp)
                                        )
                                    },
                                    onClick = {
                                        clearType = clear
                                        clearExpanded = false
                                    }
                                )
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
                    Text("Full Combo", fontSize = 12.sp)
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp),
                    ) {
                        ChuniEnums.FullComboType.entries.forEachIndexed { index, it ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = it.ordinal,
                                    count = ChuniEnums.FullComboType.entries.size,
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
                    Text("Full Chain", fontSize = 12.sp)
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp),
                    ) {
                        ChuniEnums.FullChainType.entries.forEachIndexed { index, it ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = it.ordinal,
                                    count = ChuniEnums.FullChainType.entries.size,
                                ),
                                selected = fullChainType == it,
                                onClick = {
                                    fullChainType = it
                                }
                            ) {
                                Text(it.typeName2)
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.LightGray,
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp),
                        onClick = {
                            onDismissRequest()
                        }
                    ) {
                        Text("取消")
                    }

                    Button(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp),
                        onClick = {
                            if (
                                songInfo != null &&
                                selectionDiff != null &&
                                clearType != null &&
                                fullComboType != null &&
                                fullChainType != null
                            ) {
                                GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                                    createChuniScore(
                                        ChuniScoreEntity(
                                            songId = songInfo!!.id,
                                            title = title,
                                            level = levelValue,
                                            score = score.toInt(),
                                            rating = calcChuniRating(score.toInt(), levelValue),
                                            version = difficulties[selectionDiff!!.diffIndex].version,
                                            rankType = ChuniEnums.RankType.getRankTypeByScore(score.toInt()),
                                            diff = selectionDiff!!,
                                            fullComboType = fullComboType!!,
                                            fullChainType = fullChainType!!,
                                            clearType = clearType!!
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