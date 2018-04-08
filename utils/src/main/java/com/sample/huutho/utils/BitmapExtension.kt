package com.sample.huutho.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.annotation.DrawableRes
import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by ThoNh on 2/12/2018.
 */

private fun calculateInSampleSize(options: BitmapFactory.Options,
                                  reqWidth: Int = Resources.getSystem().displayMetrics.widthPixels,
                                  reqHeight: Int = Resources.getSystem().displayMetrics.heightPixels): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while ((halfWidth / inSampleSize) >= reqWidth && (halfHeight / inSampleSize) >= reqHeight) {
            inSampleSize *= 2
        }
    }
    Log.e("ThoNH", "calculateInSampleSize -> inSampleSize = $inSampleSize")
    return inSampleSize
}


/**
 * @use call this func on async(UI){}
 */
fun Any.decodeStream(context: Context,
                     uri: Uri,
                     reqWidth: Int = Resources.getSystem().displayMetrics.widthPixels,
                     reqHeight: Int = Resources.getSystem().displayMetrics.heightPixels,
                     start: () -> Unit = {},
                     success: (bmp: Bitmap) -> Unit = {},
                     failure: () -> Unit = {}) = try {

    Log.e("ThoNH", "decodeStream(context, uri: $uri, reqWidth: $reqWidth, reqHeight: $reqHeight)")
    async(UI) {
        start.invoke()
        val bmp = async {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)
        }
        success.invoke(bmp.await())
    }
} catch (ex: Exception) {
    ex.printStackTrace();
    failure.invoke()
}


/**
 * @use call this func on async(UI){}
 */
fun Any.decodeResource(context: Context,
                       @DrawableRes resId: Int,
                       reqWidth: Int = Resources.getSystem().displayMetrics.widthPixels,
                       reqHeight: Int = Resources.getSystem().displayMetrics.heightPixels,
                       start: () -> Unit = {},
                       success: (bmp: Bitmap) -> Unit = {},
                       failure: () -> Unit = {}) = try {

    Log.e("ThoNH", "decodeResource(context, resId: $resId, reqWidth: $reqWidth, reqHeight: $reqHeight)")

    async(UI) {
        start.invoke()
        val bmpAwait = async {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(context.resources, resId, options)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            BitmapFactory.decodeResource(context.resources, resId, options)
        }
        success.invoke(bmpAwait.await())
    }


} catch (ex: Exception) {
    ex.printStackTrace()
    failure.invoke()
}


/**
 * @use call this func on async(UI){}
 */
fun Any.decodeFile(pathFile: String,
                   reqWidth: Int = Resources.getSystem().displayMetrics.widthPixels,
                   reqHeight: Int = Resources.getSystem().displayMetrics.heightPixels,
                   start: () -> Unit = {},
                   success: (bmp: Bitmap) -> Unit = {},
                   failure: () -> Unit = {}) = try {

    Log.e("ThoNH", "decodeFile(pathFile: $pathFile, reqWidth: $reqWidth, reqHeight: $reqHeight)")
    async(UI) {
        start.invoke()
        val bmp = async {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(pathFile, options)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            BitmapFactory.decodeFile(pathFile, options)
        }
        success.invoke(bmp.await())
    }
} catch (ex: Exception) {
    ex.printStackTrace()
    failure.invoke()
}


/**
 * @use call this func on async(UI){}
 */
fun Any.decodeFileDescriptor(
        fileDescriptor: FileDescriptor,
        reqWidth: Int = Resources.getSystem().displayMetrics.widthPixels,
        reqHeight: Int = Resources.getSystem().displayMetrics.heightPixels,
        start: () -> Unit = {},
        success: (bmp: Bitmap) -> Unit = {},
        failure: () -> Unit = {}) = try {

    Log.e("ThoNH", "decodeFileDescriptor(fileDescriptor: $fileDescriptor, reqWidth: $reqWidth, reqHeight: $reqHeight)")

    async(UI) {
        start.invoke()
        val bmp = async {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
        }
        success.invoke(bmp.await())
    }
} catch (ex: Exception) {
    ex.printStackTrace()
    failure.invoke()
}


//https://github.com/codepath/android_guides/wiki/Sharing-Content-with-Intents
/**
 * Save bitmap to special folder --> Config.DIR_SAVE_PHOTO
 * Save bitmap outside UiThread using Coroutine
 * ReScan photo after saved
 * Optional default parameter: callback success & failure
 * @param context using rescan photo after saved
 * @param bmp Bitmap will be save to photo
 * @param success default parameter callback when saved success
 * @param failure default parameter callback when throw exception
 *
 * @howToUse saveBitmap(context!!, bmp)
 * @howToUse saveBitmap(context!!, bmp , {} , {})
 * @howToUse saveBitmap(context!!, bmp , {file ->} , {})
 * @howToUse saveBitmap(context!!, bmp , this::success , this:failure)
 *
 */
fun Any.saveBitmap(context: Context,
                   bmp: Bitmap,
                   start: () -> Unit = {},
                   success: (file: File) -> Unit = {},
                   failure: () -> Unit = {}) = try {

    Log.e("ThoNH", "saveBitmap -> bitmap(${bmp.width}, ${bmp.height})")
    start.invoke()
    async {

        val mediaStorageDir = File(Config.SAVE_IMAGE_DIR)
        val timeStamp = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(Date())
        val mediaFile = File(mediaStorageDir.absolutePath + File.separator + "Photo_$timeStamp.jpg")

        Log.e("ThoNH", mediaFile.absolutePath)

        if (!mediaFile.exists()) {
            mediaFile.parentFile.mkdirs()
        }
        if (!mediaFile.exists()) {
            mediaFile.createNewFile()
        }


        val fos = FileOutputStream(mediaFile)
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(mediaStorageDir)
            scanIntent.data = contentUri
            context.sendBroadcast(scanIntent)
        } else {
            val intent = Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getDataDirectory()))
            context.sendBroadcast(intent)
        }
        Log.e("ThoNH", "saveBitmap -> Image save successfully in: ${mediaFile.absolutePath}")
        success.invoke(mediaFile)

    }
} catch (ex: Exception) {
    ex.printStackTrace()
    failure.invoke()
}

/**
 * Save bitmap to cache dir
 * Call when u want transform bitmap to fragment or other activity
 */
fun Context.saveBitmapToTempCache(bmp: Bitmap, success: (file: File) -> Unit = {}, failure: () -> Unit) = try {
    val startTime = System.currentTimeMillis()

    val file = File("$cacheDir/Tempp.JPG")

    if (file.exists()){
        file.delete()
        file.createNewFile()
    }

    Log.e("ThoNH", "saveBitmapToTempCache -> path:${file.absolutePath}")
    val fos = FileOutputStream(file)
    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.flush()
    fos.close()
    Log.e("ThoNH", "saveBitmapToTempCache -> total time:${System.currentTimeMillis() - startTime}")
    success.invoke(file)

} catch (ex: Exception) {
    ex.printStackTrace()
    failure.invoke()
}


/**
 * Get bitmap from cache dir
 */
fun Context.getBitmapFromTempCache(start: () -> Unit, success: (bmp: Bitmap) -> Unit, failure: () -> Unit = {}) {
    decodeFile("$cacheDir/Tempp.JPG", start = start, success = success, failure = failure)
}


/*https://viblo.asia/p/caching-bitmaps-trong-android-N0bDM6x6v2X4*/
object MemoryCacheBitmap {
    private const val KEY = "MemoryCacheBitmap"
    lateinit var mLRUCache: LruCache<String, Bitmap>

    private fun createLRUCache(): LruCache<String, Bitmap> {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        return LruCache<String, Bitmap>(cacheSize)
    }

    /*Need call in subClass of Application.class*/
    /*Create singleton and pass to getBitmapFromCache() & addBitmapToCache()*/
    fun Application.initLRUCache() {
        async {
            mLRUCache = createLRUCache()
        }
    }

    /*get bitmap from memory cache -> maybe null if bitmap not corresponding key not exist*/
    fun getBitmapFromCache(): Bitmap? {
        return mLRUCache.get(KEY)
    }

    /*add bitmap to memory cache */
    fun addBitmapToCache(bmp: Bitmap) {
        if (getBitmapFromCache() != null) {
            mLRUCache.remove(KEY)
        }
        mLRUCache.put(KEY, bmp)
    }
}
