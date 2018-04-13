package android.gpuimage.com.Ui.filter

import android.gpuimage.com.gpuimage.GPUImageLookupFilter
import android.gpuimage.com.gpuimage.GPUImageView
import android.gpuimage.com.gpuimage.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sample.huutho.utils.getBitmapFromTempCache
import com.sample.huutho.utils.saveBitmapToTempCache
import com.sample.huutho.utils.ui.BaseFragment
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.support.v4.dip
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class FilterFragment : BaseFragment() {

    companion object {
        fun newInstance() = FilterFragment()
    }


    lateinit var mTitle: TextView
    lateinit var mSeekBar: SeekBar
    lateinit var mGpuImage: GPUImageView
    lateinit var mRecyclerView: RecyclerView
    lateinit var mCategoryContainer: LinearLayout
    lateinit var mFilterAdapter: FilterAdapter
    var mGpuLookUpFilter = GPUImageLookupFilter()


    /**********************************************************************************************/

    private val start = {}

    private val success = { bmp: Bitmap ->
        mGpuImage.setImage(bmp)
        mGpuImage.setBackgroundColor(0f, 0f, 0f, 0f)
        mGpuImage.requestRender()

        val bmpThumb = Bitmap.createScaledBitmap(bmp, dip(48), dip(48), false)
        mFilterAdapter.mBitmapThumb = bmpThumb
    }

    private val failure = { Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show() }

    /**********************************************************************************************/


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_filter, container, false)

        mTitle = view.findViewById(R.id.control_bar_title)
        mSeekBar = view.findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
        mGpuImage = view.findViewById(R.id.gpu_image_view)
        mRecyclerView = view.findViewById(R.id.recycler_view)
        mCategoryContainer = view.findViewById(R.id.filter_category_container)

        view.findViewById<ImageView>(R.id.control_bar_cancel).setOnClickListener { onCancel() }
        view.findViewById<ImageView>(R.id.control_bar_done).setOnClickListener { onSave() }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFilterAdapter = FilterAdapter(context, onItemFilterSelected)
        mRecyclerView.adapter = mFilterAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        initData()
    }


    private fun onSave() {
        val saveSuccess = { _: File -> activity?.supportFragmentManager?.popBackStackImmediate().run {} }

        val saveFailure = {
            activity?.supportFragmentManager?.popBackStackImmediate()
            Toast.makeText(context, "Save failur", Toast.LENGTH_SHORT).show()
        }

        context?.saveBitmapToTempCache(
                mGpuImage.capture(),
                saveSuccess,
                saveFailure)
    }

    private fun onCancel() {
        Toast.makeText(context, "onCancel", Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.popBackStack()
    }


    private fun initData() {
        context?.getBitmapFromTempCache(start, success, failure)
        async(UI) { addGroupCategory(getFilterAssets().await()) }
    }


    /**
     * Get Filter from asset folder
     * Load with async coroutine of Kotlin
     */
    suspend fun getFilterAssets(): Deferred<MutableList<FilterGroup>> {
        return async<MutableList<FilterGroup>>(CommonPool) {
            Gson().fromJson<MutableList<FilterGroup>>(
                    BufferedReader(InputStreamReader(this@FilterFragment.context?.assets?.open("filters/filters.json"))),
                    object : TypeToken<List<FilterGroup>>() {
                    }.type)
        }
    }

    private fun addGroupCategory(filterGroups: MutableList<FilterGroup>) {
        filterGroups.forEach {
            val filterGroup = it
            val textView = TextView(context)
            textView.apply {
                this.text = it.title
                this.setTextColor(resources.getColor(R.color.colorTextDarkSecondary))
                this.setPadding(dip(16), 0, dip(16), 0)
                this.setOnClickListener { onCategorySelected(filterGroup, textView) }
            }

            mCategoryContainer.addView(textView)
        }
    }


    private fun onCategorySelected(filterGroup: FilterGroup, textView: TextView) {
        mCategoryContainer.forEachChild { (it as TextView).setTextColor(resources.getColor(R.color.colorTextDarkSecondary)) }
        textView.setTextColor(resources.getColor(R.color.colorAccent))
        mFilterAdapter.findLastItemSelected()?.let {
            it.isSelected = false
        }
        mFilterAdapter.filters = filterGroup.items
        mRecyclerView.scrollToPosition(0)
    }

    /**
     * RecyclerView item click listener
     */
    private val onItemFilterSelected = { filter: FilterItem, view: View ->
        val bmpFilter = BitmapFactory.decodeStream(context?.assets?.open(filter.source))
        mGpuLookUpFilter.bitmap = bmpFilter
        mGpuImage.filter = mGpuLookUpFilter
        mGpuImage.requestRender()

        mSeekBar.progress = 100
    }

    private val onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser){
                mGpuLookUpFilter.setIntensity(progress / 100f)
                mGpuImage.requestRender()
            }

        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }

}