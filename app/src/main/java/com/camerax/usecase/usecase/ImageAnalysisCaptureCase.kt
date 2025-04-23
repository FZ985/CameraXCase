package com.camerax.usecase.usecase

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.UseCase
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.core.content.ContextCompat
import camerax.usecase.CameraXView
import com.camerax.usecase.databinding.CaseImageAnalysisCaptureBinding
import usecase.impl.BaseUseCase
import kotlin.math.absoluteValue


/**
 * by JFZ
 * 2025/4/23
 * desc：
 **/
class ImageAnalysisCaptureCase(private val listener: TakePictureListener?) : BaseUseCase() {

    companion object {
        @JvmStatic
        val ID = "ImageAnalysisCaptureCase".hashCode().absoluteValue
    }

    private val binding: CaseImageAnalysisCaptureBinding by lazy {
        CaseImageAnalysisCaptureBinding.inflate(LayoutInflater.from(context))
    }

    private var imageAnalysis: ImageAnalysis? = null

    private var bitmap: Bitmap? = null

    private var isTakePicture = false

    override fun onAttach(context: Context, cameraView: CameraXView) {
        imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
//                    .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            Size(
                                1080,
                                1920
                            ), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                        )
                    )
                    .build()
            )
            .setOutputImageRotationEnabled(true)
            .setTargetRotation(cameraView.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    override fun onCaseCreated() {
        removeView(binding.root)
        addView(
            binding.root,
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        )

        imageAnalysis?.setAnalyzer(ContextCompat.getMainExecutor(context)) {
            if (isTakePicture && bitmap == null) {
                isTakePicture = false
                bitmap = it.toBitmap()
                bitmap?.let { bmp ->
                    listener?.onPicture(bmp)
                }
            }
            it.close()
        }

        binding.takePicture.setOnClickListener {
            takePicture()
        }
    }

    private fun takePicture() {
        bitmap?.recycle()
        bitmap = null
        isTakePicture = true
    }

    override fun getCameraUseCase(): List<UseCase?>? {
        return listOf(imageAnalysis)
    }

    override fun onDestroy() {
        bitmap?.recycle()
        bitmap = null
        isTakePicture = false
        super.onDestroy()
    }

    override fun getCaseId(): Int = ImageAnalysisCaptureCase.ID


    interface TakePictureListener {
        fun onPicture(bitmap: Bitmap)
    }
}