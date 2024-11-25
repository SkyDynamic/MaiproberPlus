package io.github.skydynamic.maiproberplus.ui.compose.setting

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.skydynamic.maiproberplus.Application
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.BuildConfig
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniEnums
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.ui.compose.DiffChooseDialog

@Composable
fun SettingCompose() {
    var divingfishToken by remember { mutableStateOf(Application.application.configManager.config.divingfishToken) }
    var lxnsToken by remember { mutableStateOf(application.configManager.config.lxnsToken) }

    var divingfishTokenHidden by remember { mutableStateOf(true) }
    var lxnsTokenHidden by remember { mutableStateOf(true)}

    var showChooseMaimaiDiffDialog by remember { mutableStateOf(false) }
    var showChooseChuniDiffDialog by remember { mutableStateOf(false) }

    when {
        showChooseMaimaiDiffDialog -> {
            DiffChooseDialog(
                onRequest = {
                    application.configManager.config.syncConfig.maimaiSyncDifficulty = it
                    application.configManager.save()
                },
                onDismissRequest = {
                    showChooseMaimaiDiffDialog = false
                },
                defaultList = MaimaiEnums.Difficulty.entries.map { it.diffName },
                currentChoiceList = application.configManager.config.syncConfig.maimaiSyncDifficulty,
            )
        }
        showChooseChuniDiffDialog -> {
            DiffChooseDialog(
                onRequest = {
                    application.configManager.config.syncConfig.chuniSyncDifficulty = it
                    application.configManager.save()
                },
                onDismissRequest = {
                    showChooseChuniDiffDialog = false
                },
                defaultList = ChuniEnums.Difficulty.entries.map { it.diffName },
                currentChoiceList = application.configManager.config.syncConfig.chuniSyncDifficulty,
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
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
                    Application.application.configManager.config.divingfishToken = it
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
                    application.configManager.config.lxnsToken = it
                    application.configManager.save()
                }
            )
        }

        SettingItemGroup(
            modifier = Modifier.padding(top = 15.dp).wrapContentSize(),
            title = "成绩抓取设置"
        ) {
            TextButtonItem(
                title = "同步舞萌DX成绩的难度",
                description = "选择后将只同步选择的难度的成绩"
            ) {
                showChooseMaimaiDiffDialog = true
            }

            TextButtonItem(
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
            SwitchSettingItem(
                title = "成绩缓存本地",
                description = "开启后, 抓取成绩并上传到查分器时会缓存此次查分成绩到本地",
                checked = application.configManager.config.localConfig.cacheScore,
                onCheckedChange = {
                    application.configManager.config.localConfig.cacheScore = it
                    application.configManager.save()
                }
            )
        }

        SettingItemGroup(
            modifier = Modifier.padding(top = 15.dp).wrapContentSize(),
            title = "关于"
        ) {
            Text(
                "App版本: ${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE}",
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            TextButtonItem(
                title = "项目仓库",
                description = "项目的GitHub仓库"
            ) {
                val uri = Uri.parse("https://github.com/SkyDynamic/MaiproberPlus")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                application.startActivity(intent)
            }

            Text(
                "本项目遵循Apache LICENSE 2.0协议",
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}