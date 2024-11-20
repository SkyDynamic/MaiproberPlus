package io.github.skydynamic.maiproberplus.core.prober

import android.util.Log
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.ktor.client.call.body
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
    data class LxnsResponse(
        val success: Boolean,
        val code: Int,
        val message: String = "",
        val data: List<LxnsScoreBody> = listOf()
    )

    @Serializable
    data class LxnsScoreBody(
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

    override suspend fun uploadMaimaiProberData(
        importToken: String,
        authUrl: String
    ) {
        sendMessageToUi("开始获取Maimai数据并上传到落雪查分器")
        val scores = getMaimaiPageData(authUrl)

        val postScores = scores.map {
            LxnsScoreBody(
                id = MaimaiData.getSongIdFromTitle(it.name),
                levelIndex = it.diff.diffIndex,
                achievements = it.score,
                fc = it.specialClearType.sepcialClearName,
                fs = it.syncType.syncName,
                dxScore = it.dxScore.toInt(),
                type = it.type.type
            )
        }

        val postResponse = client.post("$baseApiUrl/api/v0/user/maimai/player/scores") {
            setBody(Json.encodeToString(postScores))
            headers {
                append("X-User-Token", importToken)
                append(HttpHeaders.ContentType, "application/json")
            }
        }

        val postScoreResponseBody = postResponse.body<LxnsResponse>()
        if (postScoreResponseBody.success) {
            sendMessageToUi("落雪查分器上传完毕")
            Log.d("LxnsProberUtil", "上传完毕")
            return
        } else {
            sendMessageToUi("成绩上传到水鱼查分器失败: ${postScoreResponseBody.message}")
            Log.e("LxnsProberUtil", "上传失败: ${postScoreResponseBody.message}")
        }
    }
}