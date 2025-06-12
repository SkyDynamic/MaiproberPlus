package io.github.skydynamic.maiproberplus.core.prober

import android.util.Log
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniData
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniEnums
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniScoreManager.writeChuniScoreCache
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiScoreManager.writeMaimaiScoreCache
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import io.github.skydynamic.maiproberplus.core.prober.models.divingfish.DivingFishGetChuniSCoreResponse
import io.github.skydynamic.maiproberplus.core.prober.models.divingfish.DivingFishGetMaimaiScoresResponse
import io.github.skydynamic.maiproberplus.core.prober.models.divingfish.DivingFishMaimaiScoreBody
import io.github.skydynamic.maiproberplus.core.prober.models.divingfish.DivingFishPlayerProfile
import io.github.skydynamic.maiproberplus.core.utils.ParseScorePageUtil
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class DivingFishProberUtil : IProberUtil {
    private val baseApiUrl = "https://www.diving-fish.com/api"

    override suspend fun updateUserInfo(importToken: String) {
        val resp = client.get("https://www.diving-fish.com/api/maimaidxprober/player/profile") {
            headers {
                append("Import-Token", importToken)
            }
        }
        val data = resp.body<DivingFishPlayerProfile>()
        application.configManager.config.userInfo.name = data.username
        application.configManager.config.userInfo.maimaiDan = data.additionalRating
        application.configManager.save()
    }

    override suspend fun uploadMaimaiProberData(
        importToken: String,
        authUrl: String
    ) {
        val isCache = application.configManager.config.localConfig.cacheScore
        val scores = mutableListOf<MaimaiScoreEntity>()

        application.sendNotification("水鱼查分器", "正在进行查分")
        sendMessageToUi("开始获取舞萌DX数据并上传到水鱼查分器")
        fetchMaimaiScorePage(authUrl) { diff, body ->
            Log.i("DivingFishProberUtil", "正在上传${diff.diffName}成绩到水鱼查分器")
            try {
                val result = client.post("$baseApiUrl/pageparser/page") {
                    headers {
                        append(HttpHeaders.ContentType, "text/plain")
                    }
                    contentType(ContentType.Text.Plain)
                    setBody(body)
                }

                if (isCache) {
                    val scoreBody = result.body<List<DivingFishMaimaiScoreBody>>()
                    scoreBody.forEach { score ->
                        val res = MaimaiData.MAIMAI_SONG_LIST.find { it.title == score.title }
                        if (res != null) {
                            var version =0
                            var songType = MaimaiEnums.SongType.STANDARD
                            var level = 0F
                            if (score.type == "DX") {
                                res.difficulties.dx[score.levelIndex].version
                                songType = MaimaiEnums.SongType.DX
                                level = res.difficulties.dx[score.levelIndex].levelValue
                            } else {
                                res.difficulties.standard[score.levelIndex].version
                                level = res.difficulties.standard[score.levelIndex].levelValue
                            }
                            scores.add(MaimaiScoreEntity(
                                songId = res.id,
                                title = score.title,
                                level = score.ds,
                                achievement = score.achievements,
                                dxScore = score.dxScore,
                                rating = score.ra,
                                version = version,
                                type = songType,
                                diff = MaimaiEnums.Difficulty.getDifficultyWithIndex(score.levelIndex),
                                rankType = MaimaiEnums.RankType.getRankTypeByScore(score.achievements),
                                syncType = MaimaiEnums.SyncType.getSyncTypeByName(score.fs),
                                fullComboType = MaimaiEnums.FullComboType.getFullComboTypeByName(score.fc),
                            ))
                        }
                    }
                }

                val postResult = client.post("$baseApiUrl/maimaidxprober/player/update_records") {
                    headers {
                        append("Import-Token", importToken)
                        append(HttpHeaders.ContentType, "application/json")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(result.bodyAsText())
                }
                Log.i("DivingFishProberUtil", "已上传${diff.diffName}成绩到水鱼查分器, 接口信息: ${postResult.bodyAsText()}")
            } catch (e: Exception) {
                Log.e("DivingFishProberUtil", "上传${diff.diffName}成绩到水鱼查分器失败", e)
            }
        }
        sendMessageToUi("上传舞萌DX成绩到水鱼查分器完成")
        Log.d("DivingFishProberUtil", "上传完毕")
        GlobalViewModel.maimaiHooking = false
        application.sendNotification("水鱼查分器", "查分完毕")
        if (isCache) {
            writeMaimaiScoreCache(scores)
        }
    }

    override suspend fun uploadChunithmProberData(
        importToken: String,
        authUrl: String
    ) {
        val isCache = application.configManager.config.localConfig.cacheScore
        val scores = mutableListOf<ChuniScoreEntity>()

        application.sendNotification("水鱼查分器", "正在进行查分")
        sendMessageToUi("开始获取中二节奏数据并上传到水鱼查分器")
        fetchChuniScores(authUrl) { diff, body ->
            Log.i("DivingFishProberUtil", "正在上传${diff.diffName}成绩到水鱼查分器")
            val recentParam = if (diff.diffName.lowercase().contains("recent")) "?recent=1" else ""
            try {
                client.post("$baseApiUrl/chunithmprober/player/update_records_html$recentParam") {
                    headers {
                        append("Import-Token", importToken)
                        append(HttpHeaders.ContentType, "text/plain")
                    }
                    contentType(ContentType.Text.Plain)
                    setBody(body)
                }

                if (isCache) {
                    scores.addAll(ParseScorePageUtil.parseChuni(body, diff))
                }

                Log.i("DivingFishProberUtil", "已上传${diff.diffName}成绩到水鱼查分器")
            } catch (e: Exception) {
                Log.e("DivingFishProberUtil", "上传${diff.diffName}成绩到水鱼查分器失败", e)
            }
        }
        sendMessageToUi("上传中二节奏成绩到水鱼查分器完成")
        Log.d("DivingFishProberUtil", "上传完毕")
        GlobalViewModel.chuniHooking = false
        application.sendNotification("水鱼查分器", "查分完毕")
        if (isCache) {
            writeChuniScoreCache(scores)
        }
    }

    override suspend fun getMaimaiProberData(importToken: String): List<MaimaiScoreEntity> {
        try {
            val result = client.get("$baseApiUrl/maimaidxprober/player/records") {
                headers {
                    append("Import-Token", importToken)
                }
            }
            val body = result.body<DivingFishGetMaimaiScoresResponse>()
            val scores = mutableListOf<MaimaiScoreEntity>()
            body.records.forEach {
                val type = MaimaiEnums.SongType.getSongTypeByName(it.type)
                val diff = MaimaiEnums.Difficulty.getDifficultyWithIndex(it.levelIndex)
                val levelValue = MaimaiData.getLevelValue(it.title, diff, type)
                val version = MaimaiData.getChartVersion(it.title, diff, type)
                scores.add(
                    MaimaiScoreEntity(
                        songId = MaimaiData.getSongIdFromTitle(it.title),
                        title = it.title,
                        level = levelValue,
                        achievement = it.achievements,
                        dxScore = it.dxScore,
                        rating = it.ra,
                        version = version,
                        type = type,
                        diff = diff,
                        rankType = MaimaiEnums.RankType.getRankTypeByScore(it.achievements),
                        syncType = MaimaiEnums.SyncType.getSyncTypeByName(it.fs),
                        fullComboType = MaimaiEnums.FullComboType.getFullComboTypeByName(it.fc)
                    )
                )
            }
            return scores
        } catch (e: Exception) {
            Log.e("DivingFishProberUtil", "获取舞萌DX成绩失败", e)
            sendMessageToUi("获取舞萌DX成绩失败")
            return emptyList()
        }
    }

    override suspend fun getChuniProberData(importToken: String): List<ChuniScoreEntity> {
        try {
            val result = client.get("$baseApiUrl/chunithmprober/player/records") {
                headers {
                    append("Import-Token", importToken)
                }
            }
            val body = result.body<DivingFishGetChuniSCoreResponse>()
            val scores = arrayListOf<ChuniScoreEntity>()
            body.records.best.forEach {
                val diff = ChuniEnums.Difficulty.getDifficultyWithIndex(it.levelIndex)
                val version = ChuniData.getChartVersion(it.title, diff)
                scores.add(
                    ChuniScoreEntity(
                        songId = ChuniData.getSongIdFromTitle(it.title),
                        title = it.title,
                        level = it.ds,
                        score = it.score,
                        rating = it.ra,
                        version = version,
                        diff = diff,
                        rankType = ChuniEnums.RankType.getRankTypeByScore(it.score),
                        fullComboType = ChuniEnums.FullComboType.NULL,
                        fullChainType = ChuniEnums.FullChainType.NULL,
                        clearType = ChuniEnums.ClearType.FAILED
                    )
                )
            }
            return scores
        } catch (e: Exception) {
            Log.e("DivingFishProberUtil", "获取中二节奏成绩失败", e)
            sendMessageToUi("获取中二节奏成绩失败")
            return emptyList()
        }
    }
}