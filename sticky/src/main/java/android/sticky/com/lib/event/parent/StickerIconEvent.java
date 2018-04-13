package android.sticky.com.lib.event.parent;

import android.sticky.com.lib.StickerGroup;
import android.view.MotionEvent;


/**
 * @author wupanjie
 */

public interface StickerIconEvent {

  void onActionDown(StickerGroup stickerGroup, MotionEvent event);

  void onActionMove(StickerGroup stickerGroup, MotionEvent event);

  void onActionUp(StickerGroup stickerGroup, MotionEvent event);

}
