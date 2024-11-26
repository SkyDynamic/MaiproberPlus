package io.github.skydynamic.maiproberplus

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

class AssetsManager(
    context: Context
) {
    val assetsManager = context.assets

    fun getMaimaiUIAssets(fileName: String): Bitmap? {
        try {
            val fileDescriptor = assetsManager.openFd("maimai/ui/$fileName")
            val inputStream = fileDescriptor.createInputStream()
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.d("AssetsManager", "Failed to load $fileName from assets", e)
            return null
        }
    }
}