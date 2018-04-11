package android.gpuimage.com.Ui.filter

import android.content.Context
import android.gpuimage.com.gpuimage.GPUImage
import android.gpuimage.com.gpuimage.GPUImageLookupFilter
import android.gpuimage.com.gpuimage.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

/**
 * Created by FRAMGIA\nguyen.huu.tho on 10/04/2018.
 */
class FilterAdapter(val context: Context?, private val onItemClick: (filterItem: FilterItem, view: View) -> Unit) : RecyclerView.Adapter<Holder>() {

    var filters = mutableListOf<FilterItem>()
        set(value) {
            filters.clear()
            notifyDataSetChanged()
            filters.addAll(value)
            notifyDataSetChanged()
        }

    var mBitmapThumb: Bitmap? = null

    override fun getItemCount(): Int = filters.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val filter = filters[position]

        if (filter.isSelected) {
            holder.mTextFilter.setBackgroundColor(context?.resources?.getColor(R.color.colorAccent)!!)
        } else {
            holder.mTextFilter.setBackgroundColor(context?.resources?.getColor(R.color.colorTextDarkSecondary)!!)
        }

        holder.mTextFilter.text = filter.name
        getBitmapFilter( holder.mTextFilter,holder.mImageFilter, filter.source)

        holder.itemView.setOnClickListener {

            findLastItemSelected()?.let {
                it.isSelected = false
                notifyItemChanged(filters.indexOf(it))
            }

            filter.isSelected = true
            notifyItemChanged(position)

            onItemClick.invoke(filter, holder.itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.item_filter, parent, false))
    }

    private fun getBitmapFilter(textView: TextView,imageView: ImageView, sourcePathFilter: String) {

        textView.alpha = 0f
        imageView.alpha = 0f

        async(UI) {
            val bitmap = async(CommonPool) {
                val lookup = GPUImageLookupFilter()
                lookup.bitmap = BitmapFactory.decodeStream(this@FilterAdapter.context?.assets?.open(sourcePathFilter))

                val mGpuImage = GPUImage(this@FilterAdapter.context)
                mGpuImage.setImage(mBitmapThumb)
                mGpuImage.setFilter(lookup)
                mGpuImage.bitmapWithFilterApplied
            }
            imageView.setImageBitmap(bitmap.await())
            imageView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .setStartDelay(100)

            textView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .setStartDelay(100)
        }
    }

    fun findLastItemSelected(): FilterItem? {
        val selecteds = filters.filterIndexed { _, filterItem -> filterItem.isSelected }
        if (selecteds.isNotEmpty()) {
            return selecteds.get(0)
        }
        return null
    }

}

class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mImageFilter = itemView.findViewById<AppCompatImageView>(R.id.image_filter)
    val mTextFilter = itemView.findViewById<TextView>(R.id.text_filter)
}