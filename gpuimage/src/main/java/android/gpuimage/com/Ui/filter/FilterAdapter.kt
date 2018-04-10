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
import android.widget.TextView

/**
 * Created by FRAMGIA\nguyen.huu.tho on 10/04/2018.
 */
class FilterAdapter(val context: Context?, val onItemClick: (filterItem: FilterItem, view: View) -> Unit) : RecyclerView.Adapter<Holder>() {

    var filters = mutableListOf<FilterItem>()
        set(value) {
            filters.clear()
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
        holder.mImageFilter.setImageBitmap(getBitmapFilter(filter.source))

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

    private fun getBitmapFilter(sourcePathFilter: String): Bitmap {
        val lookup = GPUImageLookupFilter()
        lookup.bitmap = BitmapFactory.decodeStream(context?.assets?.open(sourcePathFilter))

        val mGpuImage = GPUImage(context)
        mGpuImage.setImage(mBitmapThumb)
        mGpuImage.setFilter(lookup)
        return mGpuImage.bitmapWithFilterApplied
    }

    private fun findLastItemSelected(): FilterItem? {
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