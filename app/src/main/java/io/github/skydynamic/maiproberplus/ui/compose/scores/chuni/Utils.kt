package io.github.skydynamic.maiproberplus.ui.compose.scores.chuni

import androidx.lifecycle.viewModelScope
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniScoreManager.getChuniScoreCache
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun refreshChuniScore() {
    ScoreManagerViewModel.viewModelScope.launch(Dispatchers.IO) {
        ScoreManagerViewModel.chuniLoadedScores.clear()
        ScoreManagerViewModel.chuniLoadedScores.addAll(getChuniScoreCache().sortedByDescending {
            it.rating
        })
    }
}