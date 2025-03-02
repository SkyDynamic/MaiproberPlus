package io.github.skydynamic.maiproberplus.ui

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.utils.checkResourceComplete
import io.github.skydynamic.maiproberplus.core.utils.checkUpdate
import io.github.skydynamic.maiproberplus.ui.component.CheckUpdateDialog
import io.github.skydynamic.maiproberplus.ui.component.ConfirmDialog
import io.github.skydynamic.maiproberplus.ui.component.DownloadDialog
import io.github.skydynamic.maiproberplus.ui.component.InfoDialog
import io.github.skydynamic.maiproberplus.ui.component.WindowInsetsSpacer
import io.github.skydynamic.maiproberplus.ui.compose.bests.BestsImageGenerateCompose
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerCompose
import io.github.skydynamic.maiproberplus.ui.compose.setting.SettingCompose
import io.github.skydynamic.maiproberplus.ui.compose.sync.SyncCompose

@Composable
@SuppressLint("NewApi")
fun AppContent() {
    var openInitDownloadDialog by remember { mutableStateOf(false) }

    val items = listOf("成绩同步", "成绩管理", "图片生成", "设置")
    val selectedIcons = listOf(
        Icons.Filled.Refresh,
        Icons.Filled.Build,
        Icons.Filled.Star,
        Icons.Filled.Settings
    )
    val unselectedIcons = listOf(
        Icons.Outlined.Refresh,
        Icons.Outlined.Build,
        Icons.Filled.Star,
        Icons.Outlined.Settings
    )

    val composeList: List<@Composable () -> Unit> = listOf(
        @Composable { SyncCompose() },
        @Composable { ScoreManagerCompose() },
        @Composable { BestsImageGenerateCompose() },
        @Composable { SettingCompose() }
    )

    val checkResourceResult = checkResourceComplete()
    if (checkResourceResult.isNotEmpty()) {
        openInitDownloadDialog = true
    }

    LaunchedEffect(Unit) {
        if (application.configManager.config.localConfig.checkUpdate) {
            val release = checkUpdate()
            if (release != null) {
                val downloadFile = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                val newVersionFile = downloadFile.resolve(release.assets.first().name)
                if (newVersionFile.exists()) {
                    GlobalViewModel.newVersionApkUri = FileProvider.getUriForFile(
                        application, application.packageName + ".fileprovider", newVersionFile
                    )
                    GlobalViewModel.showInstallApkDialog = true
                } else {
                    GlobalViewModel.setLatestReleaseAndShowDialog(release)
                }
            } else {
                Log.i("checkUpdate", "No new release found")
            }
        }
    }

    Scaffold(
        Modifier.fillMaxSize()
    ) { insetsPadding ->
        GlobalViewModel.windowInsetsPadding = insetsPadding

        Box(
            Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (application.isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    NavigationRail(
                        containerColor = NavigationBarDefaults.containerColor,
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationRailItem(
                                icon = {
                                    Icon(
                                        if (GlobalViewModel.currentTab == index) selectedIcons[index] else unselectedIcons[index],
                                        contentDescription = item
                                    )
                                },
                                label = { Text(item) },
                                selected = GlobalViewModel.currentTab == index,
                                onClick = { GlobalViewModel.currentTab = index },
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                    }

                    Crossfade(
                        targetState = GlobalViewModel.currentTab,
                        animationSpec = tween(durationMillis = 500),
                        label = "pageCross",
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                start = if (application.isLandscape) 0.dp else WindowInsetsSpacer.startPadding,
                                end = WindowInsetsSpacer.endPadding
                            )
                    ) { targetState ->
                        composeList[targetState]()
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Crossfade(
                        targetState = GlobalViewModel.currentTab,
                        animationSpec = tween(durationMillis = 500),
                        label = "pageCross",
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                start = if (application.isLandscape) 0.dp else WindowInsetsSpacer.startPadding,
                                end = WindowInsetsSpacer.endPadding
                            )
                    ) { targetState ->
                        composeList[targetState]()
                    }
                    NavigationBar {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        if (GlobalViewModel.currentTab == index) selectedIcons[index] else unselectedIcons[index],
                                        contentDescription = item
                                    )
                                },
                                label = { Text(item) },
                                selected = GlobalViewModel.currentTab == index,
                                onClick = { GlobalViewModel.currentTab = index }
                            )
                        }
                    }
                }
            }
        }
    }

    when {
        GlobalViewModel.showMessageDialog -> {
            InfoDialog(GlobalViewModel.localMessage.value!!) {
                GlobalViewModel.showMessageDialog = false
            }
        }
        GlobalViewModel.showUpdateDialog -> {
            CheckUpdateDialog(
                release = GlobalViewModel.latestRelease,
                onRequest = {
                    application.createDownloadTask(
                        "https://ghfast.top/${GlobalViewModel.latestRelease!!.assets.first().url}",
                        GlobalViewModel.latestRelease!!.assets.first().name
                    )
                    GlobalViewModel.showUpdateDialog = false
                }
            ) {
                GlobalViewModel.showUpdateDialog = false
            }
        }
        GlobalViewModel.showInstallApkDialog -> {
            ConfirmDialog(
                info = "新版本已准备就绪, 是否马上安装",
                onRequest = {
                    application.installApk(GlobalViewModel.newVersionApkUri)
                    GlobalViewModel.showInstallApkDialog = false
                }
            ) {
                GlobalViewModel.showInstallApkDialog = false
            }
        }
        openInitDownloadDialog -> {
            DownloadDialog(checkResourceResult) {
                openInitDownloadDialog = false
            }
        }
    }
}
