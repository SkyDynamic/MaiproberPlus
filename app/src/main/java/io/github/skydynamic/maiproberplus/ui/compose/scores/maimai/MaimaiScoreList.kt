package io.github.skydynamic.maiproberplus.ui.compose.scores.maimai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerViewModel

@Composable
fun MaimaiScoreList(gridState: LazyGridState) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        columns = GridCells.Fixed(2),
        state = gridState
    ) {
        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {
            Spacer(Modifier.height(64.dp))
        }
        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = ScoreManagerViewModel.maimaiSearchText.value,
                    onValueChange = {
                        ScoreManagerViewModel.maimaiSearchText.value = it
                        ScoreManagerViewModel.searchMaimaiScore(it)
                    },
                    modifier = Modifier
                        .weight(1f),
                    label = { Text("搜索曲目或者曲目别名", fontSize = 12.sp) },
                    trailingIcon = {
                        if (ScoreManagerViewModel.maimaiSearchText.value.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    ScoreManagerViewModel.maimaiSearchText.value = ""
                                    ScoreManagerViewModel.searchMaimaiScore("")
                                }
                            ) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    },
                )

                Button(
                    modifier = Modifier
                        .padding(top = 6.dp),
                    onClick = {
                        ScoreManagerViewModel.maimaiLoadedScores.clear()
                        ScoreManagerViewModel.maimaiSearchScores.clear()
                        refreshMaimaiScore()
                    }
                ) {
                    Text("刷新列表")
                }
            }
        }
        items(
            if (ScoreManagerViewModel.maimaiSearchText.value.isNotEmpty()) {
                ScoreManagerViewModel.maimaiSearchScores
            } else {
                ScoreManagerViewModel.maimaiLoadedScores
            }
        ) {
            MaimaiScoreDetailCard(
                modifier = Modifier
                    .height(80.dp)
                    .padding(4.dp),
                scoreDetail = it,
            )
        }
    }
}
