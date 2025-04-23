package com.camerax.usecase.usecase;

import android.util.Size;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.camera.core.ImageAnalysis;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import camerax.usecase.UseCase;

/**
 * author : JFZ
 * date : 2023/9/1 17:19
 * description : 每一帧图像分析case, 参考示例
 */
public class ImageAnalysisCase extends UseCase {

    private final ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
            .setTargetResolution(new Size(720, 1280)) // 图片的建议尺寸
            .setOutputImageRotationEnabled(true) // 是否旋转分析器中得到的图片
            .setTargetRotation(Surface.ROTATION_0) // 允许旋转后 得到图片的旋转设置
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build();

    public ImageAnalysisCase() {
        imageAnalysis.setAnalyzer(Executors.newFixedThreadPool(5), image -> {
//            log("========image:"+image.getPlanes()[0]);
            image.close();
        });
    }

    @Nullable
    @Override
    public List<androidx.camera.core.UseCase> getCameraUseCase() {
        return Collections.singletonList(imageAnalysis);
    }

    @Override
    public int getCaseId() {
        return "ImageAnalysisCase".hashCode();
    }
}
