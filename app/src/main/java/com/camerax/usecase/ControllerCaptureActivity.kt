package com.camerax.usecase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.camerax.usecase.controller.CaptureControllerCase
import com.camerax.usecase.databinding.ActivityControllerCaptureBinding


/**
 * by JFZ
 * 2025/4/27
 * desc：
 **/
class ControllerCaptureActivity : AppCompatActivity() {

    private val binding: ActivityControllerCaptureBinding by lazy {
        ActivityControllerCaptureBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.camera.preview(this, {}, CaptureControllerCase())
    }
}