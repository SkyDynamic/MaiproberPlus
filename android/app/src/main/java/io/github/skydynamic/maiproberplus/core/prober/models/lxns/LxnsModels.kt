package io.github.skydynamic.maiproberplus.core.prober.models.lxns

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class LxnsResponse(
    val success: Boolean = false,
    val code: Int = 0,
    val message: String = ""
)

@Serializable
data class LxnsUserInfoResponse(
    val data: LxnsUserInfoBody
) : LxnsResponse()

@Serializable
data class LxnsMaimaiResponse(
    val data: List<LxnsMaimaiUploadReturnScoreBody> = listOf()
) : LxnsResponse()

@Serializable
data class LxnsChuniResponse(
    val data: List<LxnsChuniUploadReturnScoreBody> = listOf()
) : LxnsResponse()

@Serializable
data class LxnsGetMaimaiScoreResponse(
    val data: List<LxnsMaimaiScoreBody>
) : LxnsResponse()

@Serializable
data class LxnsGetChuniScoreBestsResponse(
    val data: LxnsChuniBestsBody
) : LxnsResponse() {
    @Serializable
    data class LxnsChuniBestsBody(
        val bests: List<LxnsChuniScoreBody>,
        val recents: List<LxnsChuniScoreBody>
    )
}

@Serializable
data class LxnsGetChuniScoreResponse(
    val data: List<LxnsChuniScoreBody>
) : LxnsResponse()

@Serializable
data class LxnsGetSiteConfigResponse(
    val data: SiteConfigData
) {
    @Serializable
    data class SiteConfigData(
        @SerialName("resource_version") val resourceVersion: ResourceVersion,
    ) {
        @Serializable
        data class ResourceVersion(
            val maimai: Int,
            val chunithm: Int
        )
    }
}

@Serializable
data class LxnsChuniScoreBody(
    val id: Int,
    @SerialName("song_name") val songName: String = "",
    val level: String = "",
    @SerialName("level_index") val levelIndex: Int,
    val score: Int,
    val rating: Float = 0.0F,
    @SerialName("over_power") val overPower: Float = 0.0F,
    val clear: String? = "failed",
    @SerialName("full_combo") val fullCombo: String? = "",
    @SerialName("full_chain") val fullChain: String? = "",
    val rank: String = "",
    @SerialName("play_time") val playTime: String? = "",
    @SerialName("upload_time") val uploadTime: String? = ""
)

@Serializable
data class LxnsChuniUploadReturnScoreBody(
    val id: Int,
    @SerialName("song_name") val songName: String = "",
    val level: String = "",
    @SerialName("level_index") val levelIndex: Int,
    val score: LxnsUploadDiff<Int> = LxnsUploadDiff(),
    val rating: LxnsUploadDiff<Float> = LxnsUploadDiff(),
    @SerialName("over_power") val overPower: LxnsUploadDiff<Float> = LxnsUploadDiff(),
    val clear: LxnsUploadDiff<String> = LxnsUploadDiff(),
    @SerialName("full_combo") val fullCombo: LxnsUploadDiff<String> = LxnsUploadDiff(),
    @SerialName("full_chain") val fullChain: LxnsUploadDiff<String> = LxnsUploadDiff(),
    val rank: String = "",
    @SerialName("play_time") val playTime: String = "",
    @SerialName("upload_time") val uploadTime: String = ""
)

@Serializable
data class LxnsMaimaiScoreBody(
    val id: Int,
    @SerialName("song_name") val songName: String? = null,
    val level: String? = null,
    @SerialName("level_index") val levelIndex: Int,
    val achievements: Float,
    val fc: String? = "",
    val fs: String? = "",
    @SerialName("dx_score") val dxScore: Int,
    @SerialName("dx_rating") val dxRating: Float? = null,
    val rate: String? = null,
    val type: String,
    @SerialName("play_time") val playTime: String = "",
    @SerialName("upload_time") val uploadTime: String? = null
)

@Serializable
data class LxnsMaimaiUploadReturnScoreBody(
    val id: Int,
    @SerialName("song_name") val songName: String = "",
    val level: String = "",
    @SerialName("level_index") val levelIndex: Int,
    val achievements: LxnsUploadDiff<Float> = LxnsUploadDiff(),
    val fc: LxnsUploadDiff<String> = LxnsUploadDiff(),
    val fs: LxnsUploadDiff<String> = LxnsUploadDiff(),
    @SerialName("dx_score") val dxScore: LxnsUploadDiff<Int>,
    @SerialName("dx_rating") val dxRating: LxnsUploadDiff<Float> = LxnsUploadDiff(),
    val rate: String = "",
    val type: String,
    @SerialName("play_time") val playTime: String = "",
    @SerialName("upload_time") val uploadTime: String = ""
)

@Serializable
data class LxnsUploadDiff<T>(
    val old: T? = null,
    val new: T? = null
)

@Serializable
data class LxnsCollectionBody(
    val id: Int = 0,
    val name: String = "",
    val color: String? = "",
)

@Serializable
data class LxnsUserInfoBody(
    val name: String,
    val trophy: LxnsCollectionBody,
    val icon: LxnsCollectionBody,
    @SerialName("name_plate") val namePlate: LxnsCollectionBody = LxnsCollectionBody(),
    @SerialName("course_rank") val courseRank: Int,
    @SerialName("class_rank") val classRank: Int
)

@Serializable
data class LxnsMaimaiRequestBody(val scores: List<LxnsMaimaiScoreBody>)

@Serializable
data class LxnsChuniRequestBody(val scores: List<LxnsChuniScoreBody>)