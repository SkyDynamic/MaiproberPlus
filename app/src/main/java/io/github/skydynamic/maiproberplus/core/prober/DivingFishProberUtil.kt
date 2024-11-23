package io.github.skydynamic.maiproberplus.core.prober

import android.util.Log
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class DivingFishProberUtil : IProberUtil {
    private val baseApiUrl = "https://www.diving-fish.com/api"

    override suspend fun uploadMaimaiProberData(
        importToken: String,
        authUrl: String
    ) {
        sendMessageToUi("开始获取舞萌DX数据并上传到水鱼查分器")
        fetchMaimaiScorePage(authUrl) { diff, body ->
            Log.i("DivingFishProberUtil", "正在上传${diff.diffName}成绩到水鱼查分器")
            try {
                client.post("$baseApiUrl/maimaidxprober/player/update_records_html") {
                    headers {
                        append("Import-Token", importToken)
                        append(HttpHeaders.ContentType, "text/plain")
                    }
                    contentType(ContentType.Text.Plain)
                    setBody(body)
                }
            } catch (e: Exception) {
                Log.e("DivingFishProberUtil", "上传${diff.diffName}成绩到水鱼查分器失败", e)
            }
            Log.i("DivingFishProberUtil", "已上传${diff.diffName}成绩到水鱼查分器")
        }
        sendMessageToUi("上传舞萌DX成绩到水鱼查分器完成")
        Log.d("DivingFishProberUtil", "上传完毕")
        GlobalViewModel.maimaiHooking = false
    }

    override suspend fun uploadChunithmProberData(
        importToken: String,
        authUrl: String
    ) {
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
            } catch (e: Exception) {
                Log.e("DivingFishProberUtil", "上传${diff.diffName}成绩到水鱼查分器失败", e)
            }
            Log.i("DivingFishProberUtil", "已上传${diff.diffName}成绩到水鱼查分器")
        }
        sendMessageToUi("上传中二节奏成绩到水鱼查分器完成")
        Log.d("DivingFishProberUtil", "上传完毕")
        GlobalViewModel.chuniHooking = false
    }
}