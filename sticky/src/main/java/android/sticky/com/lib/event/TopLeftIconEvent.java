package android.sticky.com.lib.event;

import android.sticky.com.lib.StickerGroup;
import android.sticky.com.lib.event.parent.StickerIconEvent;
import android.view.MotionEvent;


/**
 * @author wupanjie
 */

public class TopLeftIconEvent implements StickerIconEvent {
    @Override
    public void onActionDown(StickerGroup stickerGroup, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerGroup stickerGroup, MotionEvent event) {

    }

    @Override
    public void onActionUp(StickerGroup stickerGroup, MotionEvent event) {
        stickerGroup.removeCurrentSticker();
    }
}
