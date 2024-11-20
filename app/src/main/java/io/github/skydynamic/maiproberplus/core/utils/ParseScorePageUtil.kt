package io.github.skydynamic.maiproberplus.core.utils

import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import org.jsoup.Jsoup

fun calcScore(score: String, songLevel: Float): Int {
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
        val musicCards = document.getElementsByClass("w_450 m_15 p_r f_0")

        for (musicCard in musicCards) {
            val musicName = musicCard
                .getElementsByClass("music_name_block t_l f_13 break").text()
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

            val musicClearType = MaimaiEnums.ClearType
                .getClearTypeByScore(musicScore.replace("%", "").toFloat())
            var musicSyncType = MaimaiEnums.SyncType.NULL
            var musicSpecialClearType = MaimaiEnums.SpecialClearType.NULL

            val isDeluxe = musicCard.getElementsByClass("music_kind_icon")
                .attr("src")
                .contains("music_dx")
            val musicType = if (isDeluxe) MaimaiEnums.SongType.DX else MaimaiEnums.SongType.STANDARD

            val res = MaimaiData.MAIMAI_SONG_LIST.find { it.title == musicName }
            val musicLevel = if (isDeluxe) {
                if (res != null) res.difficulties.dx[difficulty.diffIndex].levelValue else -1f
            } else {
                if (res != null) res.difficulties.standard[difficulty.diffIndex].levelValue else -1f
            }

            val musicRating = calcScore(musicScore, musicLevel)
            val musicVersion = res?.version ?: 10000

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
                        "fc" -> musicSpecialClearType = MaimaiEnums.SpecialClearType.FC
                        "fcp" -> musicSpecialClearType = MaimaiEnums.SpecialClearType.FCP
                        "ap" -> musicSpecialClearType = MaimaiEnums.SpecialClearType.AP
                        "app" -> musicSpecialClearType = MaimaiEnums.SpecialClearType.APP
                    }
                }
            }
            musicList.add(
                MaimaiData.MusicDetail(
                    musicName, musicLevel,
                    musicScoreNum, musicDxScoreNum ?: 0,
                    musicRating, musicVersion,
                    musicType, difficulty,
                    musicClearType, musicSyncType,
                    musicSpecialClearType
                )
            )
        }
        return musicList
    }
}