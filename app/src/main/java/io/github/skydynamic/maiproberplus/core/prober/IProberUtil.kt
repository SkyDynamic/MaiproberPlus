package io.github.skydynamic.maiproberplus.core.prober

import android.util.Log
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData.MusicDetail
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.core.utils.ParseScorePageUtil
import io.github.skydynamic.maiproberplus.core.utils.WechatRequestUtil.WX_WINDOWS_UA
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface IProberUtil {
    suspend fun updateAccountInfo(importToken: String) {}
    suspend fun uploadMaimaiProberData(importToken: String, authUrl: String) {}
    suspend fun uploadChunithmProberData(importToken: String, authUrl: String) {}

    suspend fun getMaimaiPageData(authUrl: String) : List<MusicDetail> {
        val scores = mutableListOf<MusicDetail>()

        client.get(authUrl) {
            getDefaultWahlapRequestBuilder()
        }

        val result = client.get("https://maimai.wahlap.com/maimai-mobile/home/")

        if (result.bodyAsText().contains("错误")) {
            Log.e("ProberUtil", "登录失败, 抓取成绩停止")
            return emptyList()
        }

        for (diff in MaimaiEnums.Difficulty.entries) {
            Log.i("ProberUtil", "开始抓取${diff.diffName}成绩")
            with(client) {
                val scoreResp: HttpResponse = get(
                    "https://maimai.wahlap.com/maimai-mobile/record/" +
                            "musicGenre/search/?genre=99&diff=${diff.diffIndex}"
                )
                val body = scoreResp.bodyAsText()

                val data = Regex("<html.*>([\\s\\S]*)</html>")
                    .find(body)?.groupValues?.get(1)?.replace("\\s+/g", " ")

                scores.addAll(ParseScorePageUtil.parseMaimai(data ?: "", diff))
            }
        }

        return scores
    }

    fun sendMessageToUi(message: String) {
         CoroutineScope(Dispatchers.Main).launch {
            GlobalViewModel.sendAndShowMessage(message)
         }
    }
}

private fun HttpRequestBuilder.getDefaultWahlapRequestBuilder() {
    headers {
        append(HttpHeaders.Connection, "keep-alive")
        append("Upgrade-Insecure-Requests", "1")
        append(HttpHeaders.UserAgent, WX_WINDOWS_UA)
        append(
            HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9," +
                    "image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        )
        append("Sec-Fetch-Site", "none")
        append("Sec-Fetch-Mode", "navigate")
        append("Sec-Fetch-User", "?1")
        append("Sec-Fetch-Dest", "document")
        append(HttpHeaders.AcceptEncoding, "gzip, deflate, br")
        append(HttpHeaders.AcceptLanguage, "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
    }
}