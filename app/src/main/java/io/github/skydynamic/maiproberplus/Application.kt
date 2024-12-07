package io.github.skydynamic.maiproberplus

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import io.github.skydynamic.maiproberplus.core.ProberContext
import io.github.skydynamic.maiproberplus.core.config.ConfigManager
import io.github.skydynamic.maiproberplus.core.config.ConfigStorage
import io.github.skydynamic.maiproberplus.core.database.AppDatabase
import io.github.skydynamic.maiproberplus.core.prober.ProberPlatform
import io.github.skydynamic.maiproberplus.core.proxy.HttpServerService
import io.github.skydynamic.maiproberplus.ui.compose.GameType
import io.github.skydynamic.maiproberplus.vpn.core.LocalVpnService
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object GlobalViewModel : ViewModel() {
    var isVpnServiceRunning by mutableStateOf(LocalVpnService.IsRunning)
    var showMessageDialog by mutableStateOf(false)
    var proberPlatform by mutableStateOf(ProberPlatform.DIVING_FISH)
    var gameType by mutableStateOf(GameType.MaimaiDX)
    var maimaiHooking by mutableStateOf(false)
    var chuniHooking by mutableStateOf(false)

    private val _localMessage = MutableLiveData<String>()
    val localMessage: LiveData<String> get() = _localMessage
    fun sendAndShowMessage(message: String) {
        _localMessage.value = message
    }
}

class Application : Application() {
    lateinit var configManager: ConfigManager
    lateinit var proberContext: ProberContext
    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        application = this
        configManager = ConfigManager(this)
        db = Room.databaseBuilder(this, AppDatabase::class.java, "maiproberplus").build()
        startService(Intent(this, HttpServerService::class.java))
        this.initProberContext()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val defaultChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "DefaultMaiProberNotification",
            importance
        )
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        
        notificationManager.createNotificationChannel(defaultChannel)
    }

    fun sendNotification(
        title: String,
        message: String
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@Application,
                    POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(1, builder.build())
        }
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

    private fun getCacheSize(): Long {
        val cacheDir = cacheDir
        val externalCacheDir = externalCacheDir

        var totalSize = 0L

        totalSize += calculateDirectorySize(cacheDir)
        if (externalCacheDir != null) {
            totalSize += calculateDirectorySize(externalCacheDir)
        }

        return totalSize
    }

    private fun calculateDirectorySize(directory: File?): Long {
        if (directory == null || !directory.exists()) {
            return 0
        }

        var size = 0L
        for (file in directory.listFiles() ?: emptyArray()) {
            size += if (file.isDirectory) {
                calculateDirectorySize(file)
            } else {
                file.length()
            }
        }
        return size
    }

    fun clearCache(): Long {
        val cacheDir = cacheDir
        val externalCacheDir = externalCacheDir

        val totalSize = getCacheSize()

        deleteDirectory(cacheDir)
        if (externalCacheDir != null) {
            deleteDirectory(externalCacheDir)
        }

        return totalSize
    }

    fun deleteDirectory(directory: File?) {
        if (directory == null || !directory.exists()) {
            return
        }

        for (file in directory.listFiles() ?: emptyArray()) {
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete()
            }
        }
        directory.delete()
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