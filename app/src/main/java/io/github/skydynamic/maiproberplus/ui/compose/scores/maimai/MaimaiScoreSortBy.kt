package io.github.skydynamic.maiproberplus.ui.compose.scores.maimai

import kotlinx.serialization.Serializable

@Serializable
enum class MaimaiScoreSortBy(val displayName: String) {
    Level("等级"),
    Achievement("达成率"),
    DxScore("DX 分数"),
    Rating("DX Rating"),
    Difficulty("难度"),
}
