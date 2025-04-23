package com.camerax.usecase

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.camerax.usecase.databinding.ActivityImageAnalysisCaptureBinding
import com.camerax.usecase.usecase.ImageAnalysisCaptureCase


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

    override fun onPicture(bitmap: Bitmap) {
        MainActivity.bitmap = bitmap
        setResult(RESULT_OK, Intent())
        finish()
    }
}