package usecase.impl;


import android.graphics.Bitmap;

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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getRotationDegrees() {
        return rotationDegrees;
    }

    public Exception getException() {
        return exception;
    }
}
