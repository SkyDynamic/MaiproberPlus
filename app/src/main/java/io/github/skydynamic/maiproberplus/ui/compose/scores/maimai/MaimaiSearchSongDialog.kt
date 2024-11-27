package io.github.skydynamic.maiproberplus.ui.compose.scores.maimai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.ui.theme.getCardColor

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MaimaiSearchSongDialog(
    onConfirm: (MaimaiData.SongInfo) -> Unit,
    onDismissRequest: () -> Unit
) {
    val maimaiAliasMap: Map<Int, List<String>> by lazy {
        MaimaiData.MAIMAI_SONG_ALIASES.associateBy({ it.songId }, { it.aliases })
    }

    var songInfo: MaimaiData.SongInfo? by remember { mutableStateOf(null) }
    var searchText by remember { mutableStateOf("") }

    val allSongs = remember { MaimaiData.MAIMAI_SONG_LIST }
    val filteredSongs = remember(searchText) {
        allSongs.filter { songInfo ->
            songInfo.title.contains(searchText, ignoreCase = true) ||
                    maimaiAliasMap[
                        songInfo.id
                    ]?.any { alias ->
                        alias.contains(searchText, ignoreCase = true)
                    } == true || songInfo.artist.contains(searchText, ignoreCase = true)
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.width(350.dp).height(500.dp).padding(start = 16.dp, end = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(getCardColor())
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    label = {
                        Text("搜索歌曲")
                    }
                )

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(horizontal = 8.dp),
                    columns = GridCells.Fixed(1)
                ) {
                    items(filteredSongs) {
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable(onClick = {
                                    songInfo = it
                                    searchText = it.title
                                })
                        ) {
                            Column {
                                Text(
                                    text = it.title,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = it.artist,
                                    fontSize = 12.sp,
                                    color = Color(82, 82, 82, 255)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            if (songInfo != null) {
                                onConfirm(songInfo!!)
                            }
                            onDismissRequest()
                        }
                    ) {
                        Text("确定")
                    }
                }
            }
        }
    }
}