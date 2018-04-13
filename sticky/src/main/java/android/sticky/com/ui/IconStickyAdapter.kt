package android.sticky.com.ui

import android.net.Uri
import android.sticky.com.R
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Created by FRAMGIA\nguyen.huu.tho on 13/04/2018.
 */
class IconStickyAdapter (var listener: IOnStickyEventListener) : RecyclerView.Adapter<IconStickyAdapter.ViewHolder>() {

    var mStickies: MutableList<ItemsBean> = mutableListOf()
        set(value) {
            mStickies.clear()
            mStickies.addAll(value)
            notifyDataSetChanged()
        }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sticky = mStickies[position]

        Glide.with(holder.itemView.context).load(Uri.parse("file:///android_asset/stickers/${sticky.main}")).into(holder.mStickerIcon)

        holder.itemView.setOnClickListener {
            listener.onStickySelected(holder.itemView, sticky, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sticky, parent, false))

    override fun getItemCount(): Int = mStickies.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mStickerIcon: ImageView = itemView.findViewById(R.id.mStickerIcon)
    }

    interface IOnStickyEventListener {
        fun onStickySelected(view: View, sticky: ItemsBean, position: Int)
    }
}