package io.github.skydynamic.maiproberplus.core.data.maimai

import android.content.Context
import coil3.Bitmap
import io.github.skydynamic.maiproberplus.Application
import io.github.skydynamic.maiproberplus.core.prober.client
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

class MaimaiData {
    @Serializable
    data class Aliases(
        @SerialName("song_id") val songId: Int,
        val aliases: List<String>
    )

    @Serializable
    data class SongsAliases(val aliases: List<Aliases>)

    @Serializable
    data class Notes(
        val total: Int,
        val tap: Int,
        val hold: Int,
        val slide: Int,
        val touch: Int,
        @SerialName("break") val breakTotal: Int
    )

    @Serializable
    data class SongDiffculty(
        val type: MaimaiEnums.SongType,
        val difficulty: Int,
        val level: String,
        @SerialName("level_value") val levelValue: Float,
        @SerialName("note_designer") val noteDesigner: String,
        val version: Int,
        val notes: Notes
    )

    @Serializable
    data class SongDifficulties(val standard: List<SongDiffculty>, val dx: List<SongDiffculty>)

    @Serializable
    data class SongInfo(
        val id: Int, val title: String, val artist: String, val genre: String,
        val bpm: Int, val version: Int, val difficulties: SongDifficulties,
        val disabled: Boolean = false
    )

    @Serializable
    data class MusicDetail(
        val id: Int = -1,
        val name: String, val level: Float,
        val score: Float, val dxScore: Int,
        val rating: Int, val version: Int,
        val type: MaimaiEnums.SongType, val diff: MaimaiEnums.Difficulty,
        val rankType: MaimaiEnums.RankType, val syncType: MaimaiEnums.SyncType,
        val fullComboType: MaimaiEnums.FullComboType
    )

    @Serializable
    data class LxnsSongListResponse(val songs: List<SongInfo>)

    companion object {
        var MAIMAI_SONG_LIST = readMaimaiSongList()
        var MAIMAI_SONG_ALIASES = readMaimaiSongAliases()

        @OptIn(DelicateCoroutinesApi::class)
        fun syncMaimaiSongList() {
            val context = Application.application
            var listFile = File(context.filesDir, "maimai_song_list.json")

            GlobalScope.launch(Dispatchers.IO) {
                val result =
                    client.get("https://maimai.lxns.net/api/v0/maimai/song/list?notes=true")
                listFile.deleteOnExit()
                listFile.createNewFile()
                val bufferedWriter =
                    context.openFileOutput("maimai_song_list.json", Context.MODE_PRIVATE)
                        .bufferedWriter()
                bufferedWriter.write(result.bodyAsText())
                bufferedWriter.close()
            }

            MAIMAI_SONG_LIST = readMaimaiSongList()
        }

        private fun readMaimaiSongList(): List<SongInfo> {
            return JSON.decodeFromString<LxnsSongListResponse>(
                Application.application.getFilesDirInputStream("maimai_song_list.json")
                    .bufferedReader().use { it.readText() }
            ).songs
        }

        private fun readMaimaiSongAliases(): List<Aliases> {
            return JSON.decodeFromString<SongsAliases>(
                Application.application.getFilesDirInputStream("maimai_song_aliases.json")
                    .bufferedReader().use { it.readText() }
            ).aliases
        }

        fun getSongIdFromTitle(title: String): Int {
            return MAIMAI_SONG_LIST.find { it.title == title }?.id ?: -1
        }

        fun getLevelValue(
            title: String,
            diffculty: MaimaiEnums.Difficulty,
            type: MaimaiEnums.SongType
        ): Float {
            val difficulties = MAIMAI_SONG_LIST.find { it.title == title }?.difficulties
            return if (type == MaimaiEnums.SongType.DX) {
                difficulties?.dx[diffculty.diffIndex]?.levelValue ?: 0F
            } else if (type == MaimaiEnums.SongType.STANDARD) {
                difficulties?.standard[diffculty.diffIndex]?.levelValue ?: 0F
            } else {
                0F
            }
        }

        fun getChartVersion(
            title: String,
            diffculty: MaimaiEnums.Difficulty,
            type: MaimaiEnums.SongType
        ): Int {
            val difficulties = MAIMAI_SONG_LIST.find { it.title == title }?.difficulties
            return if (type == MaimaiEnums.SongType.DX) {
                difficulties?.dx[diffculty.diffIndex]?.version ?: 0
            } else if (type == MaimaiEnums.SongType.STANDARD) {
                difficulties?.standard[diffculty.diffIndex]?.version ?: 0
            } else {
                0
            }
        }

        fun getNoteTotal(
            title: String,
            diffculty: MaimaiEnums.Difficulty,
            type: MaimaiEnums.SongType
        ): Int {
            val difficulties = MAIMAI_SONG_LIST.find { it.title == title }?.difficulties
            return if (type == MaimaiEnums.SongType.DX) {
                difficulties?.dx[diffculty.diffIndex]?.notes?.total ?: 0
            } else if (type == MaimaiEnums.SongType.STANDARD) {
                difficulties?.standard[diffculty.diffIndex]?.notes?.total ?: 0
            } else {
                0
            }
        }

        fun getDxStar(
            noteTotal: Int,
            dxScore: Int
        ): Int {
            val value = dxScore.toDouble() / (noteTotal * 3)
            return when {
                value < 0.85 -> 0
                value < 0.9 -> 1
                value < 0.93 -> 2
                value < 0.95 -> 3
                value < 0.97 -> 4
                else -> 5
            }
        }

        fun getDxStarBitmap(dxStar: Int): Bitmap? {
            return Application.application
                .assetsManager
                .getMaimaiUIAssets("UI_GAM_Gauge_DXScoreIcon_0$dxStar.png")
        }
    }
}