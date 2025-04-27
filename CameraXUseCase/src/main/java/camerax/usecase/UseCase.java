package camerax.usecase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.view.PreviewView;

import java.util.List;

import camerax.core.CameraCase;
import camerax.core.CameraCaseView;

/**
 * author : JFZ
 * date : 2023/7/15 17:36
 * description : UseCase功能开发类
 */
public abstract class UseCase extends CameraCase<CameraXView> {

    Camera mCamera;

    protected final void onCreate(@NonNull Context context,
                                  @NonNull CameraXView cameraView,
                                  @NonNull PreviewView previewView,
                                  @NonNull CameraCaseView<CameraXView> cameraCaseView,
                                  int width, int height,
                                  Camera camera) {
        this.mContext = context;
        this.mResources = context.getResources();
        this.mTheme = context.getTheme();
        this.mCameraView = cameraView;
        this.mCamera = camera;
        this.mPreviewView = previewView;
        this.mCameraCaseView = cameraCaseView;
        this.mWidth = width;
        this.mHeight = height;
        onCaseCreated();
    }


    /**
     * 相机改变
     */
    public void onCameraNotify(CameraXView cameraView) {

    }

    public final Camera getCamera() {
        return mCamera;
    }


    //谷歌的usecase
    @Nullable
    public List<androidx.camera.core.UseCase> getCameraUseCase() {
        return null;
    }

}
