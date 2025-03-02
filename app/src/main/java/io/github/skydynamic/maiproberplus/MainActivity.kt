package io.github.skydynamic.maiproberplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.skydynamic.maiproberplus.ui.AppContent
import io.github.skydynamic.maiproberplus.ui.theme.MaiProberplusTheme

val NOTIFICATION_CHANNEL_ID = "io.github.skydynamic.maiproberplus.notification.channel.default"
val PROCESS_NOTIFICATION_CHANNEL_ID = "io.github.skydynamic.maiproberplus.notification.channel.process"

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

        GlobalViewModel.localMessage.observe(this) { message ->
            GlobalViewModel.showMessageDialog = true
        }

        GlobalViewModel.needUpdate.observe(this) { needUpdate ->
            GlobalViewModel.showUpdateDialog = true
        }
    }
}
