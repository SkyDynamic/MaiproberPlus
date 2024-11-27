package io.github.skydynamic.maiproberplus.core.database.inteface

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity

@Dao
interface ChuniScoreDao {
    @Insert
    suspend fun insert(score: ChuniScoreEntity)

    @Insert
    fun insertAll(vararg score: ChuniScoreEntity)

    @Insert
    fun insertAll(scores: List<ChuniScoreEntity>)

    @Delete
    fun delete(score: ChuniScoreEntity)

    @Query("DELETE FROM chuni_score_entity WHERE rowid = :scoreId")
    fun deleteWithScoreId(scoreId: Int)

    @Query("DELETE FROM chuni_score_entity")
    fun deleteAll()

    @Query("SELECT * FROM chuni_score_entity")
    suspend fun getAllMusicScore(): List<ChuniScoreEntity>

    @Query("""
        SELECT t1.*
        FROM chuni_score_entity t1
        JOIN (
            SELECT title, diff, MAX(score) as maxScore
            FROM chuni_score_entity
            GROUP BY title, diff
        ) t2 ON t1.title = t2.title AND t1.diff = t2.diff AND t1.score = t2.maxScore
        ORDER BY t1.score DESC
    """)
    suspend fun getAllHighestMusicScore(): List<ChuniScoreEntity>


    @Query("SELECT COUNT(*) FROM chuni_score_entity")
    suspend fun getMusicScoreCount(): Int

    @Query("SELECT * FROM chuni_score_entity WHERE title = :title")
    suspend fun getMusicScoreByTitle(title: String): ChuniScoreEntity

    @Query("SELECT * FROM chuni_score_entity WHERE song_id = :songId")
    suspend fun getMusicScoreBySongId(songId: Int): ChuniScoreEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM chuni_score_entity WHERE score = :score)")
    suspend fun exists(score: Int): Boolean
}