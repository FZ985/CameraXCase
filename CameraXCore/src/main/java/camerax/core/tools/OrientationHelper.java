package camerax.core.tools;


import android.content.Context;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

/**
 * by JFZ
 * 2025/4/25
 * desc：
 **/
public class OrientationHelper {

    private int newRotation;
    private int rotationDegrees;
    private final OrientationEventListener orientationEventListener;

    public OrientationHelper(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        newRotation = wm.getDefaultDisplay().getRotation();
        updateDegrees(newRotation);
        orientationEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) return;
                // 将角度映射为 Surface 旋转常量
                if (orientation >= 315 || orientation < 45) {
                    newRotation = Surface.ROTATION_0;
                } else if (orientation >= 45 && orientation < 135) {
                    newRotation = Surface.ROTATION_270;
                } else if (orientation >= 135 && orientation < 225) {
                    newRotation = Surface.ROTATION_180;
                } else {
                    newRotation = Surface.ROTATION_90;
                }
                updateDegrees(newRotation);
//                Log.e("OrientationHelper","orientation:" + orientation + ",newRotation:" + newRotation);
            }
        };
        orientationEventListener.enable();
    }

    private void updateDegrees(int rotation) {
        if (rotation == Surface.ROTATION_0) {
            rotationDegrees = 0;
        } else if (rotation == Surface.ROTATION_270) {
            rotationDegrees = 270;
        } else if (rotation == Surface.ROTATION_180) {
            rotationDegrees = 180;
        } else {
            rotationDegrees = 90;
        }
    }

    public int getRotation() {
        return newRotation;
    }

    public int getRotationDegrees() {
        return rotationDegrees;
    }

    public void destroy() {
        try {
            if (orientationEventListener != null) {
                orientationEventListener.disable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
