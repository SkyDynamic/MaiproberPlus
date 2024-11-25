package io.github.skydynamic.maiproberplus.ui.compose.scores.maimai

import androidx.lifecycle.viewModelScope
import io.github.skydynamic.maiproberplus.core.prober.getMaimaiScoreCache
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun refreshMaimaiScore() {
    ScoreManagerViewModel.viewModelScope.launch(Dispatchers.IO) {
        ScoreManagerViewModel.maimaiLoadedScores.clear()
        ScoreManagerViewModel.maimaiLoadedScores.addAll(getMaimaiScoreCache().sortedByDescending {
            it.rating
        })
    }
}
