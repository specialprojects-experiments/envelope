package com.specialprojects.experiments.envelopecall

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore


object FileDownloader {
    fun maybeStartDownload(context: Context, uriString: String): Long {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val fileUri = Uri.parse(uriString)
        val fileName = getFileName(fileUri)
        val request = DownloadManager.Request(fileUri).apply {
            setTitle(fileName)
            setDescription(fileName)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }

        return downloadManager.enqueue(request)
    }

    fun getFileMimeType(context: Context, id: Long): String {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        return downloadManager.getMimeTypeForDownloadedFile(id)
    }

    private fun getFileName(uri: Uri): String? {
        var result = uri.path

        uri.path?.also {
            val cut = it.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }

            return result
        }

        return null
    }
}