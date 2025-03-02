package io.github.skydynamic.maiproberplus

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Application
import android.app.DownloadManager
import android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import io.github.skydynamic.maiproberplus.core.ProberContext
import io.github.skydynamic.maiproberplus.core.config.ConfigManager
import io.github.skydynamic.maiproberplus.core.config.ConfigStorage
import io.github.skydynamic.maiproberplus.core.data.GameType
import io.github.skydynamic.maiproberplus.core.database.AppDatabase
import io.github.skydynamic.maiproberplus.core.prober.ProberPlatform
import io.github.skydynamic.maiproberplus.core.proxy.HttpServerService
import io.github.skydynamic.maiproberplus.core.utils.Release
import io.github.skydynamic.maiproberplus.receiver.InstallApkReceiver
import io.github.skydynamic.maiproberplus.vpn.core.LocalVpnService
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object GlobalViewModel : ViewModel() {
    var windowInsetsPadding by mutableStateOf(PaddingValues(0.dp))

    var isVpnServiceRunning by mutableStateOf(LocalVpnService.IsRunning)
    var showMessageDialog by mutableStateOf(false)
    var showUpdateDialog by mutableStateOf(false)
    var showInstallApkDialog by mutableStateOf(false)
    var proberPlatform by mutableStateOf(ProberPlatform.DIVING_FISH)
    var gameType by mutableStateOf(GameType.MaimaiDX)
    var maimaiHooking by mutableStateOf(false)
    var chuniHooking by mutableStateOf(false)

    var currentTab by mutableIntStateOf(0)

    var latestRelease: Release? by mutableStateOf(null)
    var newVersionApkUri: Uri by mutableStateOf(Uri.EMPTY)

    private val _localMessage = MutableLiveData<String>()
    val localMessage: LiveData<String> get() = _localMessage
    fun sendAndShowMessage(message: String) {
        _localMessage.value = message
    }

    private val _needUpdate = MutableLiveData<Boolean>()
    val needUpdate: LiveData<Boolean> get() = _needUpdate
    fun setLatestReleaseAndShowDialog(release: Release?) {
        latestRelease = release
        _needUpdate.value = true
    }
}

class Application : Application() {
    lateinit var configManager: ConfigManager
    lateinit var proberContext: ProberContext
    lateinit var db: AppDatabase

    val isLandscape: Boolean
        @Composable get() =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val layoutDirection: LayoutDirection
        @Composable get() =
            if (LocalConfiguration.current.layoutDirection == Configuration.SCREENLAYOUT_LAYOUTDIR_RTL) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        application = this
        configManager = ConfigManager(this)
        db = Room.databaseBuilder(this, AppDatabase::class.java, "maiproberplus").build()
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

    fun startHttpServer() {
        val intent = Intent(this, HttpServerService::class.java)
        startService(intent)
    }

    fun stopHttpServer() {
        val intent = Intent(this, HttpServerService::class.java).apply {
            action = HttpServerService.STOP_HTTP_SERVICE_INTENT
        }
        startService(intent)
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

        if (filesDir.resolve("b50cache").exists()) {
            totalSize += calculateDirectorySize(filesDir.resolve("b50cache"))
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

        if (filesDir.resolve("b50cache").exists()) {
            deleteDirectory(filesDir.resolve("b50cache"))
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

    fun getImageFromAssets(file: String): Bitmap? {
        val inputStream = assets.open(file)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        return bitmap
    }

    fun getBitmapFromDrawable(id: Int): Bitmap {
        val drawable = resources.getDrawable(id, null)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun getFont(fontId: Int): Typeface {
        return ResourcesCompat.getFont(this, fontId) ?: Typeface.DEFAULT
    }

    fun saveImageToGallery(bitmap: Bitmap, fileName: String) {
        val resolver = contentResolver
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + "/MaiProberPlus")

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        resolver.openOutputStream(uri!!).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it!!)
            Toast.makeText(this, "已保存到相册", Toast.LENGTH_SHORT).show()
        }
    }

    fun createDownloadTask(url: String, fileName: String) {
        val request = DownloadManager.Request(
            Uri.parse(url)
        )
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setTitle("下载MaiProberPlus更新")
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        val downloadCompleteReceiver = InstallApkReceiver(this, downloadManager, downloadId)
        val filter = IntentFilter(ACTION_DOWNLOAD_COMPLETE)
        // Android 12
        ContextCompat.registerReceiver(
            this, downloadCompleteReceiver, filter, ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun checkInstallPermission(): Boolean {
        return packageManager.canRequestPackageInstalls()
    }

    fun startInstallPermissionSettingActivity() {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.data = Uri.parse("package:$packageName")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            val fallbackIntent = Intent(Settings.ACTION_SECURITY_SETTINGS)
            fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(fallbackIntent)
        }
        Toast.makeText(this, "请允许安装未知来源应用", Toast.LENGTH_SHORT).show()
    }

    fun installApk(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setData(uri)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("App", "${e.message}")
        }
    }

    companion object {
        lateinit var application: io.github.skydynamic.maiproberplus.Application
    }
}