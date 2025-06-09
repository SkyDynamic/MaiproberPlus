package io.github.skydynamic.maiproberplus.ui.compose.bests

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.R
import io.github.skydynamic.maiproberplus.core.config.ConfigStorage
import io.github.skydynamic.maiproberplus.core.data.GameType
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiScoreManager.getMaimaiScoreCache
import io.github.skydynamic.maiproberplus.core.prober.ProberPlatform
import io.github.skydynamic.maiproberplus.core.prober.sendMessageToUi
import io.github.skydynamic.maiproberplus.core.utils.ChuniB30ImageGenerater
import io.github.skydynamic.maiproberplus.core.utils.MaimaiB50GenerateUtil
import io.github.skydynamic.maiproberplus.ui.component.ImagePreview
import io.github.skydynamic.maiproberplus.ui.component.WindowInsetsSpacer
import io.github.skydynamic.maiproberplus.ui.compose.scores.refreshScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class BestsImage(
    val gameType: GameType,
    val image: Bitmap,
)

object BestsImageGenerateViewModel : ViewModel() {
    var canGenerate by mutableStateOf(true)
    var bestsImage: BestsImage? by mutableStateOf(null)
}

fun checkEnable(platform: ProberPlatform): Boolean {
    return if (platform == ProberPlatform.LOCAL) {
        GlobalViewModel.gameType != GameType.Chunithm
    } else {
        true
    }
}

@Composable
fun BestsImageGenerateCompose() {
    val config = application.configManager.config
    var showImagePreview by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState())
    ) {
        WindowInsetsSpacer.TopPaddingSpacer()

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
            MaiContent(config = config) { bitmap ->
                BestsImageGenerateViewModel.bestsImage = BestsImage(
                    GameType.MaimaiDX,
                    bitmap
                )
                BestsImageGenerateViewModel.canGenerate = true
            }
        } else if (GlobalViewModel.gameType == GameType.Chunithm) {
            ChuniContent(config = config) { bitmap ->
                BestsImageGenerateViewModel.bestsImage = BestsImage(
                    GameType.Chunithm,
                    bitmap
                )
                BestsImageGenerateViewModel.canGenerate = true
            }
        }

        if (BestsImageGenerateViewModel.bestsImage != null) {
            AsyncImage(
                model = BestsImageGenerateViewModel.bestsImage!!.image,
                contentDescription = when (BestsImageGenerateViewModel.bestsImage!!.gameType) {
                    GameType.MaimaiDX -> "b50"
                    GameType.Chunithm -> "b30"
                },
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
                    val bitmap = BestsImageGenerateViewModel.bestsImage!!.image
                    val fileName = when (BestsImageGenerateViewModel.bestsImage!!.gameType) {
                        GameType.MaimaiDX -> "B50"
                        GameType.Chunithm -> "B30"
                    } + "_${System.currentTimeMillis()}"
                    application.saveImageToGallery(bitmap, fileName)
                }
            ) {
                Row {
                    Icon(painterResource(R.drawable.save_24px), null)

                    Text("保存图片到相册")
                }
            }
        }

        if (application.isLandscape) {
            WindowInsetsSpacer.BottomPaddingSpacer()
        }
    }

    BackHandler(showImagePreview) {
        showImagePreview = false
    }

    if (showImagePreview) {
        Popup(
            onDismissRequest = { showImagePreview = false },
            properties = PopupProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { showImagePreview = false }) // 点击外部关闭
                    }
            ) {
                ImagePreview(
                    image = BestsImageGenerateViewModel.bestsImage!!.image,
                    onDismiss = { showImagePreview = false }
                )
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
