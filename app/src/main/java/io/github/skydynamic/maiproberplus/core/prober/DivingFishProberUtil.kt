package io.github.skydynamic.maiproberplus.core.prober

import android.util.Log
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DivingFishProberUtil : IProberUtil {
    private val baseApiUrl = "https://www.diving-fish.com/api"

    @Serializable
    data class DivingFishScoreUploadBody(
        val achievements: Float,
        val dxScore: Int,
        val fc: String,
        val fs: String,
        @SerialName("level_index") val levelIndex: Int,
        val title: String,
        val type: String
    )

    override suspend fun uploadMaimaiProberData(
        importToken: String,
        authUrl: String
    ) {
        sendMessageToUi("开始获取Maimai数据并上传到水鱼查分器")
        val scores = getMaimaiPageData(authUrl)

        if (scores.isEmpty()) {
            return
        }

        val postScores = scores.map {
            DivingFishScoreUploadBody(
                achievements = it.score,
                dxScore = it.dxScore,
                fc = it.fullComboType.typeName,
                fs = it.syncType.syncName,
                levelIndex = it.diff.diffIndex,
                title = it.name,
                type = it.type.type
            )
        }

        val postResponse = client.post("$baseApiUrl/maimaidxprober/player/update_records") {
            setBody(Json.encodeToString(postScores))
            headers {
                append("Import-Token", importToken)
                append(HttpHeaders.ContentType, "application/json")
            }
        }

        if (postResponse.status.value == 200) {
            sendMessageToUi("成绩已成功上传到水鱼查分器")
            Log.d("DivingFishProberUtil", "上传完毕")
        } else if (postResponse.status.value == 400) {
            sendMessageToUi("成绩上传到水鱼查分器失败, 请检查token是否正确")
            Log.d("DivingFishProberUtil", "上传失败")
        }
        GlobalViewModel.maimaiHooking = false
    }

    override suspend fun uploadChunithmProberData(
        importToken: String,
        authUrl: String
    ) {
        sendMessageToUi("开始获取中二节奏数据并上传到水鱼查分器")
        fetchChuniScores(authUrl) { diff, body ->
            val recentParam = if (diff.diffName.lowercase().contains("recent")) "?recent=1" else ""
            client.post("https://www.diving-fish.com/api/chunithmprober/player/update_records_html$recentParam") {
                headers {
                    append("Import-Token", importToken)
                    append(HttpHeaders.ContentType, "text/plain")
                }
                contentType(ContentType.Text.Plain)
                setBody(body)
            }
        }
        sendMessageToUi("上传中二节奏成绩到水鱼查分器完成")
        Log.d("DivingFishProberUtil", "上传完毕")
        GlobalViewModel.chuniHooking = false
    }
}