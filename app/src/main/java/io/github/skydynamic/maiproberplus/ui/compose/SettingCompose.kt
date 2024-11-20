package io.github.skydynamic.maiproberplus.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiproberplus.Application
import io.github.skydynamic.maiproberplus.ui.compose.setting.PasswordTextFiled
import io.github.skydynamic.maiproberplus.ui.compose.setting.SettingItemGroup

@Composable
fun SettingCompose() {
    var divingfishToken by remember { mutableStateOf(Application.application.configManager.config.divingfishToken) }
    var lxnsToken by remember { mutableStateOf(Application.application.configManager.config.lxnsToken) }

    var divingfishTokenHidden by remember { mutableStateOf(true) }
    var lxnsTokenHidden by remember { mutableStateOf(true)}

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingItemGroup(
            modifier = Modifier.fillMaxSize().padding(top = 15.dp),
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
            ) {
                divingfishToken = it
                Application.application.configManager.config.divingfishToken = it
                Application.application.configManager.save()
            }

            PasswordTextFiled(
                modifier = Modifier.padding(15.dp).fillMaxWidth().height(60.dp),
                label = { Text("落雪查分器Token") },
                icon = { Icon(Icons.Filled.Lock, null) },
                hidden = lxnsTokenHidden,
                value = lxnsToken,
                onTrailingIconClick = {
                    lxnsTokenHidden = !lxnsTokenHidden
                },
            ) {
                lxnsToken = it
                Application.application.configManager.config.lxnsToken = it
                Application.application.configManager.save()
            }
        }
    }
}