package io.github.skydynamic.maiproberplus

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
import io.github.skydynamic.maiproberplus.ui.compose.InfoDialog
import io.github.skydynamic.maiproberplus.ui.compose.SettingCompose
import io.github.skydynamic.maiproberplus.ui.compose.SyncCompose
import io.github.skydynamic.maiproberplus.ui.theme.MaiProberplusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaiProberplusTheme(
                dynamicColor = false
            ) {
                AppContent()
            }
        }

        GlobalViewModel.localMessage.observe(this, Observer { message ->
            GlobalViewModel.showMessageDialog = true
        })
    }
}

@Composable
@SuppressLint("NewApi")
fun AppContent() {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf("成绩同步", "设置")
    val selectedIcons = listOf(Icons.Filled.Refresh, Icons.Filled.Settings)
    val unselectedIcons =
        listOf(Icons.Outlined.Refresh, Icons.Outlined.Settings)

    val composeList: List<@Composable () -> Unit> = listOf(
        @Composable { SyncCompose() },
        @Composable { SettingCompose() }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                contentDescription = item
                            )
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Crossfade(
                targetState = selectedItem,
                animationSpec = tween(durationMillis = 500),
                label = "pageCross"
            ) { targetState ->
                composeList[targetState]()
            }
        }
    }

    if (GlobalViewModel.showMessageDialog) {
        InfoDialog(GlobalViewModel.localMessage.value!!) {
            GlobalViewModel.showMessageDialog = false
        }
    }
}
