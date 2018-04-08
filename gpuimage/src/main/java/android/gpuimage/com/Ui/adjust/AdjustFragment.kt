package android.gpuimage.com.Ui.adjust

import android.gpuimage.com.gpuimage.*
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.sample.huutho.utils.App
import com.sample.huutho.utils.getBitmapFromTempCache
import com.sample.huutho.utils.ui.BaseFragment
import com.sample.huutho.utils.widget.IconView
import kotlinx.android.synthetic.main.fragment_adjuts.*
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.info

class AdjustFragment : BaseFragment() {

    companion object {
        fun newInstance(): AdjustFragment {
            val fragment = AdjustFragment()
            return fragment
        }
    }

    private lateinit var mBitmap: Bitmap

    private var filterGroup: MutableList<GPUImageFilter> = mutableListOf()

    private var mAdjustDatas: MutableList<AdjustItem> = createTmpDataTool()

    private lateinit var mCurrentAdjustTool: AdjustItem

    private var startLoadBmp = { info { "startLoadBmp()" } }

    private var loadBmpSuccess = { bmp: Bitmap ->
        mBitmap = Bitmap.createBitmap(bmp)
        Log.e("ThoNH","size bmp:" + bmp.width + " - " + bmp.height)
        Log.e("ThoNH","size image:" + gpu_image_view.width + " - " + gpu_image_view.height)

        Handler().postDelayed({
            gpu_image_view.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
            gpu_image_view.setBackgroundColor(0f, 0f, 0f, 0f)
            gpu_image_view.setImage(mBitmap)
            gpu_image_view.filter = android.gpuimage.com.gpuimage.GPUImageFilterGroup(filterGroup)
            gpu_image_view.requestRender()
        },2000)

        gpu_image_view.requestRender()
    }

    private var loadBmpFailure = { info { "loadBmpFailure()" } }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_adjuts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mAdjustDatas.forEach {

            filterGroup.add(it.gpuImageFilter)

            val adjustItem = it
            val iconView = IconView(this@AdjustFragment.context)
            iconView.setView(adjustItem.icon, adjustItem.title)
            iconView.setOnClickListener { onAdjustToolClicked(adjustItem, iconView) }
            adjust_tool_container.addView(iconView)
        }

        App.getInstance().getBitmapFromTempCache(startLoadBmp, loadBmpSuccess, loadBmpFailure)


        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mCurrentAdjustTool.filterAdjust.adjust(progress)
                    gpu_image_view.requestRender()
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    fun onAdjustToolClicked(adjustItem: AdjustItem, iconView: IconView) {
        // selected item click
        adjust_tool_container.forEachChild { (it as IconView).setUnSelected() }
        iconView.setSelected()

        mCurrentAdjustTool = adjustItem
    }

    fun createTmpDataTool(): MutableList<AdjustItem> {
        val datas = mutableListOf<AdjustItem>()

        val item = AdjustItem(R.drawable.ic_check, R.string.app_name, GPUImageSharpenFilter())
        val item2 = AdjustItem(R.drawable.ic_check, R.string.app_name, GPUImageContrastFilter())
        val item3 = AdjustItem(R.drawable.ic_check, R.string.app_name, GPUImageGammaFilter())
        val item5 = AdjustItem(R.drawable.ic_check, R.string.app_name, GPUImageEmbossFilter())
        val item7 = AdjustItem(R.drawable.ic_check, R.string.app_name, GPUImageHueFilter())

        datas.add(item)
        datas.add(item2)
        datas.add(item3)
        datas.add(item5)
        datas.add(item7)

        return datas
    }

}