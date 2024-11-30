package io.github.skydynamic.maiproberplus.core.database.inteface

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity

@Dao
interface MaimaiScoreDao {
    @Insert
    suspend fun insert(score: MaimaiScoreEntity)

    @Insert
    suspend fun insertAll(vararg score: MaimaiScoreEntity)

    @Insert
    suspend fun insertAll(scores: List<MaimaiScoreEntity>)

    @Delete
    suspend fun delete(score: MaimaiScoreEntity)

    @Query("DELETE FROM maimai_score_entity WHERE rowid = :scoreId")
    suspend fun deleteWithScoreId(scoreId: Int)

    @Query("DELETE FROM maimai_score_entity")
    suspend fun deleteAll()

    @Query("SELECT * FROM maimai_score_entity")
    suspend fun getAllMusicScore(): List<MaimaiScoreEntity>

    @Query("""
        SELECT t1.*
        FROM maimai_score_entity t1
        JOIN (
            SELECT title, diff, type, MAX(achievement) as maxAchievement
            FROM maimai_score_entity
            GROUP BY title, diff, type
        ) t2 ON t1.title = t2.title AND t1.diff = t2.diff AND t1.type = t2.type AND t1.achievement = t2.maxAchievement
        JOIN (
            SELECT title, diff, type, achievement, MAX(dxScore) as maxDxScore
            FROM maimai_score_entity
            GROUP BY title, diff, type, achievement
        ) t3 ON t1.title = t3.title AND t1.diff = t3.diff AND t1.type = t3.type AND t1.achievement = t3.achievement AND t1.dxScore = t3.maxDxScore
        ORDER BY t1.achievement DESC, t1.dxScore DESC
    """)
    suspend fun getAllHighestMusicScore(): List<MaimaiScoreEntity>

    @Query("SELECT COUNT(*) FROM maimai_score_entity")
    suspend fun getMusicScoreCount(): Int

    @Query("SELECT * FROM maimai_score_entity WHERE title = :title")
    suspend fun getMusicScoreByTitle(title: String): MaimaiScoreEntity

    @Query("SELECT * FROM maimai_score_entity WHERE song_id = :songId")
    suspend fun getMusicScoreBySongId(songId: Int): MaimaiScoreEntity?

    @Query("SELECT * FROM maimai_score_entity WHERE rowid = :scoreId")
    suspend fun getMusicScoreByScoreId(scoreId: Int): MaimaiScoreEntity?

    @Query("""
        SELECT 
        EXISTS(
            SELECT 1 FROM maimai_score_entity WHERE title = :title AND diff = :difficulty AND type = :type AND achievement = :achievement AND dxScore = :dxScore
        )
    """)
    suspend fun exists(
        title: String,
        difficulty: MaimaiEnums.Difficulty,
        type: MaimaiEnums.SongType,
        achievement: Float,
        dxScore: Int
    ): Boolean
}