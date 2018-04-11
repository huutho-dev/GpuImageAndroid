package com.sample.huutho.utils.ui.crop

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

/**
 * Created by FRAMGIA\nguyen.huu.tho on 10/04/2018.
 */

object CROP_TYPE {
    const val FLIP_HORIZONTAL = 0x0
    const val FLIP_VERTICAL = 0x1
    const val ROTATE = 0x2
    const val FREE = 0x3
    const val ORIGIN = 0x4
    const val SIZE_1_1 = 0x5
    const val SIZE_3_4 = 0x6
    const val SIZE_4_3 = 0x7
    const val SIZE_16_9 = 0x8
    const val SIZE_9_16 = 0x9
}

data class CropItem(@DrawableRes val icon: Int, @StringRes val title: Int, val cropType: Int)