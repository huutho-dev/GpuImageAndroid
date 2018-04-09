package android.gpuimage.com.Ui.filter

import android.gpuimage.com.gpuimage.GPUImageView
import android.gpuimage.com.gpuimage.R
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.sample.huutho.utils.saveBitmapToTempCache
import com.sample.huutho.utils.ui.BaseFragment
import java.io.File

class FilterFragment : BaseFragment() {

    lateinit var mTitle: TextView
    lateinit var mSeekBar: SeekBar
    lateinit var mGpuImage: GPUImageView
    lateinit var mRecyclerView: RecyclerView
    lateinit var mCategoryContainer: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_filter, container, false)
        mTitle = view.findViewById(R.id.control_bar_title)
        mSeekBar = view.findViewById(R.id.seek_bar)
        mGpuImage = view.findViewById(R.id.gpu_image_view)
        mRecyclerView = view.findViewById(R.id.recycler_view)
        mCategoryContainer = view.findViewById(R.id.filter_category_container)
        view.findViewById<ImageView>(R.id.control_bar_cancel).setOnClickListener { onCancel() }
        view.findViewById<ImageView>(R.id.control_bar_done).setOnClickListener { onSave() }
        return super.onCreateView(inflater, container, savedInstanceState)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addFilterCategory()
    }

    private fun addFilterCategory() {

    }

    private fun createTempDataFilter() {

    }

}