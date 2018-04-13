package android.sticky.com.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.sticky.com.R;
import android.sticky.com.lib.event.BottomRightIconEvent;
import android.sticky.com.lib.event.TopLeftIconEvent;
import android.sticky.com.lib.event.parent.OnStickerOperationListener;
import android.sticky.com.lib.stickeritem.IconMenuOption;
import android.sticky.com.lib.stickeritem.Sticker;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Sticker View
 *
 * @author wupanjie
 *         Edited by ThoNH
 */
public class StickerGroup extends FrameLayout {
    private static final String TAG = StickerGroup.class.getSimpleName();

    private boolean isShowIcons;
    private boolean isShowBorder;
    private final boolean isBringToFrontCurrentSticker;
    private boolean isLocked;
    private boolean isConstrained;

    private final List<Sticker> mArraySticker = new ArrayList<>();
    private final List<IconMenuOption> mOptionIcons = new ArrayList<>(4);

    private final Paint mBorderPaint = new Paint();
    private final RectF mStickerViewRect = new RectF();

    private final Matrix sizeMatrix = new Matrix();
    private final Matrix downMatrix = new Matrix();
    private final Matrix moveMatrix = new Matrix();

    // region storing variables
    private final float[] bitmapPoints = new float[8];
    private final float[] bounds = new float[8];
    private final float[] point = new float[2];
    private final PointF currentCenterPoint = new PointF();
    private final float[] tmp = new float[2];
    private PointF midPoint = new PointF();
    // endregion
    private final int mTouchSlop;

    private IconMenuOption currentIcon;
    //the first point down position
    private float downX;
    private float downY;

    private float oldDistance = 0f;
    private float oldRotation = 0f;

    @Config.ActionMode
    private int mCurrentMode = Config.ActionMode.NONE;


    private long mLastClickTime = 0;
    private int mMinClickDelayTime = Config.DEFAULT_MIN_CLICK_DELAY_TIME;

    private Sticker mHandlingSticker;

    private OnStickerOperationListener onStickerOperationListener;

    public StickerGroup(Context context) {
        this(context, null);
    }

    public StickerGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.StickerGroup);
            isShowIcons = a.getBoolean(R.styleable.StickerGroup_showIcons, false);
            isShowBorder = a.getBoolean(R.styleable.StickerGroup_showBorder, false);
            isBringToFrontCurrentSticker = a.getBoolean(R.styleable.StickerGroup_bringToFrontCurrentSticker, false);

            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(a.getColor(R.styleable.StickerGroup_borderColor, Color.BLACK));
            mBorderPaint.setAlpha(a.getInteger(R.styleable.StickerGroup_borderAlpha, 128));

            configDefaultIcons();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    public void configDefaultIcons() {
        mOptionIcons.clear();

        // Add top-left Icon & set Event
        Drawable mTopLeftDrawable = ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_close_white_18dp);
        IconMenuOption mTopLeftIcon = new IconMenuOption(mTopLeftDrawable, Config.Gravity.LEFT_TOP);
        mTopLeftIcon.setIconEventListener(new TopLeftIconEvent());
        mOptionIcons.add(mTopLeftIcon);

        // Add bottom-right Icon & set Event
        Drawable mRightBottomDrawable = ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_scale_white_18dp);
        IconMenuOption mBottomRightIcon = new IconMenuOption(mRightBottomDrawable, Config.Gravity.RIGHT_BOTOM);
        mBottomRightIcon.setIconEventListener(new BottomRightIconEvent());
        mOptionIcons.add(mBottomRightIcon);

//        Drawable mTopRightDrawable = ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_flip_white_18dp);
//        IconMenuOption mTopRightIcon = new IconMenuOption(mTopRightDrawable, IconMenuOption.RIGHT_TOP);
//        mTopRightIcon.setIconEventListener(new FlipHorizontallyEvent());
//        mOptionIcons.add(mTopRightIcon);
    }

    /**
     * Swaps sticker at layer [[oldPos]] with the one at layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    public void swapLayers(int oldPos, int newPos) {
        if (mArraySticker.size() >= oldPos && mArraySticker.size() >= newPos) {
            Collections.swap(mArraySticker, oldPos, newPos);
            invalidate();
        }
    }

    /**
     * Sends sticker from layer [[oldPos]] to layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    public void sendToLayer(int oldPos, int newPos) {
        if (mArraySticker.size() >= oldPos && mArraySticker.size() >= newPos) {
            Sticker s = mArraySticker.get(oldPos);
            mArraySticker.remove(oldPos);
            mArraySticker.add(newPos, s);
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mStickerViewRect.left = left;
            mStickerViewRect.top = top;
            mStickerViewRect.right = right;
            mStickerViewRect.bottom = bottom;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawStickers(canvas);
    }

    protected void drawStickers(Canvas canvas) {
        for (int i = 0; i < mArraySticker.size(); i++) {
            Sticker sticker = mArraySticker.get(i);
            if (sticker != null) {
                sticker.draw(canvas);
            }
        }

        if (mHandlingSticker != null && !isLocked && (isShowBorder || isShowIcons)) {

            getStickerPoints(mHandlingSticker, bitmapPoints);

            float x1 = bitmapPoints[0];
            float y1 = bitmapPoints[1];
            float x2 = bitmapPoints[2];
            float y2 = bitmapPoints[3];
            float x3 = bitmapPoints[4];
            float y3 = bitmapPoints[5];
            float x4 = bitmapPoints[6];
            float y4 = bitmapPoints[7];

            if (isShowBorder) {
                canvas.drawLine(x1, y1, x2, y2, mBorderPaint);
                canvas.drawLine(x1, y1, x3, y3, mBorderPaint);
                canvas.drawLine(x2, y2, x4, y4, mBorderPaint);
                canvas.drawLine(x4, y4, x3, y3, mBorderPaint);
            }

            //draw mOptionIcons
            if (isShowIcons) {
                float rotation = calculateRotation(x4, y4, x3, y3);
                for (int i = 0; i < mOptionIcons.size(); i++) {
                    IconMenuOption icon = mOptionIcons.get(i);
                    switch (icon.getPosition()) {
                        case Config.Gravity.LEFT_TOP:
                            configIconMatrix(icon, x1, y1, rotation);
                            break;

                        case Config.Gravity.RIGHT_TOP:
                            configIconMatrix(icon, x2, y2, rotation);
                            break;

                        case Config.Gravity.LEFT_BOTTOM:
                            configIconMatrix(icon, x3, y3, rotation);
                            break;

                        case Config.Gravity.RIGHT_BOTOM:
                            configIconMatrix(icon, x4, y4, rotation);
                            break;
                    }
                    icon.draw(canvas, mBorderPaint);
                }
            }
        }
    }

    protected void configIconMatrix(@NonNull IconMenuOption icon, float x, float y, float rotation) {
        icon.setX(x);
        icon.setY(y);
        icon.getMatrix().reset();

        icon.getMatrix().postRotate(rotation, icon.getWidth() / 2, icon.getHeight() / 2);
        icon.getMatrix().postTranslate(x - icon.getWidth() / 2, y - icon.getHeight() / 2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isLocked) return super.onInterceptTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();

                return findCurrentIconTouched() != null || findHandlingSticker() != null;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isLocked) {
            return super.onTouchEvent(event);
        }

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!onTouchDown(event)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistance = calculateDistance(event);
                oldRotation = calculateRotation(event);

                midPoint = calculateMidPoint(event);

                if (mHandlingSticker != null && isInStickerArea(mHandlingSticker, event.getX(1),
                        event.getY(1)) && findCurrentIconTouched() == null) {
                    mCurrentMode = Config.ActionMode.ZOOM_WITH_TWO_FINGER;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                handleCurrentMode(event);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (mCurrentMode == Config.ActionMode.ZOOM_WITH_TWO_FINGER && mHandlingSticker != null) {
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerZoomFinished(mHandlingSticker);
                    }
                }
                mCurrentMode = Config.ActionMode.NONE;
                break;
        }
        return true;
    }

    /**
     * @param event MotionEvent received from {@link #onTouchEvent)
     * @return true if has touch something
     */
    protected boolean onTouchDown(@NonNull MotionEvent event) {
        mCurrentMode = Config.ActionMode.DRAG;

        downX = event.getX();
        downY = event.getY();

        midPoint = calculateMidPoint();
        oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY);
        oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY);

        currentIcon = findCurrentIconTouched();
        if (currentIcon != null) {
            mCurrentMode = Config.ActionMode.ICON;
            currentIcon.onActionDown(this, event);
        } else {
            mHandlingSticker = findHandlingSticker();
        }

        if (mHandlingSticker != null) {
            onStickerOperationListener.onStickerTouchedDown(mHandlingSticker);
            downMatrix.set(mHandlingSticker.getMatrix());
            if (isBringToFrontCurrentSticker) {
                mArraySticker.remove(mHandlingSticker);
                mArraySticker.add(mHandlingSticker);
            }
        }

        if (currentIcon == null && mHandlingSticker == null) {
            return false;
        }


        invalidate();
        return true;
    }

    protected void onTouchUp(@NonNull MotionEvent event) {
        long currentTime = SystemClock.uptimeMillis();

        if (mCurrentMode == Config.ActionMode.ICON && currentIcon != null && mHandlingSticker != null) {
            currentIcon.onActionUp(this, event);
        }

        if (mCurrentMode == Config.ActionMode.DRAG
                && Math.abs(event.getX() - downX) < mTouchSlop
                && Math.abs(event.getY() - downY) < mTouchSlop
                && mHandlingSticker != null) {
            mCurrentMode = Config.ActionMode.CLICK;
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerClicked(mHandlingSticker);
            }
            if (currentTime - mLastClickTime < mMinClickDelayTime) {
                if (onStickerOperationListener != null) {
                    onStickerOperationListener.onStickerDoubleTapped(mHandlingSticker);
                }
            }
        }

        if (mCurrentMode == Config.ActionMode.DRAG && mHandlingSticker != null) {
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDragFinished(mHandlingSticker);
            }
        }

        mCurrentMode = Config.ActionMode.NONE;
        mLastClickTime = currentTime;
    }

    protected void handleCurrentMode(@NonNull MotionEvent event) {
        switch (mCurrentMode) {
            case Config.ActionMode.NONE:
            case Config.ActionMode.CLICK:

                break;
            case Config.ActionMode.DRAG:
                if (mHandlingSticker != null) {
                    moveMatrix.set(downMatrix);
                    moveMatrix.postTranslate(event.getX() - downX, event.getY() - downY);
                    mHandlingSticker.setMatrix(moveMatrix);
                    if (isConstrained) {
                        constrainSticker(mHandlingSticker);
                    }
                }
                break;
            case Config.ActionMode.ZOOM_WITH_TWO_FINGER:
                if (mHandlingSticker != null) {
                    float newDistance = calculateDistance(event);
                    float newRotation = calculateRotation(event);

                    moveMatrix.set(downMatrix);
                    moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                            midPoint.y);
                    moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
                    mHandlingSticker.setMatrix(moveMatrix);
                }

                break;

            case Config.ActionMode.ICON:
                if (mHandlingSticker != null && currentIcon != null) {
                    currentIcon.onActionMove(this, event);
                }
                break;
        }
    }

    public void zoomAndRotateCurrentSticker(@NonNull MotionEvent event) {
        zoomAndRotateSticker(mHandlingSticker, event);
    }

    public void zoomAndRotateSticker(@Nullable Sticker sticker, @NonNull MotionEvent event) {
        if (sticker != null) {
            float newDistance = calculateDistance(midPoint.x, midPoint.y, event.getX(), event.getY());
            float newRotation = calculateRotation(midPoint.x, midPoint.y, event.getX(), event.getY());

            moveMatrix.set(downMatrix);
            moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                    midPoint.y);
            moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
            mHandlingSticker.setMatrix(moveMatrix);
        }
    }

    protected void constrainSticker(@NonNull Sticker sticker) {
        float moveX = 0;
        float moveY = 0;
        int width = getWidth();
        int height = getHeight();
        sticker.getMappedCenterPoint(currentCenterPoint, point, tmp);
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x;
        }

        if (currentCenterPoint.x > width) {
            moveX = width - currentCenterPoint.x;
        }

        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y;
        }

        if (currentCenterPoint.y > height) {
            moveY = height - currentCenterPoint.y;
        }

        sticker.getMatrix().postTranslate(moveX, moveY);
    }


    @Nullable
    protected IconMenuOption findCurrentIconTouched() {
        for (IconMenuOption icon : mOptionIcons) {
            float x = icon.getX() - downX;
            float y = icon.getY() - downY;
            float distance_pow_2 = x * x + y * y;
            if (distance_pow_2 <= Math.pow(icon.getIconRadius() + icon.getIconRadius(), 2)) {
                return icon;
            }
        }

        return null;
    }

    /**
     * find the touched Sticker
     **/
    @Nullable
    protected Sticker findHandlingSticker() {
        for (int i = mArraySticker.size() - 1; i >= 0; i--) {
            if (isInStickerArea(mArraySticker.get(i), downX, downY)) {
                return mArraySticker.get(i);
            }
        }
        return null;
    }

    protected boolean isInStickerArea(@NonNull Sticker sticker, float downX, float downY) {
        tmp[0] = downX;
        tmp[1] = downY;
        return sticker.contains(tmp);
    }

    @NonNull
    protected PointF calculateMidPoint(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            midPoint.set(0, 0);
            return midPoint;
        }
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        midPoint.set(x, y);
        return midPoint;
    }

    @NonNull
    protected PointF calculateMidPoint() {
        if (mHandlingSticker == null) {
            midPoint.set(0, 0);
            return midPoint;
        }
        mHandlingSticker.getMappedCenterPoint(midPoint, point, tmp);
        return midPoint;
    }

    /**
     * calculate rotation in line with two fingers and x-axis
     **/
    protected float calculateRotation(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * calculate Distance in two fingers
     **/
    protected float calculateDistance(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        for (int i = 0; i < mArraySticker.size(); i++) {
            Sticker sticker = mArraySticker.get(i);
            if (sticker != null) {
                transformSticker(sticker);
            }
        }
    }

    /**
     * Sticker's drawable will be too bigger or smaller
     * This method is to transform it to fit
     * step 1：let the center of the sticker image is coincident with the center of the View.
     * step 2：Calculate the zoom and zoom
     **/
    protected void transformSticker(@Nullable Sticker sticker) {
        if (sticker == null) {
            Log.e(TAG, "transformSticker: the bitmapSticker is null or the bitmapSticker bitmap is null");
            return;
        }

        sizeMatrix.reset();

        float width = getWidth();
        float height = getHeight();
        float stickerWidth = sticker.getWidth();
        float stickerHeight = sticker.getHeight();
        //step 1
        float offsetX = (width - stickerWidth) / 2;
        float offsetY = (height - stickerHeight) / 2;

        sizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (width < height) {
            scaleFactor = width / stickerWidth;
        } else {
            scaleFactor = height / stickerHeight;
        }

        sizeMatrix.postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);

        sticker.getMatrix().reset();
        sticker.setMatrix(sizeMatrix);

        invalidate();
    }

    public void flipCurrentSticker(int direction) {
        flip(mHandlingSticker, direction);
    }

    public void flip(@Nullable Sticker sticker, @Config.Flip int direction) {
        if (sticker != null) {
            sticker.getCenterPoint(midPoint);
            if ((direction & Config.Flip.FLIP_HORIZONTALLY) > 0) {
                sticker.getMatrix().preScale(-1, 1, midPoint.x, midPoint.y);
                sticker.setFlippedHorizontally(!sticker.isFlippedHorizontally());
            }
            if ((direction & Config.Flip.FLIP_VERTICALLY) > 0) {
                sticker.getMatrix().preScale(1, -1, midPoint.x, midPoint.y);
                sticker.setFlippedVertically(!sticker.isFlippedVertically());
            }

            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerFlipped(sticker);
            }

            invalidate();
        }
    }

    public boolean replace(@Nullable Sticker sticker) {
        return replace(sticker, true);
    }

    public boolean replace(@Nullable Sticker sticker, boolean needStayState) {
        if (mHandlingSticker != null && sticker != null) {
            float width = getWidth();
            float height = getHeight();
            if (needStayState) {
                sticker.setMatrix(mHandlingSticker.getMatrix());
                sticker.setFlippedVertically(mHandlingSticker.isFlippedVertically());
                sticker.setFlippedHorizontally(mHandlingSticker.isFlippedHorizontally());
            } else {
                mHandlingSticker.getMatrix().reset();
                // reset scale, angle, and put it in center
                float offsetX = (width - mHandlingSticker.getWidth()) / 2f;
                float offsetY = (height - mHandlingSticker.getHeight()) / 2f;
                sticker.getMatrix().postTranslate(offsetX, offsetY);

                float scaleFactor;
                if (width < height) {
                    scaleFactor = width / mHandlingSticker.getDrawable().getIntrinsicWidth();
                } else {
                    scaleFactor = height / mHandlingSticker.getDrawable().getIntrinsicHeight();
                }
                sticker.getMatrix().postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);
            }
            int index = mArraySticker.indexOf(mHandlingSticker);
            mArraySticker.set(index, sticker);
            mHandlingSticker = sticker;

            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(@Nullable Sticker sticker) {
        if (mArraySticker.contains(sticker)) {
            mArraySticker.remove(sticker);
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDeleted(sticker);
            }
            if (mHandlingSticker == sticker) {
                mHandlingSticker = null;
            }
            invalidate();

            return true;
        } else {
            Log.d(TAG, "remove: the sticker is not in this StickerView");
            return false;
        }
    }

    public boolean removeCurrentSticker() {
        return remove(mHandlingSticker);
    }

    public void removeAllStickers() {
        mArraySticker.clear();
        if (mHandlingSticker != null) {
            mHandlingSticker.release();
            mHandlingSticker = null;
        }
        invalidate();
    }

    @NonNull
    public StickerGroup addSticker(@NonNull Sticker sticker) {
        return addSticker(sticker, Config.Position.CENTER);
    }

    public StickerGroup addSticker(@NonNull final Sticker sticker, final @Config.Position int position) {
        if (ViewCompat.isLaidOut(this)) {
            addStickerImmediately(sticker, position);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    addStickerImmediately(sticker, position);
                }
            });
        }
        return this;
    }

    protected void addStickerImmediately(@NonNull Sticker sticker, @Config.Position int position) {
        setStickerPosition(sticker, position);


        float scaleFactor, widthScaleFactor, heightScaleFactor;

        widthScaleFactor = (float) getWidth() / sticker.getDrawable().getIntrinsicWidth();
        heightScaleFactor = (float) getHeight() / sticker.getDrawable().getIntrinsicHeight();
        scaleFactor = widthScaleFactor > heightScaleFactor ? heightScaleFactor : widthScaleFactor;

        sticker.getMatrix()
                .postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);

        mHandlingSticker = sticker;
        mArraySticker.add(sticker);
        if (onStickerOperationListener != null) {
            onStickerOperationListener.onStickerAdded(sticker);
        }
        invalidate();
    }

    protected void setStickerPosition(@NonNull Sticker sticker, @Config.Position int position) {
        float width = getWidth();
        float height = getHeight();
        float offsetX = width - sticker.getWidth();
        float offsetY = height - sticker.getHeight();
        if ((position & Config.Position.TOP) > 0) {
            offsetY /= 4f;
        } else if ((position & Config.Position.BOTTOM) > 0) {
            offsetY *= 3f / 4f;
        } else {
            offsetY /= 2f;
        }
        if ((position & Config.Position.LEFT) > 0) {
            offsetX /= 4f;
        } else if ((position & Config.Position.RIGHT) > 0) {
            offsetX *= 3f / 4f;
        } else {
            offsetX /= 2f;
        }
        sticker.getMatrix().postTranslate(offsetX, offsetY);
    }

    @NonNull
    public float[] getStickerPoints(@Nullable Sticker sticker) {
        float[] points = new float[8];
        getStickerPoints(sticker, points);
        return points;
    }

    public void getStickerPoints(@Nullable Sticker sticker, @NonNull float[] dst) {
        if (sticker == null) {
            Arrays.fill(dst, 0);
            return;
        }
        sticker.getBoundPoints(bounds);
        sticker.getMappedPoints(dst, bounds);
    }

    public void save(@NonNull File file) {
        try {
            StickerUtils.saveImageToGallery(file, createBitmap());
            StickerUtils.notifySystemGallery(getContext(), file);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
            //
        }
    }

    @NonNull
    public Bitmap createBitmap() throws OutOfMemoryError {
        mHandlingSticker = null;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public int getStickerCount() {
        return mArraySticker.size();
    }

    public boolean isNoneSticker() {
        return getStickerCount() == 0;
    }

    public boolean isLocked() {
        return isLocked;
    }

    @NonNull
    public StickerGroup setLocked(boolean locked) {
        this.isLocked = locked;
        invalidate();
        return this;
    }

    @NonNull
    public StickerGroup setMinClickDelayTime(int minClickDelayTime) {
        this.mMinClickDelayTime = minClickDelayTime;
        return this;
    }

    public int getMinClickDelayTime() {
        return mMinClickDelayTime;
    }

    public boolean isConstrained() {
        return isConstrained;
    }

    @NonNull
    public StickerGroup setConstrained(boolean constrained) {
        this.isConstrained = constrained;
        postInvalidate();
        return this;
    }

    @NonNull
    public StickerGroup setOnStickerOperationListener( @Nullable OnStickerOperationListener onStickerOperationListener) {
        this.onStickerOperationListener = onStickerOperationListener;
        return this;
    }

    @Nullable
    public OnStickerOperationListener getOnStickerOperationListener() {
        return onStickerOperationListener;
    }

    @Nullable
    public Sticker getCurrentSticker() {
        return mHandlingSticker;
    }

    @NonNull
    public List<IconMenuOption> getOptionIcons() {
        return mOptionIcons;
    }

    public void setOptionIcons(@NonNull List<IconMenuOption> optionIcons) {
        this.mOptionIcons.clear();
        this.mOptionIcons.addAll(optionIcons);
        invalidate();
    }
}
