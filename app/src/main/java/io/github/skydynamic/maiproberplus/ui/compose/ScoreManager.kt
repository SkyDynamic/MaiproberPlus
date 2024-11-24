package io.github.skydynamic.maiproberplus.ui.compose

import android.util.Log
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.core.prober.getMaimaiScoreCache
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.floor

object ScoreManagerViewModel : ViewModel() {
    val maimaiLoadedScores = mutableStateListOf<MaimaiData.MusicDetail>()
    val maimaiSearchScores = mutableStateListOf<MaimaiData.MusicDetail>()
    val maimaiSearchText = mutableStateOf("")

    fun searchMaimaiScore(text: String) {
        if (text.isEmpty()) {
            maimaiSearchScores.clear()
        } else {
            val searchResult = maimaiLoadedScores.filter {
                it.name.contains(text, ignoreCase = true)
            }
            maimaiSearchScores.clear()
            maimaiSearchScores.addAll(searchResult)
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun refreshMaimaiScore() {
    GlobalScope.launch(Dispatchers.IO) {
        ScoreManagerViewModel.maimaiLoadedScores.clear()
        ScoreManagerViewModel.maimaiLoadedScores.addAll(getMaimaiScoreCache().sortedByDescending {
            it.rating
        })
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun ScoreManager() {
    var gameTypeIndex by remember { mutableIntStateOf(0) }
    var openInitDownloadDialog by remember { mutableStateOf(false) }
    var canShow by remember { mutableStateOf(false) }
    val viewModel = remember { ScoreManagerViewModel }

    val gameTypeList = listOf("舞萌DX", "中二节奏")

    if (!application.filesDir.resolve("maimai_song_list.json").exists() ||
        !application.filesDir.resolve("chuni_song_list.json").exists()
    ) {
        openInitDownloadDialog = true
    } else {
        canShow = true
    }

    LaunchedEffect(key1 = canShow) {
        if (canShow) {
            refreshMaimaiScore()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                gameTypeList.forEachIndexed { index, name ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index, count = gameTypeList.size
                        ),
                        onClick = { gameTypeIndex = index },
                        selected = index == gameTypeIndex
                    ) {
                        Text(name)
                    }
                }
            }
        }

        item {
            Row {
                OutlinedTextField(
                    value = viewModel.maimaiSearchText.value,
                    onValueChange = {
                        viewModel.maimaiSearchText.value = it
                        viewModel.searchMaimaiScore(it)
                    },
                    modifier = Modifier.height(60.dp).weight(0.6f, fill = false),
                    label = { Text("搜索歌曲") },
                    trailingIcon = {
                        if (!viewModel.maimaiSearchText.value.isEmpty()) {
                            IconButton(
                                onClick = {
                                    viewModel.maimaiSearchText.value = ""
                                    viewModel.searchMaimaiScore("")
                                }
                            ) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    },
                )

                Button(
                    modifier = Modifier.height(60.dp).weight(0.4f, fill = false),
                    onClick = {
                        viewModel.maimaiLoadedScores.clear()
                        viewModel.maimaiSearchScores.clear()

                        refreshMaimaiScore()
                    }
                ) {
                    Text("刷新列表")
                }
            }
        }

        item {
            Spacer(Modifier.height(15.dp))
        }

        if (canShow) {
            when (gameTypeIndex) {
                0 -> {
                    if (viewModel.maimaiSearchText.value.isNotEmpty()) {
                        items(viewModel.maimaiSearchScores.chunked(2)) { rowItems ->
                            Row {
                                rowItems.forEach { score ->
                                    MaimaiScoreDetailCard(
                                        modifier = Modifier
                                            .height(80.dp)
                                            .weight(1f)
                                            .padding(4.dp),
                                        scoreDetail = score
                                    )
                                }
                            }
                        }
                    } else {
                        items(viewModel.maimaiLoadedScores.chunked(2)) { rowItems ->
                            Row {
                                rowItems.forEach { score ->
                                    MaimaiScoreDetailCard(
                                        modifier = Modifier
                                            .height(80.dp)
                                            .weight(1f)
                                            .padding(4.dp),
                                        scoreDetail = score
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                }
            }
        }
    }

    when {
        openInitDownloadDialog -> {
            DownloadDialog(
                listOf(
                    FileDownloadMeta(
                        "maimai_song_list.json",
                        ".",
                        "https://maimai.lxns.net/api/v0/maimai/song/list?notes=true"
                    ),
                    FileDownloadMeta(
                        "chuni_song_list.json",
                        ".",
                        "https://maimai.lxns.net/api/v0/chunithm/song/list"
                    )
                )
            ) {
                openInitDownloadDialog = false
                canShow = true
            }
        }
    }
}



@Composable
fun MaimaiScoreDetailCard(
    modifier: Modifier,
    scoreDetail: MaimaiData.MusicDetail
) {
    var title = scoreDetail.name
    var level = scoreDetail.level
    var color = scoreDetail.diff.color
    var rating = scoreDetail.rating

    Box(
        modifier = modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
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
                .background(Color(color.red, color.green, color.blue, 0.4F))
        )

        Box(
            modifier = Modifier
                .height(25.dp)
                .fillMaxWidth()
                .background(Color(color.red, color.green, color.blue, 0.8F))
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 5.dp, end = 40.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            GetMaimaiSongTypeIco(
                modifier = Modifier.align(Alignment.CenterEnd),
                type = scoreDetail.type
            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            GetMaimaiLevelBox(
                modifier = Modifier.align(Alignment.CenterEnd),
                level = level
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(4.dp)
        ) {
            Text(
                text = "${scoreDetail.score}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "DX Rating: ${floor(rating.toDouble()).toInt()}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun GetMaimaiSongTypeIco(
    modifier: Modifier,
    type: MaimaiEnums.SongType
) {
    val color = if (type == MaimaiEnums.SongType.DX) {
        Color(255, 87, 34, 255)
    } else {
        Color(63, 81, 181, 255)
    }
    Box(
        modifier = modifier.fillMaxHeight().padding(end = 5.dp)
    ) {
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
                .width(30.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )

        Text(
            text = if (type == MaimaiEnums.SongType.DX) "DX" else "标准",
            fontSize = 10.sp,
            modifier = Modifier.fillMaxHeight().align(Alignment.Center)
        )
    }
}

@Composable
fun GetMaimaiLevelBox(
    modifier: Modifier,
    level: Float
) {
    Box(
        modifier = modifier.fillMaxHeight().padding(top = 25.dp, end = 5.dp)
    ) {
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
                .width(40.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(43, 38, 38, 255))
        ) {
            Text(
                text = "$level",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxHeight().align(Alignment.Center),
                color = Color(139, 132, 132, 255)
            )
        }
    }
}