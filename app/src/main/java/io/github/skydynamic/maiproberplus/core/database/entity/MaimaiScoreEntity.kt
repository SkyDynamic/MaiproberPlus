package io.github.skydynamic.maiproberplus.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums

@Entity(tableName = "maimai_score_entity")
data class MaimaiScoreEntity(
    @PrimaryKey(autoGenerate = true) val scoreId: Int = 0,
    @ColumnInfo("song_id") val songId: Int = -1,
    @ColumnInfo("title") val title: String,
    val level: Float,
    val achievement: Float,
    val dxScore: Int,
    val rating: Int,
    val version: Int,
    val type: MaimaiEnums.SongType,
    val diff: MaimaiEnums.Difficulty,
    val rankType: MaimaiEnums.RankType,
    val syncType: MaimaiEnums.SyncType,
    val fullComboType: MaimaiEnums.FullComboType
)
