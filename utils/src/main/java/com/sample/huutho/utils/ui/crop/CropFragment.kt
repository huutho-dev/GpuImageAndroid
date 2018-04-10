package com.sample.huutho.utils.ui.crop

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.isseiaoki.simplecropview.CropImageView
import com.sample.huutho.utils.R
import com.sample.huutho.utils.getBitmapFromTempCache
import com.sample.huutho.utils.saveBitmapToTempCache
import com.sample.huutho.utils.ui.BaseFragment
import com.sample.huutho.utils.widget.IconView
import org.jetbrains.anko.forEachChild
import java.io.File

/**
 * Created by FRAMGIA\nguyen.huu.tho on 09/04/2018.
 */
class CropFragment : BaseFragment() {

    lateinit var mTitle: TextView
    lateinit var mCropImageView: CropImageView
    lateinit var mCropContainer: LinearLayout
    private val dataCrop = createCropData()
    private lateinit var mBitmap: Bitmap

    companion object {
        fun newInstance(): CropFragment {
            return CropFragment()
        }
    }

    /**********************************************************************************************/
    private var start = {}

    private var success = { bmp: Bitmap -> mBitmap = bmp ; mCropImageView.imageBitmap = mBitmap}

    private var failure = { onCancel() }
    /**********************************************************************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crop, container, false)

        mTitle = view.findViewById(R.id.control_bar_title)
        view.findViewById<ImageView>(R.id.control_bar_cancel).setOnClickListener { onCancel() }
        view.findViewById<ImageView>(R.id.control_bar_done).setOnClickListener { onSave() }

        mCropImageView = view.findViewById(R.id.mCropImageView)
        mCropContainer = view.findViewById(R.id.mCropContainer)

        dataCrop.forEach {
            val cropItem = it
            val iconView = IconView(context)
            iconView.setView(it.icon, it.title)
            iconView.setOnClickListener { onCropItemClick(cropItem, iconView) }
            mCropContainer.addView(iconView)
        }

        context?.getBitmapFromTempCache(start, success, failure)

        return view
    }


    private fun onCropItemClick(cropItem: CropItem, view: IconView) {
        mCropContainer.forEachChild { (it as IconView).setUnSelected() }
        view.setSelected()
        when (cropItem.cropType) {

            CROP_TYPE.FLIP_HORIZONTAL -> flipHorizontal()

            CROP_TYPE.FLIP_VERTICAL -> flipVertical()

            CROP_TYPE.ROTATE -> mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D)

            CROP_TYPE.FREE -> mCropImageView.setCropMode(CropImageView.CropMode.FREE)

            CROP_TYPE.ORIGIN -> mCropImageView.setCropMode(CropImageView.CropMode.FIT_IMAGE)

            CROP_TYPE.SIZE_1_1 -> mCropImageView.setCropMode(CropImageView.CropMode.SQUARE)

            CROP_TYPE.SIZE_3_4 -> mCropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4)

            CROP_TYPE.SIZE_4_3 -> mCropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3)

            CROP_TYPE.SIZE_16_9 -> mCropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9)

            CROP_TYPE.SIZE_9_16 -> mCropImageView.setCropMode(CropImageView.CropMode.RATIO_9_16)
        }
    }


    /*Flip bitmap horizontal*/
    private fun flipHorizontal() {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f, (mBitmap.getWidth() / 2).toFloat(), (mBitmap.getHeight() / 2).toFloat())
        val newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.width, mBitmap.height, matrix, true)

        mBitmap = Bitmap.createBitmap(newBitmap)
        mCropImageView.imageBitmap = mBitmap
        newBitmap.recycle()
    }

    private fun flipVertical() {
        val matrix = Matrix()
        matrix.postScale(1f, -1f, (mBitmap.width / 2).toFloat(), (mBitmap.height / 2).toFloat())
        val newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.width, mBitmap.height, matrix, true)

        mBitmap = Bitmap.createBitmap(newBitmap)
        mCropImageView.imageBitmap = mBitmap
        newBitmap.recycle()
    }


    private fun onCancel() {
        Toast.makeText(context, "onCancel", Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.popBackStack()
    }


    private fun onSave() {

        val saveSuccess = { _: File -> activity?.supportFragmentManager?.popBackStackImmediate().run {} }
        val saveFailure = {
            activity?.supportFragmentManager?.popBackStackImmediate()
            Toast.makeText(context, "Save failur", Toast.LENGTH_SHORT).show()
        }

        context?.saveBitmapToTempCache(
                mCropImageView.croppedBitmap,
                saveSuccess,
                saveFailure)
    }


    private fun createCropData(): MutableList<CropItem> {
        val datas = mutableListOf<CropItem>()
        datas.add(CropItem(R.drawable.icon_crop_menu_item_horizontal_mirror,R.string.crop_flip_horizontal, CROP_TYPE.FLIP_HORIZONTAL))
        datas.add(CropItem(R.drawable.icon_crop_menu_item_horizontal_mirror,R.string.crop_flip_vertical, CROP_TYPE.FLIP_VERTICAL))
        datas.add(CropItem(R.drawable.icon_crop_menu_item_rotate,R.string.crop_rotate, CROP_TYPE.ROTATE))
        datas.add(CropItem(R.drawable.icon_crop_menu_item_free,R.string.crop_free, CROP_TYPE.FREE))
        datas.add(CropItem(R.drawable.icon_crop_menu_item_origin,R.string.crop_fit_origin, CROP_TYPE.ORIGIN))
        datas.add(CropItem(R.drawable.icon_crop_menu_item_1_1,R.string.crop_ratio_1_1, CROP_TYPE.SIZE_1_1))
        datas.add(CropItem(R.drawable.icon_crop_menu_item_3_4,R.string.crop_ratio_3_4, CROP_TYPE.SIZE_3_4))
        datas.add(CropItem(R.drawable.icon_crop_menu_item_4_3,R.string.crop_ratio_4_3, CROP_TYPE.SIZE_4_3))
        datas.add(CropItem(R.drawable.icon_crop_menu_item_16_9,R.string.crop_ratio_16_9, CROP_TYPE.SIZE_16_9))
        datas.add(CropItem(R.drawable.icon_crop_menu_item_9_16,R.string.crop_ratio_9_16, CROP_TYPE.SIZE_9_16))
        return datas
    }

}