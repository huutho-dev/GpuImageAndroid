package android.gpuimage.com.Ui.filter

import com.google.gson.annotations.SerializedName

/**
 * Created by FRAMGIA\nguyen.huu.tho on 10/04/2018.
 */
data class FilterGroup(
        @SerializedName("title")  val title: String,
        @SerializedName("order")  val order: Int,
        @SerializedName("items")  val items: MutableList<FilterItem> = mutableListOf()) {
}