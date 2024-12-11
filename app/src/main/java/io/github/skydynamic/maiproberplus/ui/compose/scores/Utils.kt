package io.github.skydynamic.maiproberplus.ui.compose.scores

import io.github.skydynamic.maiproberplus.ui.compose.GameType
import io.github.skydynamic.maiproberplus.ui.compose.scores.chuni.refreshChuniScore
import io.github.skydynamic.maiproberplus.ui.compose.scores.maimai.refreshMaimaiScore
import io.github.skydynamic.maiproberplus.ui.compose.sync.FileDownloadMeta

val resources = listOf(
    FileDownloadMeta(
        "maimai_song_list.json",
        ".",
        "https://maimai.lxns.net/api/v0/maimai/song/list?notes=true"
    ),
    FileDownloadMeta(
        "chuni_song_list.json",
        ".",
        "https://maimai.lxns.net/api/v0/chunithm/song/list"
    ),
    FileDownloadMeta(
        "maimai_song_aliases.json",
        ".",
        "https://maimai.lxns.net/api/v0/maimai/alias/list"
    ),
    FileDownloadMeta(
        "chuni_song_aliases.json",
        ".",
        "https://maimai.lxns.net/api/v0/chunithm/alias/list"
    )
)

fun refreshScore(gameType: GameType) {
    when (gameType) {
        GameType.MaimaiDX -> refreshMaimaiScore()
        GameType.Chunithm -> refreshChuniScore()
    }
}

