package io.github.skydynamic.maiproberplus.ui.compose.setting

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.BuildConfig
import io.github.skydynamic.maiproberplus.core.config.ScoreDisplayType
import io.github.skydynamic.maiproberplus.core.config.ScoreStyleType
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniEnums
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.core.prober.sendMessageToUi
import io.github.skydynamic.maiproberplus.ui.component.ConfirmDialog
import io.github.skydynamic.maiproberplus.ui.component.DiffChooseDialog
import io.github.skydynamic.maiproberplus.ui.component.DownloadDialog
import io.github.skydynamic.maiproberplus.ui.component.MultiObjectSelectDialog
import io.github.skydynamic.maiproberplus.ui.component.WindowInsetsSpacer
import io.github.skydynamic.maiproberplus.ui.compose.scores.resources
import io.github.skydynamic.maiproberplus.ui.compose.setting.components.ScoreDisplayExampleLarge
import io.github.skydynamic.maiproberplus.ui.compose.setting.components.ScoreDisplayExampleMiddle
import io.github.skydynamic.maiproberplus.ui.compose.setting.components.ScoreDisplayExampleSmall
import io.github.skydynamic.maiproberplus.ui.compose.setting.components.SettingScoreStyleExampleColorOverlay
import io.github.skydynamic.maiproberplus.ui.compose.setting.components.SettingScoreStyleExampleTextShadow

@Composable
fun SettingCompose() {
    val maimaiShougouColorList = listOf("normal", "bronze", "silver", "gold", "rainbow")

    val config = application.configManager.config
    
    var divingfishToken by remember { mutableStateOf(config.divingfishToken) }
    var lxnsToken by remember { mutableStateOf(config.lxnsToken) }

    var userName by remember { mutableStateOf(config.userInfo.name) }
    var maimaiIcon by remember { mutableStateOf(config.userInfo.maimaiIcon.toString()) }
    var maimaiPlate by remember { mutableStateOf(config.userInfo.maimaiPlate.toString()) }
    var maimaiShougouText by remember { mutableStateOf(config.userInfo.shougou) }
    var maimaiShougouColor by remember { mutableStateOf(config.userInfo.shougouColor) }

    var divingfishTokenHidden by remember { mutableStateOf(true) }
    var lxnsTokenHidden by remember { mutableStateOf(true)}

    var scoreDisplayType by remember { mutableStateOf(config.scoreDisplayType) }
    var scoreColorOverlayType by remember { mutableStateOf(config.scoreStyleType) }

    var showChooseMaimaiDiffDialog by remember { mutableStateOf(false) }
    var showChooseChuniDiffDialog by remember { mutableStateOf(false) }
    var showSelectShougouColorDialog by remember { mutableStateOf(false) }

    var showConfirmUpdateSongResourceDialog by remember { mutableStateOf(false) }
    var showUpdateSongResourceDialog by remember { mutableStateOf(false) }

    val groupPadding = PaddingValues(15.dp)

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
        showSelectShougouColorDialog -> {
            MultiObjectSelectDialog(
                onRequest = {
                    maimaiShougouColor = it
                    config.userInfo.shougouColor = it
                    application.configManager.save()
                },
                onDismiss = {
                    showSelectShougouColorDialog = false
                },
                objects = maimaiShougouColorList,
            )
        }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(if (application.isLandscape) 2 else 1)
    ) {
        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            WindowInsetsSpacer.TopPaddingSpacer()
        }
        item {
            SettingItemGroup(
                modifier = Modifier
                    .padding(groupPadding)
                    .wrapContentSize(),
                title = "查分Token设置"
            ) {
                PasswordTextFiled(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                        .height(60.dp),
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
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                        .height(60.dp),
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
        }
        item {
            SettingItemGroup(
                modifier = Modifier
                    .padding(groupPadding)
                    .wrapContentSize(),
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
        }
        item {
            SettingItemGroup(
                modifier = Modifier
                    .padding(groupPadding)
                    .wrapContentSize(),
                title = "成绩展示设置"
            ) {
                BaseTextItem(
                    title = "成绩卡片排列",
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp)
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    Arrangement.SpaceAround,
                    Alignment.CenterVertically,
                ) {
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                scoreDisplayType = ScoreDisplayType.Small
                                config.scoreDisplayType = ScoreDisplayType.Small
                                application.configManager.save()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScoreDisplayExampleSmall(
                            Modifier
                                .padding(4.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                ScoreDisplayType.Small.displayName,
                            )
                            RadioButton(
                                selected = scoreDisplayType == ScoreDisplayType.Small,
                                onClick = null
                            )
                        }
                    }
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                scoreDisplayType = ScoreDisplayType.Middle
                                config.scoreDisplayType = ScoreDisplayType.Middle
                                application.configManager.save()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScoreDisplayExampleMiddle(
                            Modifier
                                .padding(4.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                ScoreDisplayType.Middle.displayName,
                            )
                            RadioButton(
                                selected = scoreDisplayType == ScoreDisplayType.Middle,
                                onClick = null
                            )
                        }
                    }
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                scoreDisplayType = ScoreDisplayType.Large
                                config.scoreDisplayType = ScoreDisplayType.Large
                                application.configManager.save()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScoreDisplayExampleLarge(
                            Modifier
                                .padding(4.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                ScoreDisplayType.Large.displayName,
                            )
                            RadioButton(
                                selected = scoreDisplayType == ScoreDisplayType.Large,
                                onClick = null
                            )
                        }
                    }
                }

                horizontalDivider()

                BaseTextItem(
                    title = "成绩卡片样式",
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp)
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    Arrangement.SpaceAround,
                    Alignment.CenterVertically,
                ) {
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.large)
                            .clickable {
                                scoreColorOverlayType = ScoreStyleType.ColorOverlay
                                config.scoreStyleType = ScoreStyleType.ColorOverlay
                                application.configManager.save()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SettingScoreStyleExampleColorOverlay(
                            Modifier
                                .padding(4.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                ScoreStyleType.ColorOverlay.displayName,
                            )
                            RadioButton(
                                selected = scoreColorOverlayType == ScoreStyleType.ColorOverlay,
                                onClick = null
                            )
                        }
                    }
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.large)
                            .clickable {
                                scoreColorOverlayType = ScoreStyleType.TextShadow
                                config.scoreStyleType = ScoreStyleType.TextShadow
                                application.configManager.save()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SettingScoreStyleExampleTextShadow(
                            Modifier
                                .padding(4.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                ScoreStyleType.TextShadow.displayName,
                            )
                            RadioButton(
                                selected = scoreColorOverlayType == ScoreStyleType.TextShadow,
                                onClick = null
                            )
                        }
                    }
                }

                horizontalDivider()
            }
        }
        item {
            SettingItemGroup(
                modifier = Modifier
                    .padding(groupPadding)
                    .wrapContentSize(),
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

                SwitchSettingItem(
                    title = "解析舞萌DX用户信息",
                    description = "开启后, 抓取舞萌DX成绩时会解析用户信息并保存",
                    checked = config.localConfig.parseMaimaiUserInfo,
                    onCheckedChange = {
                        config.localConfig.parseMaimaiUserInfo = it
                        application.configManager.save()
                    }
                )
            }
        }
        item {
            SettingItemGroup(
                modifier = Modifier
                    .padding(groupPadding)
                    .wrapContentSize(),
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
                        if (it.isDigitsOnly() && it.isNotEmpty()) {
                            config.userInfo.maimaiIcon = it.toInt()
                            application.configManager.save()
                            maimaiIcon = it
                        } else if (it.isEmpty()) {
                            config.userInfo.maimaiIcon = 1
                            application.configManager.save()
                            maimaiIcon = it
                        }
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
                        if (it.isDigitsOnly() && it.isNotEmpty()) {
                            config.userInfo.maimaiPlate = it.toInt()
                            application.configManager.save()
                            maimaiPlate = it
                        } else if (it.isEmpty()) {
                            config.userInfo.maimaiPlate = 1
                            application.configManager.save()
                            maimaiPlate = it
                        }
                    },
                    label = { Text("舞萌DX姓名框", fontSize = 12.sp) },
                    supportingText = {
                        Text("*不知道该参数的含义，请勿修改", color = Color.Red)
                    }
                )

                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                        .fillMaxWidth(),
                    value = maimaiShougouText,
                    onValueChange = {
                        config.userInfo.shougou = it
                        maimaiShougouText = it
                        application.configManager.save()
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.LightGray,
                    thickness = 1.dp
                )

                TextButtonItem(
                    modifier = Modifier
                        .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    title = "选择称号框颜色 (当前: $maimaiShougouColor)",
                    onClick = {
                        showSelectShougouColorDialog = true
                    }
                )

                TextButtonItem(
                    modifier = Modifier
                        .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    title = "打开资源ID对照表",
                    description = "点击后将跳转到本APP使用的资源ID对照页面"
                ) {
                    val uri = Uri.parse("https://rif.skydynamic.top")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    application.startActivity(intent)
                }
            }
        }
        item {
            SettingItemGroup(
                modifier = Modifier
                    .padding(groupPadding)
                    .wrapContentSize(),
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
        }
        item {
            SettingItemGroup(
                modifier = Modifier
                    .padding(groupPadding)
                    .wrapContentSize(),
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

                TextButtonItem(
                    modifier = Modifier
                        .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    title = "反馈问题",
                    description = "跳转到Github Issues界面进行问题反馈"
                ) {
                    val uri = Uri.parse("https://github.com/SkyDynamic/MaiproberPlus/issues")
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
        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            if (application.isLandscape) {
                WindowInsetsSpacer.BottomPaddingSpacer()
            }
        }
    }
}
