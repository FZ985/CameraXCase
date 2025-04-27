package camerax.core;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * by JFZ
 * 2025/4/27
 * desc：
 **/
@SuppressLint("ViewConstructor")
public class CameraCaseView<VG extends ViewGroup> extends View {

    @Nullable
    private CameraCase<VG> cameraCase;

    public CameraCaseView(Context context, @Nullable CameraCase<VG> cameraCase) {
        super(context);
        this.cameraCase = cameraCase;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (cameraCase != null) {
            cameraCase.onDraw(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (cameraCase != null) {
            return cameraCase.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            if (cameraCase != null) {
                cameraCase.onDestroy();
            }
            cameraCase = null;
        } catch (Exception e) {
            Log.e("CaseView", "onDetachedFromWindow:" + e.getMessage());
        }
        super.onDetachedFromWindow();
    }
}
