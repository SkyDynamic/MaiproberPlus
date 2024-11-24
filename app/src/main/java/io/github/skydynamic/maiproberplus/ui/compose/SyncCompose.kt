package io.github.skydynamic.maiproberplus.ui.compose

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import io.github.skydynamic.maiproberplus.vpn.core.LocalVpnService
import io.github.skydynamic.maiproberplus.Application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.prober.ProberPlatform
import io.github.skydynamic.maiproberplus.core.proxy.HttpServer
import io.github.skydynamic.maiproberplus.ui.compose.setting.PasswordTextFiled

data class FileDownloadMeta(
    val fileName: String,
    val fileSavePath: String,
    val fileDownloadUrl: String
)

val application: Application = Application.application

object SyncViewModel : ViewModel() {
    var openInitDialog by mutableStateOf(false)
    var openInitDownloadDialog by mutableStateOf(false)
    var tokenHidden by mutableStateOf(true)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SyncCompose() {
    val context = LocalContext.current
    val viewModel = remember { SyncViewModel }
    val globalViewModel = remember { GlobalViewModel }

    var divingfishToken by remember { mutableStateOf(application.configManager.config.divingfishToken) }
    var lxnsToken by remember { mutableStateOf(application.configManager.config.lxnsToken) }

    val proberPlatformList = ProberPlatform.entries.map { it.proberName }
    val gameTypeList = listOf("舞萌DX", "中二节奏")

    val vpnRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startVpnService(context as Activity)
        }
    }

    when {
        viewModel.openInitDialog -> {
            InfoDialog("首次启动需要下载资源文件，请耐心等待") {
                viewModel.openInitDialog = false
                viewModel.openInitDownloadDialog = true
            }
        }
        viewModel.openInitDownloadDialog -> {
            DownloadDialog(
                listOf(
                    FileDownloadMeta(
                        "maimai_song_list.json",
                        ".",
                        "https://maimai.lxns.net/api/v0/maimai/song/list?notes=true"
                    ),
                    FileDownloadMeta(
                        "chuni_song_list.json",
                        ".",
                        "https://maimai.lxns.net/api/v0/chunithm/song/list"
                    )
                )
            ) {
                viewModel.openInitDownloadDialog = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier
                    .padding(15.dp)
                    .height(50.dp),
                onClick = {
                    if (!context.filesDir.resolve("maimai_song_list.json").exists() ||
                        !context.filesDir.resolve("chuni_song_list.json").exists()
                    ) {
                        viewModel.openInitDialog = true
                    }
                    if (!globalViewModel.isVpnServiceRunning) {
                        var intent = VpnService.prepare(context)
                        if (intent != null) {
                            vpnRequestLauncher.launch(intent)
                        } else {
                            startVpnService(context as Activity)
                        }
                    } else {
                        stopVpnService(context as Activity)
                    }
                }
            ) {
                if (!globalViewModel.isVpnServiceRunning) Text("开启劫持") else Text("结束劫持")
            }

            Button(
                modifier = Modifier
                    .padding(15.dp)
                    .height(50.dp),
                onClick = { application.startWechat() }
            ) {
                Text("启动微信")
            }
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            proberPlatformList.forEachIndexed { index, name ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = proberPlatformList.size
                    ),
                    onClick = { globalViewModel.platformIndex = index },
                    selected = index == globalViewModel.platformIndex
                ) {
                    Text(name)
                }
            }
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            gameTypeList.forEachIndexed { index, name ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = gameTypeList.size
                    ),
                    onClick = { globalViewModel.gametypeIndex = index },
                    selected = index == globalViewModel.gametypeIndex
                ) {
                    Text(name)
                }
            }
        }

        PasswordTextFiled(
            modifier = Modifier.padding(15.dp).fillMaxWidth().height(75.dp),
            label = { Text("查分器Token") },
            icon = { Icon(Icons.Filled.Lock, null) },
            hidden = viewModel.tokenHidden,
            value = if (globalViewModel.platformIndex == 0) divingfishToken else lxnsToken,
            onTrailingIconClick = { viewModel.tokenHidden = !viewModel.tokenHidden }
        ) {
            if (globalViewModel.platformIndex == 0) {
                divingfishToken = it
                application.configManager.config.divingfishToken = it
            } else {
                lxnsToken = it
                application.configManager.config.lxnsToken = it
            }
            application.configManager.save()
        }

        Button(
            modifier = Modifier
                .padding(15.dp)
                .size(300.dp, 50.dp),
            onClick = {
                application.copyTextToClipboard("http://127.0.0.2:${HttpServer.Port}/${globalViewModel.gametypeIndex}")
            }
        ) {
            val gameName = if (globalViewModel.gametypeIndex == 0) "舞萌DX" else "中二节奏"
            Text("复制${gameName} Hook链接(长期有效)")
        }
    }
}

private fun startVpnService(activity: Activity) {
    val intent = Intent(activity, LocalVpnService::class.java)
    activity.startService(intent)
}

private fun stopVpnService(activity: Activity) {
    val intent = Intent(activity, LocalVpnService::class.java).apply { action = LocalVpnService.DISCONNECT_INTENT }
    activity.startService(intent)
}