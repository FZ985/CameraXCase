package usecase.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.MeteringPoint;

/**
 * author : JFZ
 * date : 2023/7/17 14:33
 * description : 聚焦
 */
public class FocusCase extends BaseUseCase {

    private Paint mPaint;
    private int size;
    private float left, top, right, bottom;

    private int visibleColor = Color.parseColor("#EE16AE16");
    private final int goneColor = Color.TRANSPARENT;

    private boolean isDoubleTap = false;

    @Override
    public void onCaseCreated() {
        size = dp2px(35);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(goneColor);
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        visibleColor = getPrimaryColor();
        reset(getWidth() / 2.0f, getHeight() / 2.0f, size / 2);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //绘制简单的十字线
        canvas.drawLine(left, top + (bottom - top) / 2, right, top + (bottom - top) / 2, mPaint);
        canvas.drawLine(left + (right - left) / 2, top, left + (right - left) / 2, bottom, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getPreviewView() == null || getCamera() == null) {
            return false;
        }
        boolean previewRet = getPreviewView().onTouchEvent(event);
        androidx.camera.core.CameraControl cameraControl = getCamera().getCameraControl();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            isDoubleTap = false;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (!isDoubleTap) isDoubleTap = event.getPointerCount() > 1;
        } else if (action == MotionEvent.ACTION_UP) {
            if (event.getPointerCount() == 1 && !isDoubleTap) {
                cameraControl.cancelFocusAndMetering();
                MeteringPoint point = getPreviewView().getMeteringPointFactory().createPoint(event.getX(), event.getY());
                FocusMeteringAction build = new FocusMeteringAction.Builder(point).build();
                cameraControl.startFocusAndMetering(build);
                //聚焦动画
                mPaint.setColor(visibleColor);
                startFocus(event.getX(), event.getY(), () -> {
                    mPaint.setColor(goneColor);
                    invalidate();
                });
                return previewRet;
            }
        }
        return true;
    }

    private void reset(float x, float y, int length) {
        left = x - length;
        top = y - length;
        right = x + length;
        bottom = y + length;
    }

    private void startFocus(float x, float y, Runnable callback) {
        ValueAnimator scale = ValueAnimator.ofInt(size / 2, (int) (size / 2 * 0.7f), size / 2);
        scale.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            reset(x, y, value);
            invalidate();
        });
        AnimatorSet animSet = new AnimatorSet();
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (callback != null) {
                    callback.run();
                }
            }
        });
        animSet.play(scale);
        animSet.setDuration(500);
        animSet.start();
    }

    @Override
    public int getCaseId() {
        return "FocusCase".hashCode();
    }
}
