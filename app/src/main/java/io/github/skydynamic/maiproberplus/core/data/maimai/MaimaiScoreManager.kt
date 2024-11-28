package io.github.skydynamic.maiproberplus.core.data.maimai

import androidx.lifecycle.viewModelScope
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import io.github.skydynamic.maiproberplus.ui.compose.scores.maimai.refreshMaimaiScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object MaimaiScoreManager {
    suspend fun writeMaimaiScoreCache(data: List<MaimaiScoreEntity>) {
        val dao = application.db.maimaiScoreDao()
        if (dao.getMusicScoreCount() == 0) {
            dao.insertAll(data)
        } else {
            data.forEach {
                if (!dao.exists(it.title, it.type, it.achievement, it.dxScore)) {
                    dao.insert(it)
                } else {
                    return@forEach
                }
            }
        }
        refreshMaimaiScore()
    }

    fun createMaimaiScore(score: MaimaiScoreEntity) {
        GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
            val dao = application.db.maimaiScoreDao()
            if (!dao.exists(score.title, score.type, score.achievement, score.dxScore)) {
                dao.insert(score)
                refreshMaimaiScore()
            }
        }
    }

    fun getMaimaiScoreByScoreId(scoreId: Int): MaimaiScoreEntity? {
        var score: MaimaiScoreEntity? = null
        runBlocking {
            GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                val dao = application.db.maimaiScoreDao()
                score = dao.getMusicScoreByScoreId(scoreId)
            }.join()
        }
        return score
    }

    fun getMaimaiScoreCache(): List<MaimaiScoreEntity> {
        var scores: List<MaimaiScoreEntity> = emptyList()
        runBlocking {
            GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                val dao = application.db.maimaiScoreDao()
                scores = dao.getAllHighestMusicScore()
            }.join()
        }
        return scores
    }

    fun deleteScore(score: MaimaiScoreEntity) {
        GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
            val dao = application.db.maimaiScoreDao()
            dao.deleteWithScoreId(score.scoreId)
            refreshMaimaiScore()
        }
    }

    fun deleteAllScore() {
        GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
            val dao = application.db.maimaiScoreDao()
            dao.deleteAll()
            refreshMaimaiScore()
        }
    }
}