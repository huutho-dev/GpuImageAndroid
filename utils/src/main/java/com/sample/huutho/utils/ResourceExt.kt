package android.utils.nht.superutils.utils

import android.content.Context
import android.content.res.Resources
import android.view.Surface
import org.jetbrains.anko.windowManager

/**
 * Created by HuuTho on 3/23/2018.
 */

fun Any.getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels

fun Any.getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

fun Any.getScreenDensity() = Resources.getSystem().displayMetrics.density

fun Context.getScreenOrientation()
        = when (windowManager.defaultDisplay.rotation) {
    Surface.ROTATION_0 -> 0
    Surface.ROTATION_90 -> 90
    Surface.ROTATION_180 -> 180
    Surface.ROTATION_270 -> 270
    else -> 0
}
