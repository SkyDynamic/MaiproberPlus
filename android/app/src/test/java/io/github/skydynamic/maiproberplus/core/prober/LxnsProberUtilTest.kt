package io.github.skydynamic.maiproberplus.core.prober

import io.github.skydynamic.maiproberplus.core.prober.models.lxns.LxnsGetSiteConfigResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Test

class LxnsProberUtilTest {
    private val baseApiUrl = "https://maimai.lxns.net"

    @Test
    fun getMaimaiResourceVersion() = runBlocking {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        val result = try {
            val response = client.get("$baseApiUrl/api/v0/site/config")
            val body = response.body<LxnsGetSiteConfigResponse>()
            body.data.resourceVersion.maimai
        } catch (e: Exception) {
            0
        }
        println(result)
    }
}