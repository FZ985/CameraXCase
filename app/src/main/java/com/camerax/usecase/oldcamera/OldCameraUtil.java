package com.camerax.usecase.oldcamera;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.Surface;
import android.view.WindowManager;

/**
 * by JFZ
 * 2025/5/8
 * desc：
 **/
public class OldCameraUtil {

    public static int getOldRotationDegrees(Context context, int cameraId) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();

        int degrees = 0;
        if (rotation == Surface.ROTATION_90) {
            degrees = 90;
        } else if (rotation == Surface.ROTATION_180) {
            degrees = 180;
        } else if (rotation == Surface.ROTATION_270) {
            degrees = 270;
        }

        int result;
        int orientation = compatOrientation(info);
        if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (orientation + degrees) % 360;
        } else {
            result = (orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static int compatOrientation(android.hardware.Camera.CameraInfo info) {
        int orientation = info.orientation;
        if (orientation < 0) {
            return 0;
        }
        if (orientation % 90 == 0) {
            return orientation;
        }
        return Math.round((float) orientation / 90) * 90;
    }

    //将bitmap 水平翻转
    private Bitmap flipImageHorizontally(Bitmap bitmap) {
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
