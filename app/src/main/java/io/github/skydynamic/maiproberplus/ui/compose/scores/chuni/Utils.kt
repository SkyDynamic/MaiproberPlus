package io.github.skydynamic.maiproberplus.ui.compose.scores.chuni

import androidx.lifecycle.viewModelScope
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniScoreManager.getChuniScoreCache
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.core.utils.compareByDescending
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun refreshChuniScore(
    clearSearch: Boolean = false
) {
    ScoreManagerViewModel.viewModelScope.launch(Dispatchers.IO) {
        ScoreManagerViewModel.chuniLoadedScores.clear()
        ScoreManagerViewModel.chuniLoadedScores.addAll(
            getChuniScoreCache().sortedWith(chuniScoreSortComparator)
        )
        if (clearSearch) {
            ScoreManagerViewModel.chuniSearchScores.clear()
            ScoreManagerViewModel.chuniSearchText.value = ""
        } else {
            ScoreManagerViewModel.searchChuniScore()
        }
    }
}

val chuniScoreSortComparator: Comparator<ChuniScoreEntity>
    get() = when (ScoreManagerViewModel.chuniScoreSortBy.value) {
        ChuniScoreSortBy.Level -> compareByDescending { it.level }
        ChuniScoreSortBy.Score -> compareByDescending { it.level }
        ChuniScoreSortBy.Rating -> compareByDescending { it.rating }
        ChuniScoreSortBy.Difficulty -> compareByDescending(
            {it.diff}, {it.level}, {it.score}
        )
    }
