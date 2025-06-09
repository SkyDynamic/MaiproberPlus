package io.github.skydynamic.maiproberplus.ui.compose.scores.chuni

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.R
import io.github.skydynamic.maiproberplus.core.config.ScoreDisplayType
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniScoreManager.deleteAllScore
import io.github.skydynamic.maiproberplus.ui.component.ConfirmDialog
import io.github.skydynamic.maiproberplus.ui.component.WindowInsetsSpacer
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChuniScoreList(
    coroutineScope: CoroutineScope
) {
    var openDeleteConfirmDialog by remember { mutableStateOf(false) }
    var openSortByDialog by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    var loadedItemCount by remember { mutableIntStateOf(30) }
    val scoreDisplayType = application.configManager.config.scoreDisplayType
    val scoreColorOverlayType = application.configManager.config.scoreStyleType
    val gridColumnsNumber =
        when (scoreDisplayType) {
            ScoreDisplayType.Large -> 1
            else -> 2
        } * if (application.isLandscape) 2 else 1

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex * gridColumnsNumber }
            .collect { lastVisibleIndex ->
                val totalItemsCount = if (ScoreManagerViewModel.chuniSearchText.value.isNotEmpty()) {
                    ScoreManagerViewModel.chuniSearchScores.size
                } else {
                    ScoreManagerViewModel.chuniLoadedScores.size
                }

                if (lastVisibleIndex >= loadedItemCount - 1 && loadedItemCount < totalItemsCount) {
                    delay(1000)
                    loadedItemCount += 20
                }
            }
    }

    when {
        openSortByDialog -> {
            ChuniScoreSortByDialog(
                onDismissRequest = {
                    openSortByDialog = false
                    refreshChuniScore(false)
                }
            )
        }
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
        ScoreManagerViewModel.openChuniCreateScoreDialog -> {
            ChuniCreateScoreDialog(
                onDismissRequest = {
                    ScoreManagerViewModel.openChuniCreateScoreDialog = false
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
        columns = GridCells.Fixed(gridColumnsNumber),
        state = gridState
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Column {
                WindowInsetsSpacer.TopPaddingSpacer()
                Spacer(Modifier.height(64.dp))
            }
        }
        // Top Spacer

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
                        ScoreManagerViewModel.searchChuniScore()
                    },
                    singleLine = true,
                    label = { Text("搜索曲名或别名", fontSize = 12.sp) },
                    leadingIcon = {
                        IconButton(
                            onClick = {
                                openSortByDialog = true
                            },
                        ) {
                            Icon(
                                painterResource(R.drawable.sort_24dp),
                                "排序"
                            )
                        }

                    },
                    trailingIcon = {
                        if (ScoreManagerViewModel.chuniSearchText.value.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    ScoreManagerViewModel.chuniSearchText.value = ""
                                    ScoreManagerViewModel.searchChuniScore()
                                }
                            ) {
                                Icon(Icons.Default.Clear, "清除搜索")
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f),
                )

                FloatingActionButton(
                    onClick = {
                        ScoreManagerViewModel.chuniLoadedScores.clear()
                        ScoreManagerViewModel.chuniSearchScores.clear()
                        refreshChuniScore()
                    },
                    modifier = Modifier
                        .padding(top = 6.dp)
                ) {
                    Icon(Icons.Filled.Refresh, "刷新")
                }
            }
        }
        // Sort & Search & Refresh

        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        ScoreManagerViewModel.openChuniCreateScoreDialog = true
                    }
                ) {
                    Icon(Icons.Default.Add, null)
                    Text("新增成绩")
                }

                Button(
                    modifier = Modifier.weight(1f),
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
        // Add & Del All

        items(
            if (ScoreManagerViewModel.chuniSearchText.value.isNotEmpty()) {
                ScoreManagerViewModel.chuniSearchScores.take(loadedItemCount)
            } else {
                ScoreManagerViewModel.chuniLoadedScores.take(loadedItemCount)
            }
        ) {
            ChuniScoreDetailCard(
                modifier = Modifier
                    .padding(4.dp),
                scoreDisplayType = scoreDisplayType,
                scoreStyleType = scoreColorOverlayType,
                scoreDetail = it,
                onClick = {
                    ScoreManagerViewModel.showChuniScoreSelectionDialog = true
                    ScoreManagerViewModel.chuniScoreSelection = it
                }
            )
        }

        item(
            span = { GridItemSpan(maxCurrentLineSpan) }
        ) {
            Box(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                if (
                    loadedItemCount >=
                        if (ScoreManagerViewModel.chuniSearchText.value.isNotEmpty())
                            ScoreManagerViewModel.chuniSearchScores.size
                        else ScoreManagerViewModel.chuniLoadedScores.size
                ) {
                    Text("到底了...")
                } else {
                    Text("加载中...")
                }
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            if (application.isLandscape) {
                WindowInsetsSpacer.BottomPaddingSpacer()
            }
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
                    .padding(
                        bottom = if (application.isLandscape) {
                            WindowInsetsSpacer.bottomPadding
                        } else {
                            0.dp
                        },
                        end =WindowInsetsSpacer.endPadding,
                    )
            ) {
                Icon(
                    painterResource(R.drawable.arrow_upward_24px),
                    contentDescription = null
                )
            }
        }
    }
}