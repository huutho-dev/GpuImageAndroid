package android.sticky.com.lib.event;

import android.sticky.com.lib.StickerGroup;
import android.sticky.com.lib.event.parent.StickerIconEvent;
import android.view.MotionEvent;


/**
 * @author wupanjie
 */

public class BottomRightIconEvent implements StickerIconEvent {
    @Override
    public void onActionDown(StickerGroup stickerGroup, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerGroup stickerGroup, MotionEvent event) {
        stickerGroup.zoomAndRotateCurrentSticker(event);
    }

    @Override
    public void onActionUp(StickerGroup stickerGroup, MotionEvent event) {
        if (stickerGroup.getOnStickerOperationListener() != null) {
            stickerGroup.getOnStickerOperationListener()
                    .onStickerZoomFinished(stickerGroup.getCurrentSticker());
    }
    }
}
