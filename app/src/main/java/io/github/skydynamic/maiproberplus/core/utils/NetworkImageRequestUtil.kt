package io.github.skydynamic.maiproberplus.core.utils

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil3.Bitmap
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NetworkImageRequestUtil {
    private val client = HttpClient {
        install(HttpTimeout) {
            connectTimeoutMillis = 30000
            requestTimeoutMillis = 30000
        }
        install(HttpCache) {
            publicStorage(FileStorage(application.cacheDir.resolve("ktorCache")))
        }
    }

    @Composable
    fun getImageRequest(url: String): ImageRequest = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(true)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .build()

    suspend fun getImageBitmap(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.get(url)
                BitmapFactory.decodeByteArray(
                    response.readRawBytes(),
                    0,
                    response.readRawBytes().size
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}