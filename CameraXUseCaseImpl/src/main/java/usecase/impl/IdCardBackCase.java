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
 * description :身份证反面图层case （人像面）
 */
public class IdCardBackCase extends LayerCase {
    //国徽
    protected final Paint backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final RectF backRect = new RectF();

    private Bitmap backPersonBmp;

    int offsetX;
    int offsetY;

    @Override
    public void onCaseCreated() {
        super.onCaseCreated();
        //人像大小及位置 26mm x 32mm, 54/35 = 1.6875
        float pHeight = layerRectF.height() / 1.6875f;
        float pWidth = pHeight * 4 / 5;
        float pLeft = layerRectF.right - pWidth / 3.5f - pWidth;
        float pTop = layerRectF.top + pHeight / 4;
        float pRight = pLeft + pWidth;
        float pBottom = pTop + pHeight;

        backRect.set(pLeft, pTop, pRight, pBottom);
        backPaint.setColor(getPrimaryColor());
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeWidth(6);

        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.vector_idcard_person);

        offsetX = dp2px(18);
        offsetY = dp2px(10);
        backPersonBmp = Bitmap.createBitmap((int) backRect.width() + offsetX, (int) backRect.height() - offsetY, Bitmap.Config.ARGB_8888);
        if (backPersonBmp != null && drawable != null) {
            Canvas canvas = new Canvas(backPersonBmp);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        //人像矩形范围
        //canvas.drawRect(backRect, backPaint);

        //人像图片
        if (backPersonBmp != null) {
            PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
            backPaint.setColorFilter(colorFilter);
            float personLeft = backRect.centerX() - (float) backPersonBmp.getWidth() / 2 - (float) dp2px(2.5f);
            float personTop = backRect.bottom - backPersonBmp.getHeight();
            canvas.drawBitmap(backPersonBmp, personLeft, personTop, backPaint);
        }
    }

    @Override
    protected void onDestroy() {
        backPersonBmp = null;
        super.onDestroy();
    }

    @Override
    public int getCaseId() {
        return "IdCardBackCase".hashCode();
    }
}
