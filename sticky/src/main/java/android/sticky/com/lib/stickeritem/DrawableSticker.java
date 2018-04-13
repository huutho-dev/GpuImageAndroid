package android.sticky.com.lib.stickeritem;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * @author wupanjie
 */
public class DrawableSticker extends Sticker {

    private Drawable mDrawable;
    private Rect mRealBounds;

    public DrawableSticker(Drawable drawable) {
        this.mDrawable = drawable;
        mRealBounds = new Rect(0, 0, getWidth(), getHeight());
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return mDrawable;
    }

    @Override
    public DrawableSticker setDrawable(@NonNull Drawable drawable) {
        this.mDrawable = drawable;
        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.concat(getMatrix());
        mDrawable.setBounds(mRealBounds);
        mDrawable.draw(canvas);
        canvas.restore();
    }

    @NonNull
    @Override
    public DrawableSticker setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mDrawable.setAlpha(alpha);
        return this;
    }

    @Override
    public int getWidth() {
        return mDrawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return mDrawable.getIntrinsicHeight();
    }

    @Override
    public void release() {
        super.release();
        if (mDrawable != null) {
            mDrawable = null;
        }
    }
}
