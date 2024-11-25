package io.github.skydynamic.maiproberplus.core.prober

import android.util.Log
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.ui.compose.application
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.floor

class LxnsProberUtil : IProberUtil {

    private val baseApiUrl = "https://maimai.lxns.net"

    @Serializable
    open class LxnsResponse(
        val success: Boolean = false,
        val code: Int = 0,
        val message: String = ""
    )

    @Serializable
    data class LxnsMaimaiResponse(
        val data: List<LxnsMaimaiUploadReturnScoreBody> = listOf()
    ) : LxnsResponse()

    @Serializable
    data class LxnsChuniResponse(
        val data: List<LxnsChuniUploadReturnScoreBody> = listOf()
    ) : LxnsResponse()

    @Serializable
    data class LxnsGetMaimaiScoreResponse(
        val data: List<LxnsMaimaiScoreBody>
    ) : LxnsResponse()

    @Serializable
    data class LxnsChuniScoreBody(
        val id: Int,
        @SerialName("song_name") val songName: String = "",
        val level: String = "",
        @SerialName("level_index") val levelIndex: Int,
        val score: Int,
        val rating: Float = 0.0F,
        @SerialName("over_power") val overPower: Float = 0.0F,
        val clear: String,
        @SerialName("full_combo") val fullCombo: String = "",
        @SerialName("full_chain") val fullChain: String = "",
        val rank: String = "",
        @SerialName("play_time") val playTime: String = "",
        @SerialName("upload_time") val uploadTime: String = ""
    )

    @Serializable
    data class LxnsChuniUploadReturnScoreBody(
        val id: Int,
        @SerialName("song_name") val songName: String = "",
        val level: String = "",
        @SerialName("level_index") val levelIndex: Int,
        val score: LxnsUploadDiff<Int> = LxnsUploadDiff(),
        val rating: LxnsUploadDiff<Float> = LxnsUploadDiff(),
        @SerialName("over_power") val overPower: LxnsUploadDiff<Float> = LxnsUploadDiff(),
        val clear: LxnsUploadDiff<String> = LxnsUploadDiff(),
        @SerialName("full_combo") val fullCombo: LxnsUploadDiff<String> = LxnsUploadDiff(),
        @SerialName("full_chain") val fullChain: LxnsUploadDiff<String> = LxnsUploadDiff(),
        val rank: String = "",
        @SerialName("play_time") val playTime: String = "",
        @SerialName("upload_time") val uploadTime: String = ""
    )

    @Serializable
    data class LxnsMaimaiScoreBody(
        val id: Int,
        @SerialName("song_name") val songName: String = "",
        val level: String = "",
        @SerialName("level_index") val levelIndex: Int,
        val achievements: Float,
        val fc: String? = "",
        val fs: String? = "",
        @SerialName("dx_score") val dxScore: Int,
        @SerialName("dx_rating") val dxRating: Float = 0.0F,
        val rate: String = "",
        val type: String,
        @SerialName("play_time") val playTime: String = "",
        @SerialName("upload_time") val uploadTime: String = ""
    )

    @Serializable
    data class LxnsMaimaiUploadReturnScoreBody(
        val id: Int,
        @SerialName("song_name") val songName: String = "",
        val level: String = "",
        @SerialName("level_index") val levelIndex: Int,
        val achievements: LxnsUploadDiff<Float> = LxnsUploadDiff(),
        val fc: LxnsUploadDiff<String> = LxnsUploadDiff(),
        val fs: LxnsUploadDiff<String> = LxnsUploadDiff(),
        @SerialName("dx_score") val dxScore: LxnsUploadDiff<Int>,
        @SerialName("dx_rating") val dxRating: LxnsUploadDiff<Float> = LxnsUploadDiff(),
        val rate: String = "",
        val type: String,
        @SerialName("play_time") val playTime: String = "",
        @SerialName("upload_time") val uploadTime: String = ""
    )

    @Serializable
    data class LxnsUploadDiff<T>(
        val old: T? = null,
        val new: T? = null
    )

    @Serializable
    data class LxnsMaimaiRequestBody(val scores: List<LxnsMaimaiScoreBody>)

    @Serializable
    data class LxnsChuniRequestBody(val scores: List<LxnsChuniScoreBody>)

    override suspend fun uploadMaimaiProberData(
        importToken: String,
        authUrl: String
    ) {
        val isCache = application.configManager.config.localConfig.cacheScore

        application.sendNotifaction("落雪查分器", "舞萌数据上传中")
        sendMessageToUi("开始获取舞萌数据并上传到落雪查分器")
        val scores = getMaimaiScoreData(authUrl)

        if (scores.isEmpty()) {
            return
        }

        val postScores = scores.map {
            LxnsMaimaiScoreBody(
                id = MaimaiData.getSongIdFromTitle(it.name),
                levelIndex = it.diff.diffIndex,
                achievements = it.score,
                fc = it.fullComboType.typeName,
                fs = it.syncType.syncName,
                dxScore = it.dxScore.toInt(),
                type = it.type.type
            )
        }

        val body = Json.encodeToString(LxnsMaimaiRequestBody(postScores))

        val postResponse = try {
            client.post("$baseApiUrl/api/v0/user/maimai/player/scores") {
                setBody(body)
                header("X-User-Token", importToken)
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                }
            }
        } catch (e: Exception) {
            Log.e("LxnsProberUtil", "上传失败: $e")
            sendMessageToUi("上传失败: $e")
            return
        }

        val postScoreResponseBody = postResponse.body<LxnsMaimaiResponse>()
        if (postScoreResponseBody.success) {
            sendMessageToUi("上传舞萌成绩到落雪查分器成功")
            Log.d("LxnsProberUtil", "上传完毕")
        } else {
            sendMessageToUi("舞萌成绩上传到落雪查分器失败: ${postScoreResponseBody.message}")
            Log.e("LxnsProberUtil", "上传失败: ${postScoreResponseBody.message}")
        }
        GlobalViewModel.maimaiHooking = false
        application.sendNotifaction("落雪查分器", "舞萌数据上传完毕")
        if (isCache) {
            writeMaimaiScoreCache(scores)
        }
    }

    override suspend fun uploadChunithmProberData(importToken: String, authUrl: String) {
        val isCache = application.configManager.config.localConfig.cacheScore

        application.sendNotifaction("落雪查分器", "中二节奏数据上传中")
        sendMessageToUi("开始获取中二节奏数据并上传到落雪查分器")
        val scores = getChuniScoreData(authUrl)

        if (scores.isEmpty()) {
            return
        }

        val postScores = scores.map {
            LxnsChuniScoreBody(
                id = it.id,
                levelIndex = it.diff.diffIndex,
                score = it.score,
                clear = it.clearType.type,
                fullCombo = it.fullComboType.type,
                fullChain = it.fullChainType.type
            )
        }

        val body = Json.encodeToString(LxnsChuniRequestBody(postScores))

        val postResponse = try {
            client.post("$baseApiUrl/api/v0/user/chunithm/player/scores") {
                setBody(body)
                headers {
                    append("X-User-Token", importToken)
                    append(HttpHeaders.ContentType, "application/json")
                }
            }
        } catch (e: HttpRequestTimeoutException) {
            Log.e("LxnsProberUtil", "上传失败: $e")
            sendMessageToUi("上传失败: $e")
            return
        }

        val postScoreResponseBody = postResponse.body<LxnsChuniResponse>()
        if (postScoreResponseBody.success) {
            sendMessageToUi("上传中二节奏成绩到落雪查分器成功")
            Log.d("LxnsProberUtil", "上传完毕")
        } else {
            sendMessageToUi("上传中二成绩成绩到落雪查分器失败: ${postScoreResponseBody.message}")
            Log.e("LxnsProberUtil", "上传失败: ${postScoreResponseBody.message}")
        }
        GlobalViewModel.chuniHooking = false
        application.sendNotifaction("落雪查分器", "中二数据上传完毕")
        if (isCache) {
            writeChuniScoreCache(scores)
        }
    }

    override suspend fun getMaimaiProberData(importToken: String): List<MaimaiData.MusicDetail> {
        try {
            val response = client.get("$baseApiUrl/api/v0/user/maimai/player/scores") {
                header("X-User-Token", importToken)
            }
            val body = response.body<LxnsGetMaimaiScoreResponse>()
            val parseList = arrayListOf<MaimaiData.MusicDetail>()
            body.data.forEach {
                val type = MaimaiEnums.SongType.getSongTypeByName(it.type)
                val diff = MaimaiEnums.Difficulty.getDifficultyWithIndex(it.levelIndex)
                val levelValue = MaimaiData.getLevelValue(it.songName, diff, type)
                val version = MaimaiData.getChartVersion(it.songName, diff, type)
                parseList.add(
                    MaimaiData.MusicDetail(
                        name = it.songName,
                        level = levelValue,
                        score = it.achievements,
                        dxScore = it.dxScore,
                        rating = floor(it.dxRating).toInt(),
                        version = version,
                        type = type,
                        diff = diff,
                        rankType = MaimaiEnums.RankType.getRankTypeByScore(it.achievements),
                        syncType = MaimaiEnums.SyncType.getSyncTypeByName(it.fs ?: ""),
                        fullComboType = MaimaiEnums.FullComboType.getFullComboTypeByName(it.fc ?: "")
                    )
                )
            }
            return parseList
        } catch (e: Exception) {
            Log.d("LxnsProberUtil", "获取舞萌成绩失败: $e")
            sendMessageToUi("获取舞萌成绩失败: ${e.message}")
            return emptyList()
        }
    }
}