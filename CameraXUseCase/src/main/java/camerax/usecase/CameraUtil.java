package camerax.usecase;


import android.graphics.Bitmap;
import android.graphics.Matrix;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.resolutionselector.AspectRatioStrategy;

/**
 * by JFZ
 * 2025/4/23
 * desc：
 **/
public class CameraUtil {
    public static int aspectRatio(int width, int height) {
        int previewRatio = Math.max(width, height) / Math.min(width, height);
        double RATIO_4_3_VALUE = 4.0 / 3.0;
        double RATIO_16_9_VALUE = 16.0 / 9.0;
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    public static AspectRatioStrategy aspectRatioStrategy(int width, int height) {
        int previewRatio = Math.max(width, height) / Math.min(width, height);
        double RATIO_4_3_VALUE = 4.0 / 3.0;
        double RATIO_16_9_VALUE = 16.0 / 9.0;
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY;
        }
        return AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY;
    }

    //旋转bitmap
    @NonNull
    public static Bitmap rotateBitmap(@NonNull Bitmap bitmap, int rotationDegrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }


    //获取指定范围区域的bitmap
    public static Bitmap rangBitmap(Bitmap source, int x, int y, int width, int height) {
        return Bitmap.createBitmap(source, x, y, width, height);
    }

    //将bitmap进行等比裁剪
    public static Bitmap centerCrop(Bitmap source, int targetWidth, int targetHeight) {
        float sourceRatio = (float) source.getWidth() / source.getHeight();
        float targetRatio = (float) targetWidth / targetHeight;
        int startX, startY, width, height;
        if (sourceRatio > targetRatio) {
            width = (int) (source.getHeight() * targetRatio);
            height = source.getHeight();
            startX = (source.getWidth() - width) / 2;
            startY = 0;
        } else {
            width = source.getWidth();
            height = (int) (source.getWidth() / targetRatio);
            startX = 0;
            startY = (source.getHeight() - height) / 2;
        }
        Matrix matrix = new Matrix();
        float scaleX = (float) targetWidth / width;
        float scaleY = (float) targetHeight / height;
        matrix.setScale(scaleX, scaleY);
        return Bitmap.createBitmap(source, startX, startY, width, height, matrix, true);
    }

    //将bitmap 水平翻转
    public static Bitmap flipImageHorizontally(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //将bitmap 垂直翻转
    public static Bitmap flipBitmapVertically(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);  // 垂直翻转
        return Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
        );
    }
}
