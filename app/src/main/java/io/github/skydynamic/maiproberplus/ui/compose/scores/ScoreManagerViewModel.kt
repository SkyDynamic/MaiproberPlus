package io.github.skydynamic.maiproberplus.ui.compose.scores

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object ScoreManagerViewModel : ViewModel() {
    val maimaiLoadedScores = mutableStateListOf<MaimaiScoreEntity>()
    val maimaiSearchScores = mutableStateListOf<MaimaiScoreEntity>()
    val maimaiSearchText = mutableStateOf("")
    val chuniLoadedScores = mutableStateListOf<ChuniScoreEntity>()
    val chuniSearchScores = mutableStateListOf<ChuniScoreEntity>()
    val chuniSearchText = mutableStateOf("")

    var maimaiScoreSelection: MaimaiScoreEntity? by mutableStateOf(null)
    var chuniScoreSelection: ChuniScoreEntity? by mutableStateOf(null)

    var showMaimaiScoreSelectionDialog by mutableStateOf(false)
    var showChuniScoreSelectionDialog by mutableStateOf(false)

    private val chuniAliasMap: Map<Int, List<String>> by lazy {
        ChuniData.CHUNI_SONG_ALIASES.associateBy({ it.songId }, { it.aliases })
    }
    val chuniSearchCache = mutableMapOf<String, List<ChuniScoreEntity>>()

    fun searchChuniScore(text: String) {
        if (text.isEmpty()) {
            chuniSearchScores.clear()
        } else {
            val cachedResult = chuniSearchCache[text]
            if (cachedResult != null) {
                chuniSearchScores.clear()
                chuniSearchScores.addAll(cachedResult)
            } else {
                ScoreManagerViewModel.viewModelScope.launch(Dispatchers.IO) {
                    val searchResult = chuniLoadedScores.filter { musicDetail ->
                        musicDetail.title.contains(text, ignoreCase = true) ||
                                chuniAliasMap[
                                    if (musicDetail.songId == -1)
                                        ChuniData.getSongIdFromTitle(musicDetail.title)
                                    else
                                        musicDetail.songId
                                ]?.any { alias ->
                                    alias.contains(text, ignoreCase = true)
                                } == true
                    }
                    withContext(Dispatchers.Main) {
                        chuniSearchScores.clear()
                        chuniSearchScores.addAll(searchResult)
                        chuniSearchCache[text] = searchResult
                    }
                }
            }
        }
    }

    private val maimaiAliasMap: Map<Int, List<String>> by lazy {
        MaimaiData.MAIMAI_SONG_ALIASES.associateBy({ it.songId }, { it.aliases })
    }
    val maimaiSearchCache = mutableMapOf<String, List<MaimaiScoreEntity>>()

    fun searchMaimaiScore(text: String) {
        if (text.isEmpty()) {
            maimaiSearchScores.clear()
        } else {
            val cachedResult = maimaiSearchCache[text]
            if (cachedResult != null) {
                maimaiSearchScores.clear()
                maimaiSearchScores.addAll(cachedResult)
            } else {
                ScoreManagerViewModel.viewModelScope.launch(Dispatchers.IO) {
                    val searchResult = maimaiLoadedScores.filter { musicDetail ->
                        musicDetail.title.contains(text, ignoreCase = true) ||
                                maimaiAliasMap[
                                    MaimaiData.getSongIdFromTitle(musicDetail.title)
                                ]?.any { alias ->
                                    alias.contains(text, ignoreCase = true)
                                } == true
                    }
                    withContext(Dispatchers.Main) {
                        maimaiSearchScores.clear()
                        maimaiSearchScores.addAll(searchResult)
                        maimaiSearchCache[text] = searchResult
                    }
                }
            }
        }
    }
}
