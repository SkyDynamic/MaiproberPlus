package io.github.skydynamic.maiproberplus.ui.compose.sync

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.data.GameType
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniScoreManager.writeChuniScoreCache
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiScoreManager.writeMaimaiScoreCache
import io.github.skydynamic.maiproberplus.core.prober.ProberPlatform
import io.github.skydynamic.maiproberplus.core.prober.sendMessageToUi
import io.github.skydynamic.maiproberplus.core.proxy.HttpServer
import io.github.skydynamic.maiproberplus.ui.component.ConfirmDialog
import io.github.skydynamic.maiproberplus.ui.component.DownloadDialog
import io.github.skydynamic.maiproberplus.ui.component.InfoDialog
import io.github.skydynamic.maiproberplus.ui.component.WindowInsetsSpacer
import io.github.skydynamic.maiproberplus.ui.compose.scores.refreshScore
import io.github.skydynamic.maiproberplus.ui.compose.scores.resources
import io.github.skydynamic.maiproberplus.ui.compose.setting.PasswordTextFiled
import io.github.skydynamic.maiproberplus.vpn.core.LocalVpnService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncCompose() {
    val context = LocalContext.current
    val viewModel = remember { SyncViewModel }
    val globalViewModel = remember { GlobalViewModel }

    var divingfishToken by remember { mutableStateOf(application.configManager.config.divingfishToken) }
    var lxnsToken by remember { mutableStateOf(application.configManager.config.lxnsToken) }

    var openAskIsOverwriteScoresDialog by remember { mutableStateOf(false) }
    var openAskOverwriteUserInfo by remember { mutableStateOf(false) }

    val vpnRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startVpnService(context as Activity)
        }
    }

    when {
        openAskOverwriteUserInfo -> {
            ConfirmDialog(
                info = "是否获取个人信息并覆盖当前个人信息",
                onRequest = {
                    val token = when (globalViewModel.proberPlatform) {
                        ProberPlatform.DIVING_FISH ->
                            application.configManager.config.divingfishToken
                        else ->
                            application.configManager.config.lxnsToken
                    }

                    if (token.isEmpty()) {
                        sendMessageToUi("请先设置token")
                        return@ConfirmDialog
                    }

                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        val proberUtil = globalViewModel.proberPlatform.factory

                        fun sendSyncSuccessMessageToUi() {
                            sendMessageToUi("成功从${
                                globalViewModel.proberPlatform.proberName
                            }同步${globalViewModel.gameType.displayName}玩家数据")
                        }

                        proberUtil.updateUserInfo(token)

                        sendSyncSuccessMessageToUi()
                    }
                }
            ) {
                openAskOverwriteUserInfo = false
            }
        }
        openAskIsOverwriteScoresDialog -> {
            ConfirmDialog(
                info = "确认同步数据并添加到数据库吗?",
                onRequest = {
                    val token = when (globalViewModel.proberPlatform) {
                        ProberPlatform.DIVING_FISH ->
                            application.configManager.config.divingfishToken
                        else ->
                            application.configManager.config.lxnsToken
                    }

                    if (token.isEmpty()) {
                        sendMessageToUi("请先设置token")
                        return@ConfirmDialog
                    }

                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        val proberUtil = globalViewModel.proberPlatform.factory

                        fun sendSyncSuccessMessageToUi() {
                            sendMessageToUi("成功从${
                                globalViewModel.proberPlatform.proberName
                            }同步${globalViewModel.gameType.displayName}成绩")
                        }

                        when (globalViewModel.gameType) {
                            GameType.MaimaiDX -> {
                                val result = proberUtil.getMaimaiProberData(token)
                                if (result.isNotEmpty()) {
                                    writeMaimaiScoreCache(result)
                                    sendSyncSuccessMessageToUi()
                                }
                            }
                            GameType.Chunithm -> {
                                val result = proberUtil.getChuniProberData(token)
                                if (result.isNotEmpty()) {
                                    writeChuniScoreCache(result)
                                    sendSyncSuccessMessageToUi()
                                }
                            }
                        }
                    }
                }
            ) {
                openAskIsOverwriteScoresDialog = false
            }
        }
        SyncViewModel.openInitDialog -> {
            InfoDialog("首次启动需要下载资源文件，请耐心等待") {
                SyncViewModel.openInitDialog = false
                SyncViewModel.openInitDownloadDialog = true
            }
        }
        SyncViewModel.openInitDownloadDialog -> {
            DownloadDialog(
                resources
            ) {
                SyncViewModel.openInitDownloadDialog = false
            }
        }
    }

    @Composable
    fun partA(
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .padding(16.dp),
                    onClick = {
                        if (!checkResourceComplate(context)) {
                            SyncViewModel.openInitDialog = true
                        } else {
                            if (!globalViewModel.isVpnServiceRunning) {
                                val intent = VpnService.prepare(context)
                                if (intent != null) {
                                    vpnRequestLauncher.launch(intent)
                                } else {
                                    startVpnService(context as Activity)
                                }
                                application.startHttpServer()
                            } else {
                                stopVpnService(context as Activity)
                                application.stopHttpServer()
                            }
                        }
                    },
                    enabled = !GlobalViewModel.maimaiHooking || !GlobalViewModel.chuniHooking
                ) {
                    if (!globalViewModel.isVpnServiceRunning)
                        Text("开启劫持")
                    else Text("结束劫持")
                }

                Button(
                    modifier = Modifier
                        .padding(16.dp),
                    onClick = { application.startWechat() }
                ) {
                    Text("启动微信")
                }
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                ProberPlatform.entries.forEach {
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = it.ordinal,
                            count = ProberPlatform.entries.size,
                        ),
                        onClick = { globalViewModel.proberPlatform = it },
                        selected = it == globalViewModel.proberPlatform
                    ) {
                        Text(it.proberName)
                    }
                }
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                GameType.entries.forEach {
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = it.ordinal, count = GameType.entries.size
                        ),
                        selected = globalViewModel.gameType == it,
                        onClick = {
                            globalViewModel.gameType = it
                            refreshScore(it)
                        },
                    ) {
                        Text(it.displayName)
                    }
                }
            }

            PasswordTextFiled(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
                    .height(75.dp),
                label = { Text("查分器Token") },
                icon = { Icon(Icons.Filled.Lock, null) },
                hidden = SyncViewModel.tokenHidden,
                value = when (globalViewModel.proberPlatform) {
                    ProberPlatform.DIVING_FISH -> divingfishToken
                    ProberPlatform.LXNS -> lxnsToken
                    ProberPlatform.LOCAL -> ""
                },
                onTrailingIconClick = { SyncViewModel.tokenHidden = !SyncViewModel.tokenHidden },
                onValueChange = {
                    when (globalViewModel.proberPlatform) {
                        ProberPlatform.DIVING_FISH -> {
                            divingfishToken = it
                            application.configManager.config.divingfishToken = it
                        }

                        else -> {
                            lxnsToken = it
                            application.configManager.config.lxnsToken = it
                        }
                    }
                    application.configManager.save()
                },
                enable = globalViewModel.proberPlatform != ProberPlatform.LOCAL,
                horizontalDivider = false,
            )
        }
    }

    @Composable
    fun partB(
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .padding(15.dp)
                    .size(300.dp, 50.dp),
                onClick = {
                    application.copyTextToClipboard("http://127.0.0.2:${HttpServer.Port}/${globalViewModel.gameType.ordinal}")
                }
            ) {
                Text("复制${globalViewModel.gameType.displayName} Hook链接(长期有效)")
            }

            Button(
                modifier = Modifier
                    .padding(15.dp)
                    .size(300.dp, 50.dp),
                onClick = {
                    if (!checkResourceComplate(context)) {
                        SyncViewModel.openInitDialog = true
                    } else {
                        openAskIsOverwriteScoresDialog = true
                    }
                },
                enabled = globalViewModel.proberPlatform != ProberPlatform.LOCAL
            ) {
                Text("从选定的查分器获取 ${globalViewModel.gameType.displayName} 成绩")
            }

            Button(
                modifier = Modifier
                    .padding(15.dp)
                    .size(300.dp, 50.dp),
                onClick = {
                    openAskOverwriteUserInfo = true
                },
                enabled = globalViewModel.proberPlatform != ProberPlatform.LOCAL
            ) {
                Text("从选定的查分器获取 ${globalViewModel.gameType.displayName} 个人信息")
            }

            AnimatedVisibility(
                GlobalViewModel.maimaiHooking
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("正在获取 舞萌DX 成绩")
                    LinearProgressIndicator()
                }
            }

            AnimatedVisibility(
                GlobalViewModel.chuniHooking
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("正在获取 中二节奏 成绩")
                    LinearProgressIndicator()
                }
            }

        }
    }

    if (application.isLandscape) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            WindowInsetsSpacer.TopPaddingSpacer()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                partA(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                )
                VerticalDivider(Modifier.fillMaxHeight())
                partB(Modifier.weight(1f))
            }
            WindowInsetsSpacer.BottomPaddingSpacer()
        }
    } else {
        Row(
            Modifier
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                WindowInsetsSpacer.TopPaddingSpacer()
                partA()
                HorizontalDivider()
                partB()
            }
        }
    }
}

private fun startVpnService(activity: Activity) {
    val intent = Intent(activity, LocalVpnService::class.java)
    activity.startService(intent)
}

private fun stopVpnService(activity: Activity) {
    val intent = Intent(activity, LocalVpnService::class.java).apply {
        action = LocalVpnService.DISCONNECT_INTENT
    }
    activity.startService(intent)
}

private fun checkResourceComplate(context: Context): Boolean {
    return context.filesDir.resolve("maimai_song_list.json").exists()
            || context.filesDir.resolve("chuni_song_list.json").exists()
}
