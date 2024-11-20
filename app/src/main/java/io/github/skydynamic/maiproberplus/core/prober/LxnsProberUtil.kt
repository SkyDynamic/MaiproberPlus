package io.github.skydynamic.maiproberplus.core.prober

import android.util.Log
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
        val data: List<LxnsMaimaiScoreBody> = listOf()
    ) : LxnsResponse()

    @Serializable
    data class LxnsChuniResponse(
        val data: List<LxnsChuniScoreBody> = listOf()
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
    data class LxnsMaimaiScoreBody(
        val id: Int,
        @SerialName("song_name") val songName: String = "",
        val level: String = "",
        @SerialName("level_index") val levelIndex: Int,
        val achievements: Float,
        val fc: String = "",
        val fs: String = "",
        @SerialName("dx_score") val dxScore: Int,
        @SerialName("dx_rating") val dxRating: Float = 0.0F,
        val rate: String = "",
        val type: String,
        @SerialName("play_time") val playTime: String = "",
        @SerialName("upload_time") val uploadTime: String = ""
    )

    @Serializable
    data class LxnsMaimaiRequestBody(val scores: List<LxnsMaimaiScoreBody>)

    @Serializable
    data class LxnsChuniRequestBody(val scores: List<LxnsChuniScoreBody>)

    override suspend fun uploadMaimaiProberData(
        importToken: String,
        authUrl: String
    ) {
        sendMessageToUi("开始获取舞萌数据并上传到落雪查分器")
        val scores = getMaimaiPageData(authUrl)

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

        val postResponse = client.post("$baseApiUrl/api/v0/user/maimai/player/scores") {
            setBody(body)
            header("X-User-Token", importToken)
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
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
    }

    override suspend fun uploadChunithmProberData(importToken: String, authUrl: String) {
        sendMessageToUi("开始获取中二节奏数据并上传到落雪查分器")
        val scores = getChuniPageData(authUrl)

        if (scores.isEmpty()) {
            return
        }

        val postScores = scores.map {
            LxnsChuniScoreBody(
                id = ChuniData.getSongIdFromTitle(it.name),
                levelIndex = it.diff.diffIndex,
                score = it.score,
                clear = it.clearType.type,
                fullCombo = it.fullComboType.type,
                fullChain = it.fullChainType.type
            )
        }

        val body = Json.encodeToString(LxnsChuniRequestBody(postScores))

        val postResponse = client.post("$baseApiUrl/api/v0/user/chunithm/player/scores") {
            setBody(body)
            header("X-User-Token", importToken)
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
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
    }
}