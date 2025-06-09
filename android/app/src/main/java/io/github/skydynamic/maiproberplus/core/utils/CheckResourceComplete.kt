package io.github.skydynamic.maiproberplus.core.utils

import io.github.skydynamic.maiproberplus.Application
import io.github.skydynamic.maiproberplus.ui.compose.scores.resources
import io.github.skydynamic.maiproberplus.ui.compose.sync.FileDownloadMeta

fun checkResourceComplete(): List<FileDownloadMeta> {
    val returnList = arrayListOf<FileDownloadMeta>()
    resources.forEach {
        if (!Application.application.filesDir.resolve(it.fileSavePath).resolve(it.fileName).exists()) {
            returnList.add(it)
        }
    }
    return returnList
}
