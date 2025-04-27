package com.camerax.usecase

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.camerax.usecase.databinding.ActivityImageAnalysisCaptureBinding
import com.camerax.usecase.usecase.ImageAnalysisCaptureCase
import usecase.impl.CaptureResult


/**
 * by JFZ
 * 2025/4/23
 * desc：利用图像分析 实现 拍照功能
 **/
class ImageAnalysisCaptureActivity : AppCompatActivity(),
    ImageAnalysisCaptureCase.TakePictureListener {

    private val binding: ActivityImageAnalysisCaptureBinding by lazy {
        ActivityImageAnalysisCaptureBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.camera.preview(this, {}, ImageAnalysisCaptureCase(this))
    }

    override fun onPicture(result: CaptureResult) {
        MainActivity.bitmap = result.bitmap
        setResult(RESULT_OK, Intent())
        finish()
    }
}