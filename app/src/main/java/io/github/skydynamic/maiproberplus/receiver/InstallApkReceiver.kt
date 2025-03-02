package io.github.skydynamic.maiproberplus.receiver

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.skydynamic.maiproberplus.Application
import io.github.skydynamic.maiproberplus.GlobalViewModel

class InstallApkReceiver(
    val application: Context,
    val downloadManager: DownloadManager,
    val downloadId: Long
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadId != -1L && id != -1L) {
                if (downloadId == id) {
                    checkStatus()
                }
            }
            installApk()
        }
    }

    @SuppressLint("Range")
    fun checkStatus() {
        val query = DownloadManager.Query()
        val cursor = downloadManager.query(query)
        if (!cursor.moveToFirst()) {
            cursor.close()
        } else {
            val id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
            query.setFilterById(id)
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                installApk()
                cursor.close()
            }
        }
    }

    fun installApk() {
        if (Application.application.checkInstallPermission()) {
            val uri = downloadManager.getUriForDownloadedFile(downloadId)
            if (uri != null) {
                GlobalViewModel.newVersionApkUri = uri
                GlobalViewModel.showInstallApkDialog = true
            }
        } else {
            Application.application.startInstallPermissionSettingActivity()
            installApk()
        }
    }
}