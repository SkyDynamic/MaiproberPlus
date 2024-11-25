package io.github.skydynamic.maiproberplus.core.prober

import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData

interface IProberUtil {
    suspend fun updateAccountInfo(importToken: String) {}
    suspend fun uploadMaimaiProberData(importToken: String, authUrl: String) {}
    suspend fun uploadChunithmProberData(importToken: String, authUrl: String) {}
    suspend fun getMaimaiProberData(importToken: String): List<MaimaiData.MusicDetail> {
        return emptyList()
    }
    suspend fun getChuniProberData(importToken: String): List<ChuniData.MusicDetail> {
        return emptyList()
    }
}

