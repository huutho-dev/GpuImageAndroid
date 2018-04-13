package android.sticky.com.lib.event;

import android.sticky.com.lib.StickerGroup;
import android.sticky.com.lib.event.parent.StickerIconEvent;
import android.view.MotionEvent;
import android.widget.Toast;


/**
 * Created by ThoNh on 1/12/2018.
 */

public class BottomLeftIconEvent implements StickerIconEvent {
    @Override
    public void onActionDown(StickerGroup stickerGroup, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerGroup stickerGroup, MotionEvent event) {

    }

    @Override
    public void onActionUp(StickerGroup stickerGroup, MotionEvent event) {
        Toast.makeText(stickerGroup.getContext(), "Hello World!", Toast.LENGTH_SHORT).show();
    }
}
