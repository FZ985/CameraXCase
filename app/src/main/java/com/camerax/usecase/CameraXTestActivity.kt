package com.camerax.usecase

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.camerax.usecase.databinding.ActivityCameraxTestBinding


/**
 * by JFZ
 * 2025/5/8
 * desc：
 **/
class CameraXTestActivity : AppCompatActivity() {
    private val binding: ActivityCameraxTestBinding by lazy {
        ActivityCameraxTestBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.capture.setOnClickListener {

        }

        initCamera()
    }

    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()

            // 设置 Surface 给 Preview
            preview.setSurfaceProvider { request ->
                request.provideSurface(
                    binding.surface.holder.surface,
                    ContextCompat.getMainExecutor(this)
                ) {
                    log("result:${it.resultCode}")
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (exc: Exception) {
                log("Use case binding failed" + exc.message)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun log(m: String) {
        Log.e("CameraX", m)
    }
}