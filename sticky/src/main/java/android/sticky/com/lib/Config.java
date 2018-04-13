package android.sticky.com.lib;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ThoNh on 1/12/2018.
 */

public class Config {

    public static final int DEFAULT_MIN_CLICK_DELAY_TIME = 200;
    public static final float DEFAULT_ICON_MENU_RADIUS = 30f;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ActionMode.NONE, ActionMode.DRAG, ActionMode.ZOOM_WITH_TWO_FINGER, ActionMode.ICON, ActionMode.CLICK})
    public @interface ActionMode {
        public static final int NONE = 0;
        public static final int DRAG = 1;
        public static final int ZOOM_WITH_TWO_FINGER = 2;
        public static final int ICON = 3;
        public static final int CLICK = 4;
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {Flip.FLIP_HORIZONTALLY, Flip.FLIP_VERTICALLY})
    public @interface Flip {
        public static final int FLIP_HORIZONTALLY = 1;
        public static final int FLIP_VERTICALLY = 1 << 1;
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {Position.CENTER, Position.TOP, Position.BOTTOM, Position.LEFT, Position.RIGHT})
    public @interface Position {
        public static final int CENTER = 1;
        public static final int TOP = 1 << 1;
        public static final int LEFT = 1 << 2;
        public static final int RIGHT = 1 << 3;
        public static final int BOTTOM = 1 << 4;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ Gravity.LEFT_TOP, Gravity.RIGHT_TOP, Gravity.LEFT_BOTTOM, Gravity.RIGHT_BOTOM })
    public @interface Gravity {
        public static final int LEFT_TOP = 0;
        public static final int RIGHT_TOP = 1;
        public static final int LEFT_BOTTOM = 2;
        public static final int RIGHT_BOTOM = 3;
    }


}
