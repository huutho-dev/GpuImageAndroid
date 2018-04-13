package android.sticky.com.lib.stickeritem;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.sticky.com.lib.Config;
import android.sticky.com.lib.StickerGroup;
import android.sticky.com.lib.event.parent.StickerIconEvent;
import android.view.MotionEvent;


/**
 * @author wupanjie
 */
public class IconMenuOption extends DrawableSticker implements StickerIconEvent {

    private StickerIconEvent mIconEventListener;

    private float x;
    private float y;
    private float mIconRadius = Config.DEFAULT_ICON_MENU_RADIUS;

    @Config.Gravity
    private int position;


    public IconMenuOption(Drawable drawable, @Config.Gravity int gravity) {
        super(drawable);
        this.position = gravity;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(x, y, mIconRadius, paint);
        super.draw(canvas);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getIconRadius() {
        return mIconRadius;
    }

    public void setIconRadius(float iconRadius) {
        this.mIconRadius = iconRadius;
    }

    @Override
    public void onActionDown(StickerGroup stickerGroup, MotionEvent event) {
        if (mIconEventListener != null) {
            mIconEventListener.onActionDown(stickerGroup, event);
        }
    }

    @Override
    public void onActionMove(StickerGroup stickerGroup, MotionEvent event) {
        if (mIconEventListener != null) {
            mIconEventListener.onActionMove(stickerGroup, event);
        }
    }

    @Override
    public void onActionUp(StickerGroup stickerGroup, MotionEvent event) {
        if (mIconEventListener != null) {
            mIconEventListener.onActionUp(stickerGroup, event);
        }
    }

    public StickerIconEvent getIconEventListener() {
        return mIconEventListener;
    }

    public void setIconEventListener(StickerIconEvent mIconEvent) {
        this.mIconEventListener = mIconEvent;
    }

    @Config.Gravity
    public int getPosition() {
        return position;
    }

    public void setPosition(@Config.Gravity int position) {
        this.position = position;
    }
}
