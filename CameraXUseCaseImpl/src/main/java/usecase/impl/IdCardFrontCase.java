package usecase.impl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * author : JFZ
 * date : 2023/7/17 15:02
 * description :身份证正面图层case （国徽面）
 */
public class IdCardFrontCase extends LayerCase {
    //国徽
    protected final Paint frontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final RectF frontRect = new RectF();

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
        frontPaint.setStrokeWidth(9);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //国徽
        canvas.drawRect(frontRect, frontPaint);
    }

    @Override
    public int getCaseId() {
        return "IdCardFrontCase".hashCode();
    }
}
