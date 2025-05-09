package camerax.usecase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.AspectRatioStrategy;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import camerax.core.CameraCase;
import camerax.core.CameraCaseView;
import camerax.core.tools.CameraUtil;


/**
 * author : JFZ
 * date : 2023/7/15 17:11
 * description :相机控件
 */
public class CameraXView extends RelativeLayout {

    private PreviewView cameraView;//相机预览容器
    private ProcessCameraProvider cameraProvider;

    private RelativeLayout caseContainer;//CaseView的容器

    private final List<UseCase> caseGroup = new ArrayList<>();

    private final List<androidx.camera.core.UseCase> cameraUseCase = new ArrayList<>();

    LifecycleOwner lifecycleOwner;
    Camera camera;
    int mFlashMode = ImageCapture.FLASH_MODE_OFF;

    private CameraSelector userCameraSelector;
    private CameraSelector defaultSelector = CameraSelector.DEFAULT_BACK_CAMERA;
//    private CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

    private CameraCase.CaseDataObserver innerObserver = new CameraCase.CaseDataObserver() {
        @Override
        public void onChanged(int action, @NonNull Object data) {
            super.onChanged(action, data);
        }
    };

    public CameraXView(@NonNull Context context) {
        super(context);
        init();
    }

    public CameraXView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraXView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        View view = View.inflate(getContext(), R.layout.camerax_view, this);
        cameraView = view.findViewById(R.id.camera_);
        cameraView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
        caseContainer = view.findViewById(R.id.case_container);
    }


    @SuppressLint({"UnsafeOptInUsageError", "RestrictedApi"})
    private void initCamera(Runnable runnable) {
        post(() -> {

            int width = getWidth();
            int height = getHeight();

            AspectRatioStrategy screenAspectRatio = CameraUtil.aspectRatioStrategy(width, height);

            int rotation = cameraView.getDisplay().getRotation();

            // CameraProvider
            ProcessCameraProvider provider = cameraProvider;

            CameraSelector cameraSelector = defaultSelector;
            if (userCameraSelector != null) {
                cameraSelector = userCameraSelector;
            } else {
                try {
                    boolean hasBackCamera = cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA);
                    boolean hasFrontCamera = cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA);
                    log("hasBackCamera:" + hasBackCamera + "," + hasFrontCamera);
                    if (hasBackCamera) {
                        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                    } else {
                        if (hasFrontCamera) {
                            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                        } else {
                            cameraSelector = getInnerSelector();
                        }
                    }
                } catch (CameraInfoUnavailableException e) {
                    log("CameraInfoUnavailableException:" + e.getMessage());
                    cameraSelector = getInnerSelector();
                }
            }

            // Preview
            Preview preview = new Preview.Builder()
                    .setResolutionSelector(new ResolutionSelector.Builder()
                            .setAspectRatioStrategy(screenAspectRatio)
                            .build())
                    .setTargetRotation(rotation)
                    .build();

            List<androidx.camera.core.UseCase> cases = new ArrayList<>();
            cases.add(preview);
            if (!cameraUseCase.isEmpty()) {
                cases.addAll(cameraUseCase);
            }
            androidx.camera.core.UseCase[] caseArr = new androidx.camera.core.UseCase[cases.size()];
            for (int i = 0; i < cases.size(); i++) {
                caseArr[i] = cases.get(i);
            }

            // Must unbind the use-cases before rebinding them
            provider.unbindAll();

            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(cameraView.getSurfaceProvider());

            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = provider.bindToLifecycle(lifecycleOwner, cameraSelector, caseArr);
            if (runnable != null) {
                runnable.run();
            }
        });
    }

    private CameraSelector getInnerSelector() {
        CameraSelector availableCameraSelector = getAvailableCameraSelector();
        if (availableCameraSelector != null) {
            return availableCameraSelector;
        } else return defaultSelector;
    }

    public final void preview(@NonNull LifecycleOwner lifecycleOwner, @NonNull Runnable initSuccessRun, @Nullable UseCase... useCases) {
        post(() -> {
            try {
                this.lifecycleOwner = lifecycleOwner;
                cameraProvider = ProcessCameraProvider.getInstance(getContext()).get();
                if (useCases != null && useCases.length > 0) {
                    caseGroup.clear();
                    caseGroup.addAll(Arrays.asList(useCases));

                    for (UseCase uc : caseGroup) {
                        if (uc != null) {
                            uc.onCaseAttach(getContext(), this, caseGroup);
                        }
                    }
                    updateCase();
                }

                initCamera(() -> {
                    int width = getWidth();
                    int height = getHeight();
                    //添加CaseView
                    caseContainer.removeAllViews();
                    for (UseCase uc : caseGroup) {
                        if (uc != null) {
                            RelativeLayout.LayoutParams params = new LayoutParams(width, height);
                            CameraCaseView<CameraXView> caseView = new CameraCaseView<>(getContext(), uc);
                            caseView.setLayoutParams(params);
                            uc.onCreate(getContext(), this, cameraView, caseView, width, height, camera);
                            uc.registerCaseObserver(innerObserver);
                            caseContainer.addView(caseView, params);
                        }
                    }

                    for (UseCase uc : caseGroup) {
                        if (uc != null) {
                            uc.onAllCaseCreated();
                        }
                    }
                    initSuccessRun.run();
                });

//                ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(getContext());
//                future.addListener(() -> {
//                    try {
//                        cameraProvider = future.get();
//
//                    } catch (Exception e) {
//                        Log.e("CameraXView", "预览失败e:" + e.getMessage());
//                    }
//                }, ContextCompat.getMainExecutor(getContext()));
            } catch (Exception e) {
                Log.e("CameraXView", "预览失败:" + e.getMessage());
            }
        });
    }

    private void updateCase() {
        cameraUseCase.clear();
        for (UseCase u : caseGroup) {
            if (u != null) {
                List<androidx.camera.core.UseCase> list = u.getCameraUseCase();
                if (list != null && !list.isEmpty()) {
                    cameraUseCase.addAll(list);
                }
            }
        }
    }

    private void releaseObserver() {
        try {
            for (UseCase uc : caseGroup) {
                uc.unregisterCaseObserver(innerObserver);
            }
        } catch (Exception e) {
            Log.e("CameraXView", "releaseObserver catch:" + e.getMessage());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mFlashMode = ImageCapture.FLASH_MODE_OFF;
        try {
            releaseObserver();
            caseGroup.clear();
            innerObserver = null;
        } catch (Exception e) {
            Log.e("CameraXView", "销毁 catch:" + e.getMessage());
        }
        super.onDetachedFromWindow();
    }

    public final void notifyCamera() {
        notifyCamera(null);
    }

    public final void notifyCamera(@Nullable Runnable runnable) {
        //初始化一次才能重新刷新一下
        if (camera != null) {
            for (UseCase uc : caseGroup) {
                if (uc != null) {
                    uc.onCameraNotify(this);
                }
            }
            updateCase();
            initCamera(runnable);
        }
    }

    @SuppressLint("RestrictedApi")
    private CameraSelector getAvailableCameraSelector() {
        if (cameraProvider != null) {
            List<CameraInfo> availableCameraInfos = cameraProvider.getAvailableCameraInfos();
            CameraInfo firstCameraInfo = availableCameraInfos.get(0);
            return firstCameraInfo.getCameraSelector();
        }
        return null;
    }


    public final int getFlashMode() {
        return mFlashMode;
    }

    @SuppressLint("RestrictedApi")
    public final int getLensFacing() {
        CameraSelector selector = userCameraSelector;
        if (selector == null) {
            selector = defaultSelector;
        }
        Integer lensFacing = selector.getLensFacing();
        if (lensFacing != null) {
            return lensFacing;
        }
        return CameraSelector.LENS_FACING_BACK;
    }

    public final void setFlashMode(int mFlashMode) {
        this.mFlashMode = mFlashMode;
    }

    public final CameraSelector getDefaultCameraSelector() {
        return defaultSelector;
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    public ProcessCameraProvider getCameraProvider() {
        return cameraProvider;
    }

    public final CameraSelector getCameraSelector() {
        return userCameraSelector;
    }

    public final void setCameraSelector(@NonNull CameraSelector cameraSelector) {
        this.userCameraSelector = cameraSelector;
    }


    private void log(String m) {
        Log.e("CameraXView", m);
    }

}
