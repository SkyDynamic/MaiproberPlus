package io.github.skydynamic.maiproberplus.ui.compose.scores.chuni

import kotlinx.serialization.Serializable

@Serializable
enum class ChuniScoreSortBy(val displayName: String) {
    Level("等级"),
    Score("分数"),
    Rating("Rating"),
    Difficulty("难度"),
}
