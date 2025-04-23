package usecase.impl;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

import camerax.usecase.CameraUtil;
import camerax.usecase.UseCase;


/**
 * author : JFZ
 * date : 2023/7/5 14:13
 * description : 拍照结果预览
 */
public class PreviewResultCase extends LayerCase {

    public static final int ID = "PreviewResultCase".hashCode();

    public interface PreviewConfirmListener {
        void onConfirm(Bitmap originalBitmap, Bitmap cropBitmap, RectF rect, int width, int height);
    }

    private Bitmap bitmap;

    private Bitmap originalBitmap;
    private Bitmap cropBitmap;

    private Paint confirmPaint;

    private float btnRadius;
    private int btnSize;

    private float leftX, rightX, btnY;

    private float strokeWidth;

    private boolean isAnim = false;

    //取消按钮
    private Paint cancelPaint;
    private Path cancelPath;
    private RectF cancelRect;
    private float btnCancelX;
    private float cancelIndex;

    private final RectF leftRect = new RectF();

    //确认按钮
    private float btnConfirmX;
    private Path confirmPath;
    private final RectF rightRect = new RectF();

    private int themeColor;

    private final PreviewConfirmListener listener;

    public PreviewResultCase(PreviewConfirmListener listener) {
        this.listener = listener;
    }

    private UseCase.CaseDataObserver mObserver = new UseCase.CaseDataObserver() {
        @Override
        public void onChanged(int action, @NonNull Object data) {
            if (action == CaptureCase.EVENT_TAKE_PICTURE && data instanceof Bitmap) {
                bitmap = (Bitmap) data;
                Bitmap bm = CameraUtil.rotateBitmap(bitmap, 90);
                originalBitmap = CameraUtil.centerCrop(bm, getWidth(), getHeight());
                invalidate();
            }
        }
    };

    @Override
    public void onCaseCreated() {
        super.onCaseCreated();
        registerCaseObserver(mObserver);
        //取消按钮
        cancelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cancelPaint.setAntiAlias(true);
        cancelPaint.setStyle(Paint.Style.FILL);
        //确认按钮
        confirmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        confirmPaint.setAntiAlias(true);
        confirmPaint.setStyle(Paint.Style.FILL);
        //按钮半径及大小
        btnRadius = dp2px(35);
        btnSize = (int) btnRadius * 2;

        leftX = (float) getWidth() / 4;
        rightX = (float) getWidth() * 3 / 4;
        strokeWidth = btnRadius * 2 / 50f;
        btnY = getHeight() - dp2px(85);

        cancelIndex = btnRadius * 2 / 10f;
        cancelRect = new RectF(btnCancelX, btnY - cancelIndex, btnCancelX + cancelIndex * 2, btnY + cancelIndex);

        cancelPath = new Path();
        confirmPath = new Path();

        themeColor = getPrimaryColor();

        leftRect.set(leftX - btnRadius, btnY - btnRadius, leftX - btnRadius + btnRadius * 2, btnY + btnRadius);
        rightRect.set(rightX - btnRadius, btnY - btnRadius, rightX - btnRadius + btnRadius * 2, btnY + btnRadius);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            canvas.drawColor(Color.BLACK);
//            canvas.drawBitmap(originalBitmap, null, new RectF(0, 0, getCaseWidth(), getCaseHeight()), paint);
            cropBitmap = CameraUtil.rangBitmap(originalBitmap, (int) layerRectF.left, (int) layerRectF.top, (int) layerRectF.width(), (int) layerRectF.height());
            canvas.drawBitmap(cropBitmap, layerRectF.left, layerRectF.top, paint);

            if (!isAnim) {
                isAnim = true;
                startAnim();
            } else {
                //绘制取消按钮
                cancelPaint.setAntiAlias(true);
                cancelPaint.setStyle(Paint.Style.FILL);
                cancelPaint.setColor(0xEEDCDCDC);
                canvas.drawCircle(btnCancelX, btnY, btnRadius, cancelPaint);

                cancelPaint.setColor(themeColor);
                cancelPaint.setStyle(Paint.Style.STROKE);
                cancelPaint.setStrokeWidth(strokeWidth);
                cancelPath.reset();
                cancelPath.moveTo(btnCancelX - cancelIndex, btnY - cancelIndex);
                cancelPath.lineTo(btnCancelX + cancelIndex, btnY + cancelIndex);
                canvas.drawPath(cancelPath, cancelPaint);
                cancelPath.moveTo(btnCancelX - cancelIndex, btnY + cancelIndex);
                cancelPath.lineTo(btnCancelX + cancelIndex, btnY - cancelIndex);
                cancelPath.close();
                canvas.drawPath(cancelPath, cancelPaint);

                //绘制确认按钮
                confirmPaint.setColor(0xFFFFFFFF);
                confirmPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(btnConfirmX, btnY, btnRadius, confirmPaint);

                confirmPaint.setStyle(Paint.Style.STROKE);
                confirmPaint.setColor(themeColor);
                confirmPath.reset();
                confirmPaint.setStrokeWidth(strokeWidth);
                confirmPath.moveTo(btnConfirmX - btnSize / 6f, btnY);
                confirmPath.lineTo(btnConfirmX - btnSize / 21.2f, btnY + btnSize / 7.7f);
                confirmPath.lineTo(btnConfirmX + btnSize / 4.0f, btnY - btnSize / 8.5f);
                confirmPath.lineTo(btnConfirmX - btnSize / 21.2f, btnY + btnSize / 9.4f);
                confirmPath.close();
                canvas.drawPath(confirmPath, confirmPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (bitmap == null) return false;
        float x = event.getX();
        float y = event.getY();
        boolean lefTouch = leftRect.contains(x, y);
        boolean rightTouch = rightRect.contains(x, y);
        RectF contentRect = new RectF(leftRect.left, leftRect.top, rightRect.right, rightRect.bottom);
        boolean contentTouch = contentRect.contains(x, y);
        if (contentTouch) {
            if (lefTouch) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    bitmap = null;
                    originalBitmap = null;
                    cropBitmap = null;
                    isAnim = false;
                    shake();
                    reset();
                }
            }
            if (rightTouch) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    shake();
                    if (listener != null) {
                        listener.onConfirm(Bitmap.createBitmap(originalBitmap), Bitmap.createBitmap(cropBitmap), layerRectF, getWidth(), getHeight());
                    }
                    reset();
                }
            }
            return true;
        }
        return false;
    }

    private void reset() {
        bitmap = null;
        originalBitmap = null;
        cropBitmap = null;
        invalidate();
    }

    @SuppressLint("Recycle")
    private void startAnim() {
        // 取消按钮动画
        ValueAnimator cancelAnim = ValueAnimator.ofFloat(rightX, leftX);
        cancelAnim.addUpdateListener(animation -> {
            btnCancelX = (float) animation.getAnimatedValue();
            cancelRect.set(btnCancelX, btnY - cancelIndex, btnCancelX + cancelIndex * 2, btnY + cancelIndex);
            invalidate();
        });

        ValueAnimator confirmAnim = ValueAnimator.ofFloat(leftX, rightX);
        confirmAnim.addUpdateListener(animation -> {
            btnConfirmX = (float) animation.getAnimatedValue();
            invalidate();
        });

        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.setInterpolator(new LinearInterpolator());
        set.playTogether(cancelAnim, confirmAnim);
        set.start();
    }


    private void shake() {
        View view = getCaseView();
        if (view != null) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterCaseObserver(mObserver);
        isAnim = false;
        mObserver = null;
        bitmap = null;
        super.onDestroy();
    }

    @Override
    public int getCaseId() {
        return ID;
    }
}
