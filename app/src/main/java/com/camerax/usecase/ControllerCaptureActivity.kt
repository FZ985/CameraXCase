package com.camerax.usecase

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
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

        Toast.makeText(this,"rotation:"+windowManager.defaultDisplay.rotation, Toast.LENGTH_SHORT).show()
        binding.camera.preview(
            this,
            {},
            CaptureControllerCase(object : CaptureControllerCase.Callback {
                override fun onImage(bitmap: Bitmap) {
                    MainActivity.bitmap = bitmap
                    setResult(RESULT_OK, Intent())
                    finish()
                }
            })
        )
    }
}