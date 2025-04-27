package com.camerax.usecase.usecase;

import android.annotation.SuppressLint;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;

import com.camerax.usecase.R;

import usecase.impl.BaseUseCase;

/**
 * author : JFZ
 * date : 2023/7/18 14:29
 * description : 切换摄像头
 */
public class LensFacingCase extends BaseUseCase {

    public static final int ID = "LensFacingCase".hashCode();

    @Override
    public int getCaseId() {
        return ID;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCaseCreated() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp2px(50);
        params.rightMargin = dp2px(15);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_lensfacing_flash, null);

        if (mCameraCaseView != null) {
            mCameraCaseView.setVisibility(View.GONE);
        }

        ImageView lensFacingIv = view.findViewById(R.id.lensfacing);
        lensFacingIv.setImageResource(R.drawable.lens_facing);

        ImageView flashModeIv = view.findViewById(R.id.flash);
        flashModeIv.setImageResource(R.drawable.flash_off);

        addView(view, params);

        lensFacingIv.setOnClickListener(v -> {
            shake(v);
            if (mCameraView.getLensFacing() == CameraSelector.LENS_FACING_BACK) {
                mCameraView.setCameraSelector(CameraSelector.DEFAULT_FRONT_CAMERA);
            } else {
                mCameraView.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
            }
            rotation(lensFacingIv);
            mCameraView.notifyCamera();
        });

        flashModeIv.setOnClickListener(v -> {
            shake(v);
            if (mCameraView.getFlashMode() == ImageCapture.FLASH_MODE_OFF) {
                mCameraView.setFlashMode(ImageCapture.FLASH_MODE_ON);
                flashModeIv.setImageResource(R.drawable.flash_on);
            } else {
                mCameraView.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                flashModeIv.setImageResource(R.drawable.flash_off);
            }
            scale(flashModeIv);
            mCameraView.notifyCamera();
        });
    }

    private void rotation(ImageView view) {
        RotateAnimation animRotate = new RotateAnimation(0, -180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animRotate.setDuration(350);
        animRotate.setInterpolator(new DecelerateInterpolator());
        animRotate.setFillAfter(true);
        view.startAnimation(animRotate);
    }

    private void scale(ImageView view) {
        ScaleAnimation animScale = new ScaleAnimation(0.85f, 1, 0.85f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animScale.setDuration(300);
        animScale.setFillAfter(true);
        animScale.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(animScale);
    }

    private void shake(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }
}
