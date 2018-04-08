package com.sample.huutho.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File


fun File.addFileToGallery(context: Context){
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val contentUri = Uri.fromFile(this)
    mediaScanIntent.data = contentUri
    context.applicationContext.sendBroadcast(mediaScanIntent)
}