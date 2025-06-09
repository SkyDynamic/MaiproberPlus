package io.github.skydynamic.maiproberplus.core.utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object WechatRequestUtil {
    const val WX_WINDOWS_UA =
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/81.0.4044.138 Safari/537.36 NetType/WIFI " +
                "MicroMessenger/7.0.20.1781(0x6700143B) WindowsWechat(0x6307001e)"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    @JvmStatic
    fun getAuthUrl(type: String): String = runBlocking {
        val resp: HttpResponse =
            client.get("https://tgk-wcaime.wahlap.com/wc_auth/oauth/authorize/$type")
        val url = resp.request.url.toString().replace("redirect_uri=https", "redirect_uri=http")
        return@runBlocking url
    }

    @JvmStatic
    suspend fun getCookieByAuthUrl(authUrl: String): List<Cookie> {
        client.get(authUrl) {
            headers {
                append(HttpHeaders.Host, "tgk-wcaime.wahlap.com")
                append(HttpHeaders.Connection, "keep-alive")
                append("Upgrade-Insecure-Requests", "1")
                append(HttpHeaders.UserAgent, WX_WINDOWS_UA)
                append(
                    HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9" +
                            ",image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
                )
                append("Sec-Fetch-Site", "none")
                append("Sec-Fetch-Mode", "navigate")
                append("Sec-Fetch-User", "?1")
                append("Sec-Fetch-Dest", "document")
                append("Accept-Encoding", "gzip, deflate, br")
                append("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
            }
        }

        client.get("https://maimai.wahlap.com/maimai-mobile/home/")

        return client.cookies("maimai.wahlap.com") + client.cookies("tgk-wcaime.wahlap.com")
    }
}