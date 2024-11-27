package io.github.skydynamic.maiproberplus.core.database.inteface

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity

@Dao
interface MaimaiScoreDao {
    @Insert
    suspend fun insert(score: MaimaiScoreEntity)

    @Insert
    fun insertAll(vararg score: MaimaiScoreEntity)

    @Insert
    fun insertAll(scores: List<MaimaiScoreEntity>)

    @Delete
    fun delete(score: MaimaiScoreEntity)

    @Query("DELETE FROM maimai_score_entity WHERE rowid = :scoreId")
    fun deleteWithScoreId(scoreId: Int)

    @Query("DELETE FROM maimai_score_entity")
    fun deleteAll()

    @Query("SELECT * FROM maimai_score_entity")
    suspend fun getAllMusicScore(): List<MaimaiScoreEntity>

    @Query("""
        SELECT t1.*
        FROM maimai_score_entity t1
        JOIN (
            SELECT achievement, dxScore, MIN(rowid) as minRowId
            FROM maimai_score_entity
            GROUP BY achievement, dxScore
        ) t2 ON t1.achievement = t2.achievement AND t1.dxScore = t2.dxScore AND t1.rowid = t2.minRowId
        ORDER BY t1.achievement DESC
    """)
    suspend fun getAllHighestMusicScore(): List<MaimaiScoreEntity>

    @Query("SELECT COUNT(*) FROM maimai_score_entity")
    suspend fun getMusicScoreCount(): Int

    @Query("SELECT * FROM maimai_score_entity WHERE title = :title")
    suspend fun getMusicScoreByTitle(title: String): MaimaiScoreEntity

    @Query("SELECT * FROM maimai_score_entity WHERE song_id = :songId")
    suspend fun getMusicScoreBySongId(songId: Int): MaimaiScoreEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM maimai_score_entity WHERE achievement = :achievement AND dxScore = :dxScore)")
    suspend fun exists(achievement: Float, dxScore: Int): Boolean
}