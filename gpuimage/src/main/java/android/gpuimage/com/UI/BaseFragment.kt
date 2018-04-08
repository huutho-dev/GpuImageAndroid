package android.gpuimage.com.UI

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by ThoNh on 3/30/2018.
 */

abstract class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    fun onBackPressed(): Boolean {
        return false
    }

    /**
     * Get bitmap from activity pass to fragment
     * @param bmp Bitmap can be obtained from cache, resource, file ....
     */
    fun setBitmap(bmp: Bitmap) {
    }


    fun getCurrentActivity() = activity as BaseActivity


    fun getImageInGallery() = getCurrentActivity().getImageInGallery()


    fun takeImageFromCamera() = getCurrentActivity().takeImageFromCamera()


}
