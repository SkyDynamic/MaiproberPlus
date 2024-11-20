package io.github.skydynamic.maiproberplus.core.prober

interface IProberUtil {
    suspend fun updateAccountInfo(importToken: String) {}
    suspend fun uploadMaimaiProberData(importToken: String, authUrl: String) {}
    suspend fun uploadChunithmProberData(importToken: String, authUrl: String) {}
}

