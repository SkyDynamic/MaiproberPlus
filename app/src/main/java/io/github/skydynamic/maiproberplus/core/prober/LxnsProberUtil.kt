package io.github.skydynamic.maiproberplus.core.prober

import android.util.Log
import io.github.skydynamic.maiproberplus.GlobalViewModel
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

    @Serializable
    data class LxnsRequestBody(val scores: List<LxnsScoreBody>)

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

        val body = Json.encodeToString(LxnsRequestBody(postScores))

        val postResponse = client.post("$baseApiUrl/api/v0/user/maimai/player/scores") {
            setBody(body)
            header("X-User-Token", importToken)
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
        }

        val postScoreResponseBody = postResponse.body<LxnsResponse>()
        if (postScoreResponseBody.success) {
            sendMessageToUi("落雪查分器上传完毕")
            Log.d("LxnsProberUtil", "上传完毕")
        } else {
            sendMessageToUi("成绩上传到落雪查分器失败: ${postScoreResponseBody.message}")
            Log.e("LxnsProberUtil", "上传失败: ${postScoreResponseBody.message}")
        }
        GlobalViewModel.maimaiHooking = false
    }
}