package android.gpuimage.com.gpuimageandroid

import android.gpuimage.com.Ui.filter.FilterFragment
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.sample.huutho.utils.decodeResource
import com.sample.huutho.utils.saveBitmapToTempCache

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)

        decodeResource(
                this,
                R.drawable.girl,
                start = {},
                success = { bmp: Bitmap ->
                    saveBitmapToTempCache(
                            bmp,
                            { supportFragmentManager
                                    .beginTransaction()
                                    .addToBackStack("AdjustFragment")
                                    .add(R.id.root, FilterFragment.newInstance())
                                    .commit()},
                            {})
                },
                failure = {})
    }
}
