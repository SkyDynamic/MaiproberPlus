package io.github.skydynamic.maiproberplus.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniEnums

@Entity(tableName = "chuni_score_entity")
data class ChuniScoreEntity(
    @PrimaryKey(autoGenerate = true) val scoreId: Int = 0,
    @ColumnInfo("song_id") val songId: Int = -1,
    @ColumnInfo("title") val title: String,
    val level: Float,
    val score: Int,
    val rating: Float,
    val version: Int,
    val playTime: String = "",
    val rankType: ChuniEnums.RankType,
    val diff: ChuniEnums.Difficulty,
    val fullComboType: ChuniEnums.FullComboType,
    val clearType: ChuniEnums.ClearType,
    val fullChainType: ChuniEnums.FullChainType
)