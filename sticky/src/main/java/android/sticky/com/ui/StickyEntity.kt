package android.sticky.com.ui

import com.google.gson.annotations.SerializedName

/**
 * Created by FRAMGIA\nguyen.huu.tho on 13/04/2018.
 */
data class StickyEntity(
        @SerializedName("version") val version: Int,
        @SerializedName("categories") val categories: MutableList<CategoriesBean>
)


data class CategoriesBean(
        @SerializedName("name") val name: String,
        @SerializedName("id") val id: Int,
        @SerializedName("items") val items: MutableList<ItemsBean>
)


data class ItemsBean(
        @SerializedName("tiny") val tiny: String,
        @SerializedName("main") val main: String,
        @SerializedName("id") val id: String
)