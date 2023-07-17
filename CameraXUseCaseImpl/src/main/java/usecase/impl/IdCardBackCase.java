package usecase.impl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * author : JFZ
 * date : 2023/7/17 15:02
 * description :身份证反面图层case （人像面）
 */
public class IdCardBackCase extends LayerCase {
    //国徽
    protected final Paint backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final RectF backRect = new RectF();

    @Override
    public void onCaseCreated() {
        super.onCaseCreated();
        //人像大小及位置 26mm x 32mm, 54/35 = 1.6875
        float pHeight = layerRectF.height() / 1.6875f;
        float pWidth = pHeight * 3 / 4;
        float pLeft = layerRectF.right - pWidth / 4 - pWidth;
        float pTop = layerRectF.top + pHeight / 4;
        float pRight = pLeft + pWidth;
        float pBottom = pTop + pHeight;

        backRect.set(pLeft, pTop, pRight, pBottom);
        backPaint.setColor(getPrimaryColor());
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeWidth(9);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //人像
        canvas.drawRect(backRect, backPaint);
    }

    @Override
    public int getCaseId() {
        return "IdCardBackCase".hashCode();
    }
}
