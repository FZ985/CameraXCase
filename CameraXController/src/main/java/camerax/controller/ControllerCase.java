package camerax.controller;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;

import camerax.core.CameraCase;
import camerax.core.CameraCaseView;

/**
 * by JFZ
 * 2025/4/27
 * desc：
 **/
public abstract class ControllerCase extends CameraCase<CameraXControllerView> {

    protected final void onCreate(@NonNull Context context,
                                  @NonNull CameraXControllerView cameraView,
                                  @NonNull PreviewView previewView,
                                  @NonNull CameraCaseView<CameraXControllerView> cameraCaseView,
                                  int width, int height) {
        this.mContext = context;
        this.mResources = context.getResources();
        this.mTheme = context.getTheme();
        this.mCameraView = cameraView;
        this.mPreviewView = previewView;
        this.mCameraCaseView = cameraCaseView;
        this.mWidth = width;
        this.mHeight = height;
        onCaseCreated();
    }

}
