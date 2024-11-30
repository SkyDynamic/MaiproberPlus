package io.github.skydynamic.maiproberplus.core.data.chuni

import androidx.lifecycle.viewModelScope
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.ui.compose.scores.chuni.refreshChuniScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ChuniScoreManager {
    suspend fun writeChuniScoreCache(data: List<ChuniScoreEntity>) {
        val dao = application.db.chuniScoreDao()
        if (dao.getMusicScoreCount() == 0) {
            dao.insertAll(data)
        } else {
            data.forEach {
                if (!dao.exists(it.title, it.diff, it.score)) {
                    dao.insert(it)
                } else {
                    return@forEach
                }
            }
        }
    }

    fun createChuniScore(score: ChuniScoreEntity) {
        GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
            val dao = application.db.chuniScoreDao()
            if (!dao.exists(score.title, score.diff, score.score)) {
                dao.insert(score)
                refreshChuniScore()
            }
        }
    }

    fun getChuniScoreCache(): List<ChuniScoreEntity> {
        var scores: List<ChuniScoreEntity> = emptyList()
        runBlocking {
            GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                val dao = application.db.chuniScoreDao()
                scores = dao.getAllHighestMusicScore()
            }.join()
        }
        return scores
    }

    fun deleteScore(score: ChuniScoreEntity) {
        GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
            val dao = application.db.chuniScoreDao()
            dao.deleteWithScoreId(score.scoreId)
            refreshChuniScore()
        }
    }

    fun deleteAllScore() {
        GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
            val dao = application.db.chuniScoreDao()
            dao.deleteAll()
            refreshChuniScore()
        }
    }
}