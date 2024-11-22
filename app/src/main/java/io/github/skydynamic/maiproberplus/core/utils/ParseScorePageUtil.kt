package io.github.skydynamic.maiproberplus.core.utils

import android.annotation.SuppressLint
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniData
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniEnums
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import org.jsoup.Jsoup
import org.jsoup.nodes.Entities
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun calcMaimaiScore(score: String, songLevel: Float): Int {
    var formatScore = score.replace("%", "").toFloat()
    val multiplierFactor = when (formatScore) {
        in 10.0000..19.9999 -> 0.016 // D
        in 20.0000..29.9999 -> 0.032 // D
        in 30.0000..39.9999 -> 0.048 // D
        in 40.0000..49.9999 -> 0.064 // D
        in 50.0000..59.9999 -> 0.080 // C
        in 60.0000..69.9999 -> 0.096 // B
        in 70.0000..74.9999 -> 0.112 // BB
        in 75.0000..79.9999 -> 0.120 // BBB
        in 80.0000..89.9999 -> 0.128 // A
        in 90.0000..93.9999 -> 0.152 // AA
        in 94.0000..96.9998 -> 0.168 // AAA
        in 96.9999..96.9999 -> 0.176 // AAA
        in 97.0000..97.9999 -> 0.200 // S
        in 98.0000..98.9998 -> 0.203 // S+
        in 98.9999..98.9999 -> 0.206 // S+
        in 99.0000..99.4999 -> 0.208 // SS
        in 99.5000..99.9998 -> 0.211 // SS+
        in 99.9999..99.9999 -> 0.214 // SSS+
        in 100.0000..100.4998 -> 0.216 // SSS
        in 100.4999..100.4999 -> 0.222 // SSS
        in 100.5000..101.0000 -> 0.224 // SSS+
        else -> 0.000 // < 10.0000%
    }
    if (formatScore >= 100.5) formatScore = 100.5F
    return (songLevel * multiplierFactor * formatScore).toInt()
}

@SuppressLint("DefaultLocale")
fun calcChuniScore(score: Int, songLevel: Float): Float {
        return when {
            score >= 1009000 -> songLevel + 2.15f
            score >= 1007500 -> songLevel + 2.0f + (score - 1007500) / 100.0 * 0.01
            score >= 1005000 -> songLevel + 1.5f + (score - 1005000) / 500.0 * 0.1
            score >= 1000000 -> songLevel + 1.0f + (score - 1000000) / 1000.0 * 0.1
            score >= 990000 -> songLevel + (score - 990000) / 2500.0 * 0.1
            score >= 975000 -> songLevel + (score - 975000) / 2500.0 * 0.1
            score >= 925000 -> songLevel - 3.0f
            score >= 900000 -> songLevel - 5.0f
            score >= 800000 -> (songLevel - 5.0f) / 2
            score >= 500000 -> 0.0f
            else -> 0.0f
        }.let { String.format("%.2f", it).toFloat() }
}

fun extractDxScoreNum(input: String): Int? {
    val regex = Regex("""\d{1,3}(,\d{3})*""")
    val matchResult = regex.find(input)

    return matchResult?.value?.replace(",", "")?.toInt()
}

object ParseScorePageUtil {
    @JvmStatic
    fun parseMaimai(
        html: String,
        difficulty: MaimaiEnums.Difficulty
    ): List<MaimaiData.MusicDetail> {
        if (html.isEmpty()) {
            return emptyList()
        }
        val musicList = ArrayList<MaimaiData.MusicDetail>()

        val document = Jsoup.parse(html)
        document.outputSettings().prettyPrint(false)
        val musicCards = document.getElementsByClass("w_450 m_15 p_r f_0")

        for (musicCard in musicCards) {
            val musicName = Entities.unescape(musicCard
                .getElementsByClass("music_name_block t_l f_13 break").html())
            val musicScore = musicCard
                .getElementsByClass("music_score_block w_112 t_r f_l f_12").text()
            val musicDxScore = musicCard
                .getElementsByClass("music_score_block w_190 t_r f_l f_12").text()

            if (musicScore.isEmpty() && musicDxScore.isEmpty()) {
                continue
            }

            val musicScoreNum = musicScore.replace("%", "").toFloat()
            val musicDxScoreNum = extractDxScoreNum(musicDxScore)

            val musicClearTypes = musicCard.getElementsByClass("h_30 f_r")

            val musicRankType = MaimaiEnums.RankType
                .getRankTypeByScore(musicScore.replace("%", "").toFloat())
            var musicSyncType = MaimaiEnums.SyncType.NULL
            var musicFullComboType = MaimaiEnums.FullComboType.NULL

            val isDeluxe = musicCard.getElementsByClass("music_kind_icon")
                .attr("src")
                .contains("music_dx")
            val musicType = if (isDeluxe) MaimaiEnums.SongType.DX else MaimaiEnums.SongType.STANDARD

            val res = MaimaiData.MAIMAI_SONG_LIST.find { it.title == musicName }
            if (res == null) continue
            if (res.disabled == true) { continue }
            val musicLevel = if (isDeluxe) {
                res.difficulties.dx[difficulty.diffIndex].levelValue
            } else {
                res.difficulties.standard[difficulty.diffIndex].levelValue
            }

            val musicRating = calcMaimaiScore(musicScore, musicLevel)
            val musicVersion = res.version

            for (musicClearTypeElement in musicClearTypes) {
                val regex = Regex(".*music_icon_(.*?)?.png?.*")
                val value = regex.find(musicClearTypeElement.attr("src"))?.groupValues?.get(1)
                if (value != null) {
                    when (value) {
                        "sync" -> musicSyncType = MaimaiEnums.SyncType.SYNC
                        "fs" -> musicSyncType = MaimaiEnums.SyncType.FS
                        "fsp" -> musicSyncType = MaimaiEnums.SyncType.FSP
                        "fdx" -> musicSyncType = MaimaiEnums.SyncType.FDX
                        "fdxp" -> musicSyncType = MaimaiEnums.SyncType.FDXP
                        "fc" -> musicFullComboType = MaimaiEnums.FullComboType.FC
                        "fcp" -> musicFullComboType = MaimaiEnums.FullComboType.FCP
                        "ap" -> musicFullComboType = MaimaiEnums.FullComboType.AP
                        "app" -> musicFullComboType = MaimaiEnums.FullComboType.APP
                    }
                }
            }
            musicList.add(
                MaimaiData.MusicDetail(
                    musicName, musicLevel,
                    musicScoreNum, musicDxScoreNum ?: 0,
                    musicRating, musicVersion,
                    musicType, difficulty,
                    musicRankType, musicSyncType,
                    musicFullComboType
                )
            )
        }
        return musicList
    }

    fun parseChuni(
        html: String,
        difficulty: ChuniEnums.Difficulty
    ): List<ChuniData.MusicDetail> {
        if (html.isEmpty()) {
            return emptyList()
        }
        val musicList = ArrayList<ChuniData.MusicDetail>()

        val document = Jsoup.parse(html)
        document.outputSettings().prettyPrint(false)

        val musicListBox = document.getElementsByClass("musiclist_box")
        for (musicListBoxElement in musicListBox) {
            val highScore = musicListBoxElement.getElementsByClass("play_musicdata_highscore")
            if (highScore.text().isEmpty()) {
                continue
            }

            val musicName = Entities.unescape(
                musicListBoxElement.getElementsByClass("music_title").html().takeIf { it.isNotBlank() }
                    ?: musicListBoxElement.getElementsByClass("musiclist_worldsend_title").html()
            )

            val musicScore = highScore[0].tagName("span").text()
            val musicScoreNum = musicScore.replace("分数：", "").replace(",", "").toInt()

            var musicPlayTime = ""
            val musicDifficulty = if (difficulty == ChuniEnums.Difficulty.RECENT) {
                val cl = musicListBoxElement.attr("class")
                val regex = Regex("bg_(\\w+)")
                val matchResult = regex.find(cl)?.groups?.get(1)?.value ?: ""
                musicPlayTime = getCurrentUTCTimeFormatted()
                ChuniEnums.Difficulty.getDifficultyWithName(matchResult)
            } else {
                difficulty
            }

            val res = if (musicDifficulty != ChuniEnums.Difficulty.WORLDSEND) {
                ChuniData.CHUNI_SONG_LIST.find { it.title == musicName }
            } else {
                ChuniData.CHUNI_SONG_LIST.findLast { it.title == musicName && it.difficulties[0].kanji != ""}
            }

            if (res == null) { continue }

            if (res.disabled == true) { continue }

            val musicLevel = if (musicDifficulty != ChuniEnums.Difficulty.WORLDSEND) {
                res.difficulties[musicDifficulty.diffIndex].levelValue
            } else {
                res.difficulties[0].levelValue
            }

            val musicRating = calcChuniScore(musicScoreNum, musicLevel)

            val musicVersion = res.version

            var clearType = ChuniEnums.ClearType.FAILED
            var fullComboType = ChuniEnums.FullComboType.NULL
            var fullChainType = ChuniEnums.FullChainType.NULL

            val icons = musicListBoxElement.getElementsByClass("play_musicdata_icon")
            if (icons.isNotEmpty()) {
                for (iconContainer in icons) {
                    val imgs = iconContainer.select("img")
                    for (icon in imgs) {
                        val regex = Regex(".*icon_(.*?)?.png?.*")
                        val value = regex.find(icon.attr("src"))?.groupValues?.get(1)
                        if (value != null) {
                            when(value) {
                                "fullcombo" -> fullComboType = ChuniEnums.FullComboType.FC
                                "alljustice" -> fullComboType = ChuniEnums.FullComboType.AJ
                                "alljusticecritical" -> fullComboType = ChuniEnums.FullComboType.AJC
                                "fullchain" -> fullChainType = ChuniEnums.FullChainType.FC
                                "fullchain2" -> fullChainType = ChuniEnums.FullChainType.GFC
                                "clear" -> clearType = ChuniEnums.ClearType.CLEAR
                                "hard" -> clearType = ChuniEnums.ClearType.HARD
                                "absolute" -> clearType = ChuniEnums.ClearType.ABSOLUTE
                                "absolutep" -> clearType = ChuniEnums.ClearType.ABSOLUTEP
                                "catastrophy" -> clearType = ChuniEnums.ClearType.CATASTROPHY
                            }
                        }
                    }
                }
                musicList.add(ChuniData.MusicDetail(
                    id = res.id,
                    name = musicName,
                    level = musicLevel,
                    score = musicScoreNum,
                    rating = musicRating,
                    version = musicVersion,
                    rankType = ChuniEnums.RankType.getRankTypeByScore(musicScoreNum),
                    diff = musicDifficulty,
                    fullComboType = fullComboType,
                    clearType = clearType,
                    fullChainType = fullChainType,
                    playTime = musicPlayTime
                ))
            }
        }
        return musicList
    }
}

fun getCurrentUTCTimeFormatted(): String {
    val now = ZonedDateTime.now(java.time.ZoneOffset.UTC)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    return now.format(formatter)
}