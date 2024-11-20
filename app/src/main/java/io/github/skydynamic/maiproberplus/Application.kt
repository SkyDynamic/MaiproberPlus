package io.github.skydynamic.maiproberplus

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.skydynamic.maiproberplus.core.ProberContext
import io.github.skydynamic.maiproberplus.core.config.ConfigManager
import io.github.skydynamic.maiproberplus.core.config.ConfigStorage
import io.github.skydynamic.maiproberplus.core.proxy.HttpServerService
import io.github.skydynamic.maiproberplus.vpn.core.LocalVpnService
import java.io.FileOutputStream
import java.io.InputStream

object GlobalViewModel : ViewModel() {
    var isVpnServiceRunning by mutableStateOf(LocalVpnService.IsRunning)
    var showMessageDialog by mutableStateOf(false)
    var platformIndex by mutableIntStateOf(0)
    var gametypeIndex by mutableIntStateOf(0)

    private val _localMessage = MutableLiveData<String>()
    val localMessage: LiveData<String> get() = _localMessage
    fun sendAndShowMessage(message: String) {
        _localMessage.value = message
    }
}

class Application : Application() {
    lateinit var configManager: ConfigManager
    lateinit var proberContext: ProberContext

    override fun onCreate() {
        super.onCreate()
        application = this
        configManager = ConfigManager(this)
        startService(Intent(this, HttpServerService::class.java))
        this.initProberContext()
    }

    fun initProberContext() {
        proberContext = object : ProberContext {
            override fun requireConfig(): ConfigStorage {
                return configManager.config
            }
        }
    }

    fun getAssetAsString(fileName: String): String {
        return this.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    fun getAssetAsStream(fileName: String): InputStream {
        return this.assets.open(fileName)
    }

    fun getFilesDirInputStream(fileName: String): InputStream {
        return this.openFileInput(fileName)
    }

    fun getFilesDirOutputStream(fileName: String): FileOutputStream {
        return this.openFileOutput(fileName, MODE_PRIVATE)
    }

    fun startWechat() {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("App", "${e.message}")
        }
    }

    fun copyTextToClipboard(text: String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("text", text))
        Toast.makeText(this, "已复制Hook链接到剪切板\n请开启劫持后复制到微信打开", Toast.LENGTH_SHORT).show()
    }

    companion object {
        lateinit var application: io.github.skydynamic.maiproberplus.Application
    }
}