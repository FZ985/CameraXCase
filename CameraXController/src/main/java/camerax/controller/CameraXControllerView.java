package camerax.controller;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.LifecycleCameraController;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import camerax.controller.databinding.CameraxControllerViewBinding;
import camerax.core.CameraCase;
import camerax.core.CameraCaseView;

/**
 * by JFZ
 * 2025/4/27
 * desc：
 **/
public class CameraXControllerView extends RelativeLayout {

    private final CameraxControllerViewBinding binding;

    LifecycleOwner lifecycleOwner;

    private LifecycleCameraController cameraController;

    private final List<ControllerCase> caseGroup = new ArrayList<>();

    private CameraCase.CaseDataObserver innerObserver = new CameraCase.CaseDataObserver() {
        @Override
        public void onChanged(int action, @NonNull Object data) {
            super.onChanged(action, data);
        }
    };

    public CameraXControllerView(Context context) {
        this(context, null);
    }

    public CameraXControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraXControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = CameraxControllerViewBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public final void preview(@NonNull LifecycleOwner owner, @NonNull Runnable initSuccessRun, @Nullable ControllerCase... controllerCases) {
        this.lifecycleOwner = owner;
        if (controllerCases != null && controllerCases.length > 0) {
            caseGroup.clear();
            caseGroup.addAll(Arrays.asList(controllerCases));

            for (ControllerCase uc : caseGroup) {
                if (uc != null) {
                    uc.onCaseAttach(getContext(), this, caseGroup);
                }
            }
        }

        post(() -> {
            int width = getWidth();
            int height = getHeight();
            cameraController = new LifecycleCameraController(getContext());
            cameraController.bindToLifecycle(owner);
            binding.controllerPreview.setController(cameraController);

            //添加CaseView
            binding.controllerContainer.removeAllViews();
            for (ControllerCase uc : caseGroup) {
                if (uc != null) {
                    RelativeLayout.LayoutParams params = new LayoutParams(width, height);
                    CameraCaseView<CameraXControllerView> caseView = new CameraCaseView<>(getContext(), uc);
                    caseView.setLayoutParams(params);
                    uc.onCreate(getContext(), this, binding.controllerPreview, caseView, width, height);
                    uc.registerCaseObserver(innerObserver);
                    binding.controllerContainer.addView(caseView, params);
                }
            }

            for (ControllerCase uc : caseGroup) {
                if (uc != null) {
                    uc.onAllCaseCreated();
                }
            }
            initSuccessRun.run();

        });
    }

    private void releaseObserver() {
        try {
            for (ControllerCase uc : caseGroup) {
                uc.unregisterCaseObserver(innerObserver);
            }
        } catch (Exception e) {
            Log.e("CameraXView", "releaseObserver catch:" + e.getMessage());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            releaseObserver();
            caseGroup.clear();
            innerObserver = null;
        } catch (Exception e) {
            Log.e("CameraXView", "销毁 catch:" + e.getMessage());
        }
        super.onDetachedFromWindow();
    }

    @Nullable
    public LifecycleCameraController getCameraController() {
        return cameraController;
    }
}
