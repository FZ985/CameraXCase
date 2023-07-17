package usecase.impl;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

/**
 * author : JFZ
 * date : 2023/7/15 17:42
 * description : 身份证大小图层case
 */
public class LayerCase extends BaseUseCase {

    public static final float ratio_id_card = 1.58f;//身份证宽高比， 宽为高的1.58倍

    protected Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //80：50%； 99：60%； b3：70%； cc：80%； e6：90%
    private final int layerColor = Color.parseColor("#80000000");

    protected final RectF layerRectF = new RectF();

    protected float layerRadius;

    @Override
    public void onCaseCreated() {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        //卡片底部偏移
        int offsetY = dp2px(50);
        int dp20 = dp2px(20);
        int width = getWidth();
        int height = getHeight();

        layerRadius = (float) dp20 / 2;

        int cardWidth = width - dp20 - dp20;
        int cardHeight = (int) ((float) cardWidth / ratio_id_card);
        int left = dp20;
        int top = height / 2 - cardHeight / 2 - offsetY;
        int right = left + cardWidth;
        int bottom = top + cardHeight;
        layerRectF.set(left, top, right, bottom);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(layerColor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawRoundRect(layerRectF, layerRadius, layerRadius, paint);
        canvas.restoreToCount(layerId);
    }

    @Override
    public int getCaseId() {
        return "LayerCase".hashCode();
    }
}
