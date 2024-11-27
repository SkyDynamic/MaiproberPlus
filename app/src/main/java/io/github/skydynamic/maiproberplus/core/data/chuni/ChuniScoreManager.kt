package io.github.skydynamic.maiproberplus.core.data.chuni

import androidx.lifecycle.viewModelScope
import io.github.skydynamic.maiproberplus.Application
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import io.github.skydynamic.maiproberplus.ui.compose.scores.chuni.refreshChuniScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ChuniScoreManager {
    fun writeChuniScoreCache(data: List<ChuniScoreEntity>) {
        GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
            val dao = Application.application.db.chuniScoreDao()
            data.forEach {
                if (!dao.exists(it.score)) {
                    dao.insert(it)
                }
            }
        }
    }

    fun getChuniScoreCache(): List<ChuniScoreEntity> {
        var scores: List<ChuniScoreEntity> = emptyList()
        runBlocking {
            GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                val dao = Application.application.db.chuniScoreDao()
                scores = dao.getAllHighestMusicScore()
            }.join()
        }
        return scores
    }

    fun deleteScore(score: MaimaiScoreEntity) {
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