package android.sticky.com.lib.event.flip;


import android.sticky.com.lib.Config;

/**
 * @author wupanjie
 */

public class FlipVerticallyEvent extends AbstractFlipEvent {

    @Override
    @Config.Flip
    protected int getFlipDirection() {
        return Config.Flip.FLIP_VERTICALLY;
    }
}
