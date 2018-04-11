package android.gpuimage.com.Ui.filter

import com.google.gson.annotations.SerializedName

/**
 * Created by FRAMGIA\nguyen.huu.tho on 10/04/2018.
 */
data class FilterItem(@SerializedName("source") val source: String, @SerializedName("name") val name: String) {
    var isSelected = false
}