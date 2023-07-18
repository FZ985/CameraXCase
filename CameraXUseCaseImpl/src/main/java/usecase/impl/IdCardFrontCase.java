package usecase.impl;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

/**
 * author : JFZ
 * date : 2023/7/17 15:02
 * description :身份证正面图层case （国徽面）
 */
public class IdCardFrontCase extends LayerCase {
    //国徽
    protected final Paint frontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final RectF frontRect = new RectF();

    private Bitmap frontBmp;

    @Override
    public void onCaseCreated() {
        super.onCaseCreated();
        //国徽大小及位置
        float bHeight = layerRectF.height() / 3f;
        float bWidth = bHeight;
        float bLeft = layerRectF.width() / 10;
        float bTop = layerRectF.top + layerRectF.height() / 21;
        float bRight = bLeft + bWidth;
        float bBottom = bTop + bHeight;
        frontRect.set(bLeft, bTop, bRight, bBottom);
        frontPaint.setColor(getPrimaryColor());
        frontPaint.setStyle(Paint.Style.STROKE);
        frontPaint.setStrokeWidth(6);

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.vector_idcard_badge);

        frontBmp = Bitmap.createBitmap((int) frontRect.width(), (int) frontRect.height(), Bitmap.Config.ARGB_8888);
        if (frontBmp != null && drawable != null) {
            Canvas canvas = new Canvas(frontBmp);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        //国徽矩形
//        canvas.drawRect(frontRect, frontPaint);

        //绘制国徽
        if (frontBmp != null) {
            @SuppressLint("DrawAllocation") PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
            frontPaint.setColorFilter(colorFilter);
            float personLeft = frontRect.left;
            float personTop = frontRect.top;
            canvas.drawBitmap(frontBmp, personLeft, personTop, frontPaint);
        }
    }

    @Override
    public int getCaseId() {
        return "IdCardFrontCase".hashCode();
    }
}
