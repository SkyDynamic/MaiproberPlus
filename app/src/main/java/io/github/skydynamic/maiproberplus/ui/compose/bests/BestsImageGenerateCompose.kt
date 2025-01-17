package io.github.skydynamic.maiproberplus.ui.compose.bests

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.R
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiScoreManager.getMaimaiScoreCache
import io.github.skydynamic.maiproberplus.core.prober.ProberPlatform
import io.github.skydynamic.maiproberplus.core.prober.sendMessageToUi
import io.github.skydynamic.maiproberplus.core.utils.ChuniB30ImageGenerater
import io.github.skydynamic.maiproberplus.core.utils.MaimaiB50GenerateUtil
import io.github.skydynamic.maiproberplus.ui.component.ImagePreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object BestsImageGenerateViewModel : ViewModel() {
    var canGenerate by mutableStateOf(true)
    var imageBitmap: Bitmap? by mutableStateOf(null)
}

fun checkEnable(platform: ProberPlatform): Boolean {
    return if (platform == ProberPlatform.LOCAL) {
        GlobalViewModel.gameType != GameType.Chunithm
    } else {
        true
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BestsImageGenerateCompose() {
    val config = application.configManager.config
    var showImagePreview by remember { mutableStateOf(false) }

    when {
        showImagePreview -> {
            ImagePreview(
                BestsImageGenerateViewModel.imageBitmap!!,
                onDismiss = { showImagePreview = false }
            )
        }
    }

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
            GameType.entries.forEach {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = it.ordinal, count = GameType.entries.size
                    ),
                    selected = GlobalViewModel.gameType == it,
                    onClick = {
                        GlobalViewModel.gameType = it
                        refreshScore(it)
                    },
                ) {
                    Text(it.displayName)
                }
            }
        }

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
                    selected = it == GlobalViewModel.proberPlatform,
                    enabled = checkEnable(it)
                ) {
                    Text(it.proberName)
                }
            }
        }

        if (GlobalViewModel.gameType == GameType.MaimaiDX) {
            MaiContent(config = config) {
                BestsImageGenerateViewModel.imageBitmap = it
                BestsImageGenerateViewModel.canGenerate = true
            }
        } else if (GlobalViewModel.gameType == GameType.Chunithm) {
            ChuniContent(config = config) {
                BestsImageGenerateViewModel.imageBitmap = it
                BestsImageGenerateViewModel.canGenerate = true
            }
        }

        if (BestsImageGenerateViewModel.imageBitmap != null) {
            AsyncImage(
                model = BestsImageGenerateViewModel.imageBitmap,
                contentDescription = "B50",
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable {
                        showImagePreview = true
                    }
            )

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    val bitmap = BestsImageGenerateViewModel.imageBitmap!!
                    val fileName = "B50_${System.currentTimeMillis()}.png"
                    application.saveImageToGallery(bitmap, fileName)
                }
            ) {
                Row {
                    Icon(painterResource(R.drawable.save_24px), null)

                    Text("保存图片到相册")
                }
            }
        }
    }
}

@Composable
fun ColumnScope.MaiContent(
    config: ConfigStorage,
    afterGenerate: (Bitmap) -> Unit
) {
    Button(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        enabled = BestsImageGenerateViewModel.canGenerate,
        onClick = {
            BestsImageGenerateViewModel.canGenerate = false
            GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                val scores = when(GlobalViewModel.proberPlatform) {
                    ProberPlatform.DIVING_FISH -> {
                        if (config.divingfishToken.isEmpty()) {
                            sendMessageToUi("请先设置水鱼查分器Token")
                            BestsImageGenerateViewModel.canGenerate = true
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
                            BestsImageGenerateViewModel.canGenerate = true
                            return@launch
                        } else {
                            GlobalViewModel.proberPlatform.factory.getMaimaiProberData(
                                config.lxnsToken
                            )
                        }
                    }
                    ProberPlatform.LOCAL -> getMaimaiScoreCache()
                }
                afterGenerate(MaimaiB50GenerateUtil.generateBestsImage(scores))
            }
        }
    ) {
        Text("生成B50")
    }
}

@Composable
fun ColumnScope.ChuniContent(
    config: ConfigStorage,
    afterGenerate: (Bitmap) -> Unit
) {
    Button(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        enabled = BestsImageGenerateViewModel.canGenerate,
        onClick = {
            BestsImageGenerateViewModel.canGenerate = false
            GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                val scores = when(GlobalViewModel.proberPlatform) {
                    ProberPlatform.DIVING_FISH -> {
                        if (config.divingfishToken.isEmpty()) {
                            sendMessageToUi("请先设置水鱼查分器Token")
                            BestsImageGenerateViewModel.canGenerate = true
                            return@launch
                        } else {
                            GlobalViewModel.proberPlatform.factory.getChuniScoreBests(
                                config.divingfishToken
                            )
                        }
                    }
                    ProberPlatform.LXNS -> {
                        if (config.lxnsToken.isEmpty()) {
                            sendMessageToUi("请先设置落雪查分器Token")
                            BestsImageGenerateViewModel.canGenerate = true
                            return@launch
                        } else {
                            GlobalViewModel.proberPlatform.factory.getChuniScoreBests(
                                config.lxnsToken
                            )
                        }
                    }
                    ProberPlatform.LOCAL -> {
                        BestsImageGenerateViewModel.canGenerate = true
                        return@launch
                    }
                }
                afterGenerate(ChuniB30ImageGenerater.generateBestsImage(scores))
            }
        }
    ) {
        Text("生成B30")
    }
}
