package io.github.skydynamic.maiproberplus.ui.compose

import io.github.skydynamic.maiproberplus.Application
import io.github.skydynamic.maiproberplus.ui.compose.scores.resources
import io.github.skydynamic.maiproberplus.ui.compose.sync.FileDownloadMeta

fun checkResourceComplete(): List<FileDownloadMeta> {
    val returnList = arrayListOf<FileDownloadMeta>()
    resources.forEach {
        if (!Application.Companion.application.filesDir.resolve(it.fileSavePath).resolve(it.fileName).exists()) {
            returnList.add(it)
        }
    }
    return returnList
}