package io.github.skydynamic.maiproberplus.ui.compose.sync

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

object SyncViewModel : ViewModel() {
    var openInitDialog by mutableStateOf(false)
    var openInitDownloadDialog by mutableStateOf(false)
    var tokenHidden by mutableStateOf(true)
}