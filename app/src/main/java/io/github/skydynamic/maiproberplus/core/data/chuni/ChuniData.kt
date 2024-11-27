package io.github.skydynamic.maiproberplus.core.data.chuni

import android.content.Context
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

class ChuniData {
    @Serializable
    data class Aliases(
        @SerialName("song_id") val songId: Int,
        val aliases: List<String>
    )

    @Serializable
    data class SongsAliases(val aliases: List<Aliases>)

    @Serializable
    data class SongDifficulty(
        val difficulty: Int,
        val level: String,
        @SerialName("level_value") val levelValue: Float,
        @SerialName("note_designer") val noteDesigner: String,
        val version: Int,
        val kanji: String = "",
        val star: Int = 0,
    )

    @Serializable
    data class SongInfo(
        val id: Int, val title: String, val artist: String, val genre: String,
        val bpm: Int, val version: Int, val difficulties: List<SongDifficulty>,
        val disabled: Boolean = false
    )

    @Serializable
    data class LxnsSongListResponse(val songs: List<SongInfo>)

    companion object {
        var CHUNI_SONG_LIST = readChuniSongList()
        var CHUNI_SONG_ALIASES = readChuniSongAliases()

        @OptIn(DelicateCoroutinesApi::class)
        fun syncMaimaiSongList() {
            val context = Application.application
            var listFile = File(context.filesDir, "chuni_song_list.json")

            GlobalScope.launch(Dispatchers.IO) {
                val result =
                    client.get("https://maimai.lxns.net/api/v0/chunithm/song/list?notes=true")
                listFile.deleteOnExit()
                listFile.createNewFile()
                val bufferedWriter =
                    context.openFileOutput("chuni_song_list.json", Context.MODE_PRIVATE)
                        .bufferedWriter()
                bufferedWriter.write(result.bodyAsText())
                bufferedWriter.close()
            }

            CHUNI_SONG_LIST = readChuniSongList()
        }

        private fun readChuniSongList(): List<SongInfo> {
            return JSON.decodeFromString<LxnsSongListResponse>(
                Application.application.getFilesDirInputStream("chuni_song_list.json")
                    .bufferedReader().use { it.readText() }
            ).songs
        }

        private fun readChuniSongAliases(): List<Aliases> {
            return JSON.decodeFromString<SongsAliases>(
                Application.application.getFilesDirInputStream("chuni_song_aliases.json")
                    .bufferedReader().use { it.readText() }
            ).aliases
        }

        fun getSongIdFromTitle(title: String): Int {
            return CHUNI_SONG_LIST.find { it.title == title }?.id ?: -1
        }

        fun getLevelValue(title: String, difficulty: ChuniEnums.Difficulty): Float {
            val level= CHUNI_SONG_LIST.find { it.title == title }
                ?.difficulties[difficulty.diffIndex]
                ?.levelValue
            return level ?: 0F
        }

        fun getChartVersion(title: String, difficulty: ChuniEnums.Difficulty): Int {
            val version= CHUNI_SONG_LIST.find { it.title == title }
                ?.difficulties[difficulty.diffIndex]
                ?.version
            return version ?: 0
        }
    }
}