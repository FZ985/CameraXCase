package usecase.impl;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import camerax.usecase.UseCase;

/**
 * author : JFZ
 * date : 2023/7/17 14:36
 * description :
 */
public abstract class BaseUseCase extends UseCase {


    protected final Handler handler = new Handler(Looper.getMainLooper());

    public final int getPrimaryColor() {
        if (mTheme == null) {
            return Color.BLACK;
        }
        // 获取colorPrimary属性值
        TypedArray typedArray = mTheme.obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
        int colorPrimary = typedArray.getColor(0, Color.BLACK);
        typedArray.recycle();
        return colorPrimary;
    }

    public final int dp2px(float dpValue) {
        if (mContext == null) {
            return 0;
        }
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
