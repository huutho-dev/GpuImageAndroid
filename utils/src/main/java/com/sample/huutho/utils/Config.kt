package com.sample.huutho.utils

import android.os.Environment
import java.io.File

/**
 * Created by HuuTho on 4/1/2018.
 */
object Config {
    const val APPLICATION_NAME = "ColorSplash"
    const val APPLICATION_ID = "android.gpuimage.com.gpuimageandroid" // package_name_of_module_app
    var SAVE_IMAGE_DIR = "${Environment.getExternalStorageDirectory()}${File.separatorChar}$APPLICATION_NAME${File.separatorChar}"
}