package com.sample.huutho.utils.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.isseiaoki.simplecropview.CropImageView
import com.sample.huutho.utils.R

/**
 * Created by FRAMGIA\nguyen.huu.tho on 09/04/2018.
 */
class CropFragment : BaseFragment() {

    lateinit var mTitle: TextView
    lateinit var mCropImageView: CropImageView
    lateinit var mCropContainer: LinearLayout

    companion object {
        fun newInstance(): CropFragment {
            return CropFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crop, container, false)
        mTitle = view.findViewById(R.id.control_bar_title)
        mCropImageView = view.findViewById(R.id.mCropImageView)
        mCropContainer = view.findViewById(R.id.crop_container)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}