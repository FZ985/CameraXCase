package usecase.impl;


import android.graphics.Bitmap;

import androidx.annotation.Nullable;

/**
 * by JFZ
 * 2025/4/26
 * desc：
 **/
public class CaptureResult {

    private Bitmap bitmap;

    private int rotationDegrees;

    private Exception exception;

    public CaptureResult(Exception exception) {
        this.exception = exception;
    }

    public CaptureResult(Bitmap bitmap, int rotationDegrees) {
        this.bitmap = bitmap;
        this.rotationDegrees = rotationDegrees;
    }

    @Nullable
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getRotationDegrees() {
        return rotationDegrees;
    }

    @Nullable
    public Exception getException() {
        return exception;
    }
}
