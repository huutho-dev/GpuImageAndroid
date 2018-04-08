package android.gpuimage.com.Ui.adjust

import android.gpuimage.com.gpu.GPUImageFilterTools
import android.gpuimage.com.gpuimage.GPUImageFilter
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

data class AdjustItem(@DrawableRes val icon: Int, @StringRes val title: Int, var gpuImageFilter: GPUImageFilter) {

   var  filterAdjust : GPUImageFilterTools.FilterAdjuster = GPUImageFilterTools.FilterAdjuster(gpuImageFilter)

}