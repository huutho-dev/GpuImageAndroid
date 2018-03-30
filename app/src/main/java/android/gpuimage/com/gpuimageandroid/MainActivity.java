package android.gpuimage.com.gpuimageandroid;

import android.gpuimage.com.gpuimage.GPUImageGaussianBlurFilter;
import android.gpuimage.com.gpuimage.GPUImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        GPUImageView gpuimage_view = findViewById(R.id.gpuimage_view);
        gpuimage_view.setImage(bmp);
        gpuimage_view.setFilter(new GPUImageGaussianBlurFilter(5.0f));
        gpuimage_view.requestRender();

    }
}
