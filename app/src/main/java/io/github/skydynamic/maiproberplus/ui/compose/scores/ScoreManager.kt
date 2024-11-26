package io.github.skydynamic.maiproberplus.ui.compose.scores

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiproberplus.R
import io.github.skydynamic.maiproberplus.ui.compose.DownloadDialog
import io.github.skydynamic.maiproberplus.ui.compose.GameType
import io.github.skydynamic.maiproberplus.ui.compose.scores.chuni.ChuniScoreList
import io.github.skydynamic.maiproberplus.ui.compose.scores.maimai.MaimaiScoreDetailDialog
import io.github.skydynamic.maiproberplus.ui.compose.scores.maimai.MaimaiScoreList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
fun ScoreManager() {
    var gameType by remember { mutableStateOf(GameType.MaimaiDX) }
    var openInitDownloadDialog by remember { mutableStateOf(false) }
    var canShow by remember { mutableStateOf(false) }

    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    val checkResourceResult = checkResourceComplete()
    if (checkResourceResult.isNotEmpty()) {
        openInitDownloadDialog = true
    } else {
        canShow = true
    }

    LaunchedEffect(canShow) {
        if (canShow) {
            refreshScore(gameType)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (canShow) {
            refreshScore(gameType)
            when (gameType) {
                GameType.MaimaiDX -> MaimaiScoreList(gridState)
                GameType.Chunithm -> ChuniScoreList(gridState)
            }
        }
        
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            GameType.entries.forEach {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = it.ordinal, count = GameType.entries.size
                    ),
                    selected = gameType == it,
                    onClick = {
                        gameType = it
                        refreshScore(it)
                    },
                ) {
                    Text(it.displayName)
                }
            }
        }

        FloatingActionButton(
            onClick = {
                scope.launch {
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

    when {
        openInitDownloadDialog -> {
            DownloadDialog(checkResourceResult) {
                openInitDownloadDialog = false
                canShow = true
            }
        }

        ScoreManagerViewModel.showMaimaiScoreSelectionDialog -> {
            val scoreDetail = ScoreManagerViewModel.maimaiScoreSelection
            if (scoreDetail != null) {
                MaimaiScoreDetailDialog(
                    scoreDetail = scoreDetail,
                    onDismissRequest = {
                        ScoreManagerViewModel.showMaimaiScoreSelectionDialog = false
                        ScoreManagerViewModel.maimaiScoreSelection = null
                    }
                )
            }
        }
    }
}
