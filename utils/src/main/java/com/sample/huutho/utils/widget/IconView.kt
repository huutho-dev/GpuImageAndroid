package com.sample.huutho.utils.widget

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.sample.huutho.utils.R
import kotlinx.android.synthetic.main.view_icon.view.*

class IconView : RelativeLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val view = View.inflate(context, R.layout.view_icon, null)
        addView(view)
    }

    fun setView(@DrawableRes idIcon: Int, @StringRes idName: Int) {
        icon.setImageResource(idIcon)
        text.setText(idName)
    }

    fun setSelected() {
        icon.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN)
        text.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
    }

    fun setUnSelected() {
        icon.setColorFilter(ContextCompat.getColor(context, R.color.colorDarkIconActive), android.graphics.PorterDuff.Mode.MULTIPLY)
        text.setTextColor(ContextCompat.getColor(context, R.color.colorDarkIconActive))
    }
}