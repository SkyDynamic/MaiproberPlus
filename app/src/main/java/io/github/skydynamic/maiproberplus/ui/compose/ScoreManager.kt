package io.github.skydynamic.maiproberplus.ui.compose

import android.icu.math.BigDecimal
import android.util.Log
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.core.prober.getChuniScoreCache
import io.github.skydynamic.maiproberplus.core.prober.getMaimaiScoreCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import kotlin.math.floor

val resources = listOf<FileDownloadMeta>(
    FileDownloadMeta(
        "maimai_song_list.json",
        ".",
        "https://maimai.lxns.net/api/v0/maimai/song/list?notes=true"
    ),
    FileDownloadMeta(
        "chuni_song_list.json",
        ".",
        "https://maimai.lxns.net/api/v0/chunithm/song/list"
    ),
    FileDownloadMeta(
        "maimai_song_aliases.json",
        ".",
        "https://maimai.lxns.net/api/v0/maimai/alias/list"
    ),
    FileDownloadMeta(
        "chuni_song_aliases.json",
        ".",
        "https://maimai.lxns.net/api/v0/chunithm/alias/list"
    )
)

object ScoreManagerViewModel : ViewModel() {
    val maimaiLoadedScores = mutableStateListOf<MaimaiData.MusicDetail>()
    val maimaiSearchScores = mutableStateListOf<MaimaiData.MusicDetail>()
    val maimaiSearchText = mutableStateOf("")
    val chuniLoadedScores = mutableStateListOf<ChuniData.MusicDetail>()
    val chuniSearchScores = mutableStateListOf<ChuniData.MusicDetail>()
    val chuniSearchText = mutableStateOf("")

    private val chuniAliasMap: Map<Int, List<String>> by lazy {
        ChuniData.CHUNI_SONG_ALIASES.associateBy({ it.songId }, { it.aliases })
    }
    private val chuniSearchCache = mutableMapOf<String, List<ChuniData.MusicDetail>>()

    fun searchChuniScore(text: String) {
        if (text.isEmpty()) {
            chuniSearchScores.clear()
        } else {
            val cachedResult = chuniSearchCache[text]
            if (cachedResult != null) {
                chuniSearchScores.clear()
                chuniSearchScores.addAll(cachedResult)
            } else {
                ScoreManagerViewModel.viewModelScope.launch(Dispatchers.IO) {
                    val searchResult = chuniLoadedScores.filter { musicDetail ->
                        musicDetail.name.contains(text, ignoreCase = true) ||
                                chuniAliasMap[
                                    if (musicDetail.id == -1)
                                        ChuniData.getSongIdFromTitle(musicDetail.name)
                                    else
                                        musicDetail.id
                                ]?.any { alias ->
                                    alias.contains(text, ignoreCase = true)
                                } == true
                    }
                    withContext(Dispatchers.Main) {
                        chuniSearchScores.clear()
                        chuniSearchScores.addAll(searchResult)
                        chuniSearchCache[text] = searchResult
                    }
                }
            }
        }
    }

    private val maimaiAliasMap: Map<Int, List<String>> by lazy {
        MaimaiData.MAIMAI_SONG_ALIASES.associateBy({ it.songId }, { it.aliases })
    }
    private val maimaiSearchCache = mutableMapOf<String, List<MaimaiData.MusicDetail>>()

    fun searchMaimaiScore(text: String) {
        if (text.isEmpty()) {
            maimaiSearchScores.clear()
        } else {
            val cachedResult = maimaiSearchCache[text]
            if (cachedResult != null) {
                maimaiSearchScores.clear()
                maimaiSearchScores.addAll(cachedResult)
            } else {
                ScoreManagerViewModel.viewModelScope.launch(Dispatchers.IO) {
                    val searchResult = maimaiLoadedScores.filter { musicDetail ->
                        musicDetail.name.contains(text, ignoreCase = true) ||
                                maimaiAliasMap[
                                    if (musicDetail.id == -1)
                                        MaimaiData.getSongIdFromTitle(musicDetail.name)
                                    else
                                        musicDetail.id
                                ]?.any { alias ->
                                    alias.contains(text, ignoreCase = true)
                                } == true
                    }
                    withContext(Dispatchers.Main) {
                        maimaiSearchScores.clear()
                        maimaiSearchScores.addAll(searchResult)
                        maimaiSearchCache[text] = searchResult
                    }
                }
            }
        }
    }
}

fun refreshMaimaiScore() {
    ScoreManagerViewModel.viewModelScope.launch(Dispatchers.IO) {
        ScoreManagerViewModel.maimaiLoadedScores.clear()
        ScoreManagerViewModel.maimaiLoadedScores.addAll(getMaimaiScoreCache().sortedByDescending {
            it.rating
        })
    }
}

fun refreshChuniScore() {
    ScoreManagerViewModel.viewModelScope.launch(Dispatchers.IO) {
        ScoreManagerViewModel.chuniLoadedScores.clear()
        ScoreManagerViewModel.chuniLoadedScores.addAll(getChuniScoreCache().sortedByDescending {
            it.rating
        })
    }
}

fun checkResourceComplete(): List<FileDownloadMeta> {
    val returnList = arrayListOf<FileDownloadMeta>()
    resources.forEach {
        if (!application.filesDir.resolve(it.fileSavePath).resolve(it.fileName).exists()) {
            returnList.add(it)
        }
    }
    return returnList
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun ScoreManager() {
    var gameTypeIndex by remember { mutableIntStateOf(0) }
    var openInitDownloadDialog by remember { mutableStateOf(false) }
    var canShow by remember { mutableStateOf(false) }
    val viewModel = remember { ScoreManagerViewModel }

    val gameTypeList = listOf("舞萌DX", "中二节奏")

    val checkResourceResult = checkResourceComplete()
    if (!checkResourceResult.isEmpty()) {
        openInitDownloadDialog = true
    } else {
        canShow = true
    }

    LaunchedEffect(key1 = canShow) {
        if (canShow) {
            if (gameTypeIndex == 0) {
                refreshMaimaiScore()
            } else {
                refreshChuniScore()
            }
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
                        onClick = {
                            gameTypeIndex = index
                            if (index == 0) {
                                refreshMaimaiScore()
                            } else {
                                refreshChuniScore()
                            }
                        },
                        selected = index == gameTypeIndex
                    ) {
                        Text(name)
                    }
                }
            }
        }

        if (canShow) {
            when (gameTypeIndex) {
                0 -> {
                    item {
                        Row {
                            OutlinedTextField(
                                value = viewModel.maimaiSearchText.value,
                                onValueChange = {
                                    viewModel.maimaiSearchText.value = it
                                    viewModel.searchMaimaiScore(it)
                                },
                                modifier = Modifier.height(60.dp).weight(0.65f, fill = false),
                                label = { Text("搜索曲目或者曲目别名", fontSize = 12.sp) },
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
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .height(35.dp)
                                    .weight(0.35f, fill = false)
                                    .align(Alignment.CenterVertically),
                                onClick = {
                                    viewModel.maimaiLoadedScores.clear()
                                    viewModel.maimaiSearchScores.clear()
                                    refreshMaimaiScore()
                                    viewModel.searchMaimaiScore(viewModel.maimaiSearchText.value)
                                }
                            ) {
                                Text("刷新列表")
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(15.dp))
                    }
                    if (viewModel.maimaiSearchText.value.isNotEmpty()) {
                        items(viewModel.maimaiSearchScores.chunked(2)) { rowItems ->
                            Row {
                                rowItems.forEach { score ->
                                    MaimaiScoreDetailCard(
                                        modifier = Modifier
                                            .height(80.dp)
                                            .weight(1f)
                                            .padding(4.dp)
                                            .fillMaxWidth(0.5f),
                                        scoreDetail = score
                                    )
                                }
                                if (rowItems.size < 2) {
                                    Box(
                                        modifier = Modifier
                                            .height(80.dp)
                                            .weight(1f)
                                            .padding(4.dp)
                                            .fillMaxWidth(0.5f)
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
                                            .padding(4.dp)
                                            .fillMaxWidth(0.5f),
                                        scoreDetail = score
                                    )
                                }
                                if (rowItems.size < 2) {
                                    Box(
                                        modifier = Modifier
                                            .height(80.dp)
                                            .weight(1f)
                                            .padding(4.dp)
                                            .fillMaxWidth(0.5f)
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    item {
                        Row {
                            OutlinedTextField(
                                value = viewModel.chuniSearchText.value,
                                onValueChange = {
                                    viewModel.chuniSearchText.value = it
                                    viewModel.searchChuniScore(it)
                                },
                                modifier = Modifier.height(60.dp).weight(0.65f, fill = false),
                                label = { Text("搜索曲目或者曲目别名", fontSize = 12.sp) },
                                trailingIcon = {
                                    if (!viewModel.chuniSearchText.value.isEmpty()) {
                                        IconButton(
                                            onClick = {
                                                viewModel.chuniSearchText.value = ""
                                                viewModel.searchChuniScore("")
                                            }
                                        ) {
                                            Icon(Icons.Default.Clear, null)
                                        }
                                    }
                                },
                            )

                            Button(
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .height(35.dp)
                                    .weight(0.35f, fill = false)
                                    .align(Alignment.CenterVertically),
                                onClick = {
                                    viewModel.chuniLoadedScores.clear()
                                    viewModel.chuniSearchScores.clear()
                                    refreshChuniScore()

                                }
                            ) {
                                Text("刷新列表")
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(15.dp))
                    }
                    if (viewModel.chuniSearchText.value.isNotEmpty()) {
                        items(viewModel.chuniSearchScores.chunked(2)) { rowItems ->
                            Row {
                                rowItems.forEach { score ->
                                    ChuniScoreDetailCard(
                                        modifier = Modifier
                                            .height(80.dp)
                                            .weight(1f)
                                            .padding(4.dp)
                                            .fillMaxWidth(0.5f),
                                        scoreDetail = score
                                    )
                                }
                                if (rowItems.size < 2) {
                                    Box(
                                        modifier = Modifier
                                            .height(80.dp)
                                            .weight(1f)
                                            .padding(4.dp)
                                            .fillMaxWidth(0.5f)
                                    )
                                }
                            }
                        }
                    } else {
                        items(viewModel.chuniLoadedScores.chunked(2)) { rowItems ->
                            Row {
                                rowItems.forEach { score ->
                                    ChuniScoreDetailCard(
                                        modifier = Modifier
                                            .height(80.dp)
                                            .weight(1f)
                                            .padding(4.dp)
                                            .fillMaxWidth(0.5f),
                                        scoreDetail = score
                                    )
                                }
                                if (rowItems.size < 2) {
                                    Box(
                                        modifier = Modifier
                                            .height(80.dp)
                                            .weight(1f)
                                            .padding(4.dp)
                                            .fillMaxWidth(0.5f)
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }

        when {
        openInitDownloadDialog -> {
            DownloadDialog(checkResourceResult) {
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

    val id = if (scoreDetail.id == -1) {
        MaimaiData.getSongIdFromTitle(scoreDetail.name)
    } else {
        scoreDetail.id
    }

    Box(
        modifier = modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://assets2.lxns.net/maimai/jacket/$id.png")
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
                overflow = TextOverflow.Ellipsis,
                color = Color.White
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
                color = Color.White
            )
            Text(
                text = "DX Rating: ${floor(rating.toDouble()).toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

@Composable
fun ChuniScoreDetailCard(
    modifier: Modifier,
    scoreDetail: ChuniData.MusicDetail
) {
    var title = scoreDetail.name
    var level = scoreDetail.level
    var color = scoreDetail.diff.color
    var rating = scoreDetail.rating
    val id = if (scoreDetail.id == -1) {
        ChuniData.getSongIdFromTitle(title)
    } else {
        scoreDetail.id
    }

    Box(
        modifier = modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://assets2.lxns.net/chunithm/jacket/$id.png")
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
                overflow = TextOverflow.Ellipsis,
                color = Color.White
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
                text = NumberFormat.getNumberInstance().format(scoreDetail.score),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Rating: ${BigDecimal(rating.toDouble()).setScale(2, BigDecimal.ROUND_DOWN).toDouble()}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
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
            modifier = Modifier.fillMaxHeight().align(Alignment.Center),
            color = Color.White
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