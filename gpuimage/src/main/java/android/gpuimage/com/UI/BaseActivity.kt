package android.gpuimage.com.UI

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity


/**
 * Created by ThoNh on 3/30/2018.
 */
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val REQ_CODE_PICK_IMAGE_GALLERY = 0x1
        const val REQ_CODE_CAPTURE_IMAGE_CAMERA = 0x2
    }


    abstract fun newInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    /**
     * Support for onBackPressed() in fragment
     */
    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments
        var handled = false
        for (f in fragmentList) {
            if (f is BaseFragment) {
                handled = f.onBackPressed()
                if (handled) {
                    break
                }
            }
        }

        if (!handled) {
            super.onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                REQ_CODE_PICK_IMAGE_GALLERY -> {
                    var bmp: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                    (getVisibleFragment() as BaseFragment).setBitmap(bmp)
                }

                REQ_CODE_CAPTURE_IMAGE_CAMERA -> {
                    var bmp: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                    (getVisibleFragment() as BaseFragment).setBitmap(bmp)
                }

            }
        }

    }


    /**
     * Get current fragment sho
     */
    private fun getVisibleFragment(): Fragment? {
        val fragments = supportFragmentManager.fragments
        if (fragments != null) {
            fragments
                    .filter { it != null && it.isVisible }
                    .forEach { return it }
        }
        return null
    }


    fun getImageInGallery() {

    }


    fun takeImageFromCamera() {

    }


    fun getBitmapFromCache() {

    }
}