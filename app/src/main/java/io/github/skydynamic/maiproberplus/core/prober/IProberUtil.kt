package io.github.skydynamic.maiproberplus.core.prober

import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity

interface IProberUtil {
    suspend fun updateUserInfo(importToken: String) {}
    suspend fun uploadMaimaiProberData(importToken: String, authUrl: String) {}
    suspend fun uploadChunithmProberData(importToken: String, authUrl: String) {}
    suspend fun getMaimaiProberData(importToken: String): List<MaimaiScoreEntity> {
        return emptyList()
    }
    suspend fun getChuniProberData(importToken: String): List<ChuniScoreEntity> {
        return emptyList()
    }
    suspend fun getChuniScoreBests(importToken: String): List<ChuniScoreEntity> {
        return emptyList()
    }
}

