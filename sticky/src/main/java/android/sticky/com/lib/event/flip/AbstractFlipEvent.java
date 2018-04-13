package android.sticky.com.lib.event.flip;

import android.sticky.com.lib.Config;
import android.sticky.com.lib.StickerGroup;
import android.sticky.com.lib.event.parent.StickerIconEvent;
import android.view.MotionEvent;



public abstract class AbstractFlipEvent implements StickerIconEvent {

    @Override
    public void onActionDown(StickerGroup stickerGroup, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerGroup stickerGroup, MotionEvent event) {

    }

    @Override
    public void onActionUp(StickerGroup stickerGroup, MotionEvent event) {
        stickerGroup.flipCurrentSticker(getFlipDirection());
    }

    @Config.Flip
    protected abstract int getFlipDirection();
}
