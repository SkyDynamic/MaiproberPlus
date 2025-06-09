package io.github.skydynamic.maiproberplus.core.prober

import android.util.Log
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniScoreManager.writeChuniScoreCache
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiScoreManager.writeMaimaiScoreCache

class LocalProberUtil : IProberUtil {
    override suspend fun uploadMaimaiProberData(importToken: String, authUrl: String) {
        application.sendNotification("本地查分器", "正在进行查分")
        sendMessageToUi("开始获取舞萌DX成绩并缓存到本地")

        writeMaimaiScoreCache(getMaimaiScoreData(authUrl))

        sendMessageToUi("缓存舞萌DX成绩到本地完成")
        Log.d("LocalProberUtil", "缓存完成")
        GlobalViewModel.maimaiHooking = false
        application.sendNotification("本地查分器", "缓存完毕")
    }

    override suspend fun uploadChunithmProberData(importToken: String, authUrl: String) {
        application.sendNotification("本地查分器", "正在进行查分")
        sendMessageToUi("开始获取中二节奏成绩并缓存到本地")

        writeChuniScoreCache(getChuniScoreData(authUrl))

        sendMessageToUi("缓存中二节奏成绩到本地完成")
        Log.d("LocalProberUtil", "缓存完成")
        GlobalViewModel.chuniHooking = false
        application.sendNotification("本地查分器", "缓存完毕")
    }
}