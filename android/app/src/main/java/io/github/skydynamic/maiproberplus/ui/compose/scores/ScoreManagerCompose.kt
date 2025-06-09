package io.github.skydynamic.maiproberplus.ui.compose.scores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiproberplus.core.data.GameType
import io.github.skydynamic.maiproberplus.core.utils.checkResourceComplete
import io.github.skydynamic.maiproberplus.ui.component.DownloadDialog
import io.github.skydynamic.maiproberplus.ui.component.WindowInsetsSpacer
import io.github.skydynamic.maiproberplus.ui.compose.scores.chuni.ChuniScoreList
import io.github.skydynamic.maiproberplus.ui.compose.scores.maimai.MaimaiScoreList

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ScoreManagerCompose() {
    var gameType by remember { mutableStateOf(GameType.MaimaiDX) }
    var openInitDownloadDialog by remember { mutableStateOf(false) }
    var canShow by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

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
                GameType.MaimaiDX -> MaimaiScoreList(coroutineScope)
                GameType.Chunithm -> ChuniScoreList(coroutineScope)
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(WindowInsetsSpacer.topPadding)
                .align(Alignment.TopCenter)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
        )
        
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .padding(top = WindowInsetsSpacer.topPadding)
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
