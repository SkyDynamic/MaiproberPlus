package io.github.skydynamic.maiproberplus.ui.compose.scores.maimai

import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiScoreManager.getMaimaiScoreCache
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerViewModel

fun refreshMaimaiScore() {
    ScoreManagerViewModel.maimaiLoadedScores.clear()
    ScoreManagerViewModel.maimaiLoadedScores.addAll(getMaimaiScoreCache().sortedByDescending { it.rating })
    ScoreManagerViewModel.maimaiSearchScores.clear()
    ScoreManagerViewModel.maimaiSearchText.value = ""
}
