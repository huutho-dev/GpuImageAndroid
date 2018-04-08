package android.gpuimage.com.gpuimageandroid

import android.gpuimage.com.Ui.adjust.AdjustFragment
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sample.huutho.utils.decodeResource
import com.sample.huutho.utils.saveBitmapToTempCache

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        decodeResource(
                this,
                R.drawable.girl,
                start = {},
                success = { bmp: Bitmap ->
                    saveBitmapToTempCache(
                            bmp,
                            { supportFragmentManager.beginTransaction().add(R.id.root, AdjustFragment.newInstance()).commit() },
                            {})
                },
                failure = {})


    }
}
