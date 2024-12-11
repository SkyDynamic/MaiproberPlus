package io.github.skydynamic.maiproberplus.ui.compose.bests

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiScoreManager.getMaimaiScoreCache
import io.github.skydynamic.maiproberplus.core.prober.ProberPlatform
import io.github.skydynamic.maiproberplus.core.prober.sendMessageToUi
import io.github.skydynamic.maiproberplus.core.utils.MaimaiB50GenerateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object BestsImageGenerateViewModel : ViewModel() {
    var canGenerate by mutableStateOf(true)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BestsImageGenerateCompose() {
    val config = application.configManager.config

    var b50Bitmap: Bitmap? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState())
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            ProberPlatform.entries.forEach {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = it.ordinal,
                        count = ProberPlatform.entries.size,
                    ),
                    onClick = { GlobalViewModel.proberPlatform = it },
                    selected = it == GlobalViewModel.proberPlatform
                ) {
                    Text(it.proberName)
                }
            }
        }

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = BestsImageGenerateViewModel.canGenerate,
            onClick = {
                GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                    val scores = when(GlobalViewModel.proberPlatform) {
                        ProberPlatform.DIVING_FISH -> {
                            if (config.divingfishToken.isEmpty()) {
                                sendMessageToUi("请先设置水鱼查分器Token")
                                return@launch
                            } else {
                                GlobalViewModel.proberPlatform.factory.getMaimaiProberData(
                                    config.divingfishToken
                                )
                            }
                        }
                        ProberPlatform.LXNS -> {
                            if (config.lxnsToken.isEmpty()) {
                                sendMessageToUi("请先设置落雪查分器Token")
                                return@launch
                            } else {
                                GlobalViewModel.proberPlatform.factory.getMaimaiProberData(
                                    config.lxnsToken
                                )
                            }
                        }
                        ProberPlatform.LOCAL -> getMaimaiScoreCache()
                    }
                    BestsImageGenerateViewModel.canGenerate = false
                    b50Bitmap = MaimaiB50GenerateUtil.generateBestsImage(scores)
                }
            }
        ) {
            Text("生成B50")
        }

        if (b50Bitmap != null) {
            ImagePreview(b50Bitmap)
        }
    }
}

