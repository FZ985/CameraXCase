package usecase.impl;

import android.content.res.TypedArray;
import android.graphics.Color;

import camerax.usecase.UseCase;

/**
 * author : JFZ
 * date : 2023/7/17 14:36
 * description :
 */
public abstract class BaseUseCase extends UseCase {

    public int getPrimaryColor() {
        // 获取colorPrimary属性值
        TypedArray typedArray = getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
        int colorPrimary = typedArray.getColor(0, Color.BLACK);
        typedArray.recycle();
        return colorPrimary;
    }

    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
