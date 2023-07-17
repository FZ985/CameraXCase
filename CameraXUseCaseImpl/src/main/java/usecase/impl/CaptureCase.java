package usecase.impl;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import camerax.usecase.CameraXView;

/**
 * author : JFZ
 * date : 2023/7/17 15:10
 * description : 拍照按钮case
 */
public class CaptureCase extends BaseUseCase {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int radius;
    private int outsideRadius;

    private int insideRadius;

    private float x, y;

    private final RectF rectF = new RectF();

    private int insideColor = Color.WHITE;

    @Override
    public void onCaseCreated() {
        outsideRadius = dp2px(45);
        radius = dp2px(35);
        x = (float) getWidth() / 2;
        y = getHeight() - dp2px(40) - outsideRadius;
        paint.setStyle(Paint.Style.FILL);
        insideRadius = radius;
        rectF.set(x - outsideRadius, y - outsideRadius, x + outsideRadius, y + outsideRadius);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(0xcc444444);
        canvas.drawCircle(x, y, outsideRadius, paint);
        paint.setColor(insideColor);
        canvas.drawCircle(x, y, insideRadius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        boolean contains = rectF.contains(touchX, touchY);
        if (contains) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                insideColor = Color.argb(100, 255, 255, 255);
                invalidate();
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                insideColor = Color.WHITE;
                onTap();
            }
            return true;
        } else {
            insideColor = Color.WHITE;
            invalidate();
        }
        return false;
    }

    private void onTap() {
        View view = getCaseView();
        if (view != null) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        ValueAnimator anim = ValueAnimator.ofInt(radius, (int) (radius * 0.9f), radius);
        anim.setDuration(300);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(animation -> {
            insideRadius = (int) animation.getAnimatedValue();
            invalidate();
            if (animation.getCurrentPlayTime() >= animation.getDuration()) {
                postData(CameraXView.EVENT_TAKE_PICTURE);
            }
        });
        anim.start();
    }

    @Override
    public int getCaseId() {
        return "CaptureCase".hashCode();
    }
}
