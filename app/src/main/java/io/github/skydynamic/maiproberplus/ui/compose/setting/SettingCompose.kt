package io.github.skydynamic.maiproberplus.ui.compose.setting

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.BuildConfig
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniEnums
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.core.prober.sendMessageToUi
import io.github.skydynamic.maiproberplus.ui.compose.ConfirmDialog
import io.github.skydynamic.maiproberplus.ui.compose.DiffChooseDialog
import io.github.skydynamic.maiproberplus.ui.compose.DownloadDialog
import io.github.skydynamic.maiproberplus.ui.compose.scores.resources

@Composable
fun SettingCompose() {
    val config = application.configManager.config
    
    var divingfishToken by remember { mutableStateOf(config.divingfishToken) }
    var lxnsToken by remember { mutableStateOf(config.lxnsToken) }

    var userName by remember { mutableStateOf(config.userInfo.name) }
    var maimaiIcon by remember { mutableIntStateOf(config.userInfo.maimaiIcon) }
    var maimaiPlate by remember { mutableIntStateOf(config.userInfo.maimaiPlate) }

    var divingfishTokenHidden by remember { mutableStateOf(true) }
    var lxnsTokenHidden by remember { mutableStateOf(true)}

    var showChooseMaimaiDiffDialog by remember { mutableStateOf(false) }
    var showChooseChuniDiffDialog by remember { mutableStateOf(false) }

    var showConfirmUpdateSongResourceDialog by remember { mutableStateOf(false) }
    var showUpdateSongResourceDialog by remember { mutableStateOf(false) }

    when {
        showChooseMaimaiDiffDialog -> {
            DiffChooseDialog(
                onRequest = {
                    config.syncConfig.maimaiSyncDifficulty = it
                    application.configManager.save()
                },
                onDismissRequest = {
                    showChooseMaimaiDiffDialog = false
                },
                defaultList = MaimaiEnums.Difficulty.entries.map { it.diffName },
                currentChoiceList = config.syncConfig.maimaiSyncDifficulty,
            )
        }
        showChooseChuniDiffDialog -> {
            DiffChooseDialog(
                onRequest = {
                    config.syncConfig.chuniSyncDifficulty = it
                    application.configManager.save()
                },
                onDismissRequest = {
                    showChooseChuniDiffDialog = false
                },
                defaultList = ChuniEnums.Difficulty.entries.map { it.diffName },
                currentChoiceList = config.syncConfig.chuniSyncDifficulty,
            )
        }
        showConfirmUpdateSongResourceDialog -> {
            ConfirmDialog(
                info = "是否确认更新资源",
                onRequest = {
                    showUpdateSongResourceDialog = true
                },
                onDismiss = {
                    showConfirmUpdateSongResourceDialog = false
                }
            )
        }
        showUpdateSongResourceDialog -> {
            DownloadDialog(
                resources
            ) {
                showUpdateSongResourceDialog = false
                sendMessageToUi("更新完成")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SettingItemGroup(
            modifier = Modifier.padding(top = 15.dp).wrapContentSize(),
            title = "查分Token设置"
        ) {
            PasswordTextFiled(
                modifier = Modifier.padding(15.dp).fillMaxWidth().height(60.dp),
                label = { Text("水鱼查分器Token") },
                icon = { Icon(Icons.Filled.Lock, null) },
                hidden = divingfishTokenHidden,
                value = divingfishToken,
                onTrailingIconClick = {
                    divingfishTokenHidden = !divingfishTokenHidden
                },
                onValueChange = {
                    divingfishToken = it
                    config.divingfishToken = it
                    application.configManager.save()
                }
            )

            PasswordTextFiled(
                modifier = Modifier.padding(15.dp).fillMaxWidth().height(60.dp),
                label = { Text("落雪查分器Token") },
                icon = { Icon(Icons.Filled.Lock, null) },
                hidden = lxnsTokenHidden,
                value = lxnsToken,
                onTrailingIconClick = {
                    lxnsTokenHidden = !lxnsTokenHidden
                },
                onValueChange = {
                    lxnsToken = it
                    config.lxnsToken = it
                    application.configManager.save()
                }
            )
        }

        SettingItemGroup(
            modifier = Modifier.padding(top = 15.dp).wrapContentSize(),
            title = "成绩抓取设置"
        ) {
            TextButtonItem(
                modifier = Modifier
                    .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                title = "同步舞萌DX成绩的难度",
                description = "选择后将只同步选择的难度的成绩"
            ) {
                showChooseMaimaiDiffDialog = true
            }

            TextButtonItem(
                modifier = Modifier
                    .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                title = "同步中二节奏成绩的难度",
                description = "选择后将只同步选择的难度的成绩"
            ) {
                showChooseChuniDiffDialog = true
            }
        }

        SettingItemGroup(
            modifier = Modifier.padding(top = 15.dp).wrapContentSize(),
            title = "本地设置"
        ) {
            TextButtonItem(
                modifier = Modifier
                    .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                title = "更新歌曲信息与别名",
                description = "会从Lxns的API获取舞萌和中二歌曲信息与别名并覆盖本地文件"
            ) {
                showConfirmUpdateSongResourceDialog = true
            }

            SwitchSettingItem(
                title = "成绩缓存本地",
                description = "开启后, 抓取成绩并上传到查分器时会缓存此次查分成绩到本地",
                checked = config.localConfig.cacheScore,
                onCheckedChange = {
                    config.localConfig.cacheScore = it
                    application.configManager.save()
                }
            )
        }

        SettingItemGroup(
            modifier = Modifier.padding(top = 15.dp).wrapContentSize(),
            title = "用户信息"
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                value = userName,
                onValueChange = {
                    config.userInfo.name = it
                    userName = it
                    application.configManager.save()
                },
                label = { Text("用户名", fontSize = 12.sp) },
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                value = maimaiIcon.toString(),
                onValueChange = {
                    config.userInfo.maimaiIcon = it.toInt()
                    maimaiIcon = it.toInt()
                    application.configManager.save()
                },
                label = { Text("舞萌DX头像", fontSize = 12.sp) },
                supportingText = {
                    Text("*不知道该参数的含义，请勿修改", color = Color.Red)
                }
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                value = maimaiPlate.toString(),
                onValueChange = {
                    config.userInfo.maimaiPlate = it.toInt()
                    maimaiPlate = it.toInt()
                    application.configManager.save()
                },
                label = { Text("舞萌DX姓名框", fontSize = 12.sp) },
                supportingText = {
                    Text("*不知道该参数的含义，请勿修改", color = Color.Red)
                }
            )
        }

        SettingItemGroup(
            modifier = Modifier.padding(top = 15.dp).wrapContentSize(),
            title = "其他"
        ) {
            TextButtonItem(
                modifier = Modifier
                    .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                title = "清除缓存",
                description = "清除APP产生的缓存"
            ) {
                val clearSize = application.clearCache()
                sendMessageToUi(
                    "清除缓存成功, 释放了${clearSize / 1024 / 1024}MB缓存",
                )
            }
        }

        SettingItemGroup(
            modifier = Modifier.padding(top = 15.dp).wrapContentSize(),
            title = "关于"
        ) {
            Text(
                "App版本: ${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE}",
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(15.dp, top = 0.dp, bottom = 0.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            TextButtonItem(
                modifier = Modifier
                    .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                title = "项目仓库",
                description = "项目的GitHub仓库"
            ) {
                val uri = Uri.parse("https://github.com/SkyDynamic/MaiproberPlus")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                application.startActivity(intent)
            }

            Text(
                """
                    特别感谢Lxns提供的API与优秀的成绩管理页面设计
                    也特别感谢愿意给此项目贡献的开发者与参与APP测试的朋友们
                    本项目遵循Apache LICENSE 2.0协议
                """.trimIndent(),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(15.dp, top = 0.dp, bottom = 0.dp)
            )
        }
    }
}