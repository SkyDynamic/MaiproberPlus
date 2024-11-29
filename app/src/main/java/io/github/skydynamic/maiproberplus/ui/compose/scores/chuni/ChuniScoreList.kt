package io.github.skydynamic.maiproberplus.ui.compose.scores.chuni

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.skydynamic.maiproberplus.R
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniScoreManager.deleteAllScore
import io.github.skydynamic.maiproberplus.ui.compose.ConfirmDialog
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChuniScoreList(
    coroutineScope: CoroutineScope
) {
    var openDeleteConfirmDialog by remember { mutableStateOf(false) }

    val gridState = rememberLazyGridState()

    when {
        openDeleteConfirmDialog -> {
            ConfirmDialog(
                info = "你确定要删除所有成绩吗？\n该操纵不可逆!",
                onDismiss = {
                    openDeleteConfirmDialog = false
                },
                onRequest = {
                    deleteAllScore()
                    ScoreManagerViewModel.searchChuniScore("")
                    ScoreManagerViewModel.chuniSearchCache.clear()
                }
            )
        }
        ScoreManagerViewModel.showChuniScoreSelectionDialog -> {
            if (ScoreManagerViewModel.chuniScoreSelection == null) {
                ScoreManagerViewModel.showChuniScoreSelectionDialog = false
            } else {
                ChuniScoreDetailDialog(ScoreManagerViewModel.chuniScoreSelection!!) {
                    ScoreManagerViewModel.showChuniScoreSelectionDialog = false
                    ScoreManagerViewModel.chuniScoreSelection = null
                }
            }
        }
    }

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
                    value = ScoreManagerViewModel.chuniSearchText.value,
                    onValueChange = {
                        ScoreManagerViewModel.chuniSearchText.value = it
                        ScoreManagerViewModel.searchChuniScore(it)
                    },
                    modifier = Modifier
                        .weight(1f),
                    label = { Text("搜索曲目或者曲目别名", fontSize = 12.sp) },
                    trailingIcon = {
                        if (ScoreManagerViewModel.chuniSearchText.value.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    ScoreManagerViewModel.chuniSearchText.value = ""
                                    ScoreManagerViewModel.searchChuniScore("")
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
                        ScoreManagerViewModel.chuniLoadedScores.clear()
                        ScoreManagerViewModel.chuniSearchScores.clear()
                        refreshChuniScore()

                    }
                ) {
                    Text("刷新列表")
                }
            }
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
                Button(
                    modifier = Modifier.padding(8.dp).weight(1f),
                    onClick = {
                        openDeleteConfirmDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Icon(Icons.Default.Delete, null, tint = Color.White)
                    Text("删除所有成绩", color = Color.White)
                }
            }
        }

        items(
            if (ScoreManagerViewModel.chuniSearchText.value.isNotEmpty()) {
                ScoreManagerViewModel.chuniSearchScores
            } else {
                ScoreManagerViewModel.chuniLoadedScores
            }
        ) {
            ChuniScoreDetailCard(
                modifier = Modifier
                    .height(80.dp)
//                    .fillMaxWidth()
                    .padding(4.dp),
                scoreDetail = it,
                onClick = {
                    ScoreManagerViewModel.showChuniScoreSelectionDialog = true
                    ScoreManagerViewModel.chuniScoreSelection = it
                }
            )
        }
    }

    AnimatedVisibility(
        visible = gridState.canScrollBackward,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            Modifier
                .fillMaxSize()
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        gridState.scrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painterResource(R.drawable.arrow_upward_24px),
                    contentDescription = null
                )
            }
        }
    }
}