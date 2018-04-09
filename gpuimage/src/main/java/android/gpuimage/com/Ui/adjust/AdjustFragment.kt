package android.gpuimage.com.Ui.adjust

import android.gpuimage.com.gpuimage.*
import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.sample.huutho.utils.App
import com.sample.huutho.utils.getBitmapFromTempCache
import com.sample.huutho.utils.saveBitmapToTempCache
import com.sample.huutho.utils.ui.BaseFragment
import com.sample.huutho.utils.widget.IconView
import kotlinx.android.synthetic.main.fragment_adjuts.*
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.info
import java.io.File

class AdjustFragment : BaseFragment() {

    companion object {
        fun newInstance(): AdjustFragment {
            return AdjustFragment()
        }
    }

    private lateinit var mTitle: TextView

    private lateinit var mSeekBar : SeekBar

    private var filterGroup: GPUImageFilterGroup = GPUImageFilterGroup()

    private var mAdjustData: MutableList<AdjustItem> = createTmpDataTool()

    private lateinit var mCurrentAdjustTool: AdjustItem


    /**********************************************************************************************/
    private var startLoadBmp = { info { "startLoadBmp()" } }

    private var loadBmpSuccess = { bmp: Bitmap ->
        gpu_image_view.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        gpu_image_view.setBackgroundColor(0f, 0f, 0f, 0f)
        gpu_image_view.setImage(bmp)
        gpu_image_view.filter = filterGroup
        gpu_image_view.requestRender()

    }

    private var loadBmpFailure = { info { "loadBmpFailure()" } }

    /**********************************************************************************************/


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_adjuts, container, false)
        mTitle = view.findViewById(R.id.control_bar_title)
        mSeekBar = view.findViewById(R.id.seek_bar)
        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangedListener)
        view.findViewById<ImageView>(R.id.control_bar_cancel).setOnClickListener { onCancel() }
        view.findViewById<ImageView>(R.id.control_bar_done).setOnClickListener { onSave() }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdjustData.forEach {
            filterGroup.addFilter(it.gpuImageFilter)
            val adjustItem = it
            val iconView = IconView(this@AdjustFragment.context)
            iconView.setView(adjustItem.icon, adjustItem.title)
            iconView.setOnClickListener { onAdjustToolClicked(adjustItem, iconView) }
            adjust_tool_container.addView(iconView)
        }
        adjust_tool_container.getChildAt(0).performClick()

        App.getInstance().getBitmapFromTempCache(startLoadBmp, loadBmpSuccess, loadBmpFailure)

        reset.setOnClickListener {
            mAdjustData.forEach {
                it.currentValue = it.defaultValue
                it.filterAdjust.adjust(it.currentValue)
                mSeekBar.progress = mCurrentAdjustTool.defaultValue
                gpu_image_view.requestRender()
            }
        }
    }


    private fun onAdjustToolClicked(adjustItem: AdjustItem, iconView: IconView) {
        adjust_tool_container.forEachChild { (it as IconView).setUnSelected() }
        iconView.setSelected()
        mTitle.setText(adjustItem.title)
        mCurrentAdjustTool = adjustItem
        mSeekBar.progress = mCurrentAdjustTool.currentValue
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
                gpu_image_view.capture(),
                saveSuccess,
                saveFailure)
    }


    private fun createTmpDataTool(): MutableList<AdjustItem> {
        val datas = mutableListOf<AdjustItem>()
        datas.add(AdjustItem(R.drawable.icon_adjust_brightness, R.string.adjust_brightness, GPUImageBrightnessFilter()))
        datas.add(AdjustItem(R.drawable.icon_adjust_sharpen, R.string.adjust_sharpness, GPUImageSharpenFilter()))
        datas.add(AdjustItem(R.drawable.icon_adjust_contrast, R.string.adjust_contrast, GPUImageContrastFilter()))
        datas.add(AdjustItem(R.drawable.icon_adjust_saturation, R.string.adjust_saturation, GPUImageSaturationFilter()))
        datas.add(AdjustItem(R.drawable.icon_adjust_vignette, R.string.adjust_vignette, GPUImageVignetteFilter(PointF(0.5f, 0.5f), floatArrayOf(0.0f, 0.0f, 0.0f), 0f, 1f), defaultValue = 100))
        datas.add(AdjustItem(R.drawable.ic_check, R.string.adjust_white_balance, GPUImageWhiteBalanceFilter()))
        return datas
    }


    private var onSeekBarChangedListener : SeekBar.OnSeekBarChangeListener = object: SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                mCurrentAdjustTool.filterAdjust.adjust(progress)
                mCurrentAdjustTool.currentValue = progress
                gpu_image_view.requestRender()
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }

}