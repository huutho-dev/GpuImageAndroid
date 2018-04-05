package com.sample.huutho.utils

import android.os.Environment
import java.io.File

/**
 * Created by HuuTho on 4/1/2018.
 */
object Config {
    const val APP_NAME = "ColorSplash"
    var SAVE_IMAGE_DIR = "${Environment.getExternalStorageDirectory()}${File.separatorChar}$APP_NAME${File.separatorChar}"
}