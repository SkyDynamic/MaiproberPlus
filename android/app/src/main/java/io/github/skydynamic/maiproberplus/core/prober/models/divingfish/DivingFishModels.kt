package io.github.skydynamic.maiproberplus.core.prober.models.divingfish

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DivingFishPlayerProfile(
    val username: String,
    @SerialName("additional_rating") val additionalRating: Int
)

@Serializable
data class DivingFishMaimaiScoreBody(
    @SerialName("song_id") val songId: Int? = null,
    val title: String,
    val level: String,
    @SerialName("level_index") val levelIndex: Int,
    val type: String,
    val achievements: Float,
    val dxScore: Int,
    val rate: String,
    val fc: String,
    val fs: String,
    val ds: Float = 0F,
    @SerialName("level_label")val levelLabel: String,
    val ra: Int,
)

@Serializable
data class DivingFishChuniScoreBody(
    val cid: Int,
    val ds: Float,
    val fc: String,
    val level: String,
    @SerialName("level_index") val levelIndex: Int,
    @SerialName("level_label") val levelLabel: String,
    val mid: Int,
    val ra: Float,
    val score: Int,
    val title: String
)

@Serializable
data class DivingFishChuniRecordsBody(
    val best: List<DivingFishChuniScoreBody>,
    val r10: List<DivingFishChuniScoreBody>
)

@Serializable
data class DivingFishGetMaimaiScoresResponse(
    @SerialName("additional_rating") val additionalRating: Float,
    val nickname: String,
    val plate: String,
    val rating: Int,
    val username: String,
    val records: List<DivingFishMaimaiScoreBody>
)

@Serializable
data class DivingFishGetChuniSCoreResponse(
    val nickname: String,
    val rating: Float,
    val username: String,
    val records: DivingFishChuniRecordsBody
)