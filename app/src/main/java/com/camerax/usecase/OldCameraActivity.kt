package com.camerax.usecase

import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import camerax.core.tools.CameraUtil
import com.camerax.usecase.databinding.ActivityOldCameraBinding
import com.camerax.usecase.oldcamera.ImageInfo
import com.camerax.usecase.oldcamera.OnCameraCallback
import com.camerax.usecase.oldcamera.TakePictureCallback


/**
 * by JFZ
 * 2025/5/7
 * desc：
 **/
class OldCameraActivity : AppCompatActivity() {

    private val binding: ActivityOldCameraBinding by lazy {
        ActivityOldCameraBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.camera.setCameraCallback(object : OnCameraCallback {
            override fun onPreview() {
                Log.e("OldCameraActivity", "onPreview")
            }

            override fun onSurfaceCreated() {
                binding.camera.autoFitSize()
                Log.e("OldCameraActivity", "onSurfaceCreated")
            }

            override fun onError(error: Int, msg: String) {
                Log.e("OldCameraActivity", "onError")
            }
        })

        binding.camera.bindToLifecycle(this)
        binding.capture.setOnClickListener {
//            binding.camera.takePicture(object : TakePictureCallback {
//                override fun onTakePicture(
//                    camera: Camera,
//                    image: ImageInfo
//                ) {
//                    MainActivity.bitmap = CameraUtil.rotateBitmap(image.bitmap!!, image.degrees)
//                    setResult(RESULT_OK, Intent())
//                    finish()
//                }
//            })

            binding.camera.takePicture2(object : TakePictureCallback {
                override fun onTakePicture(camera: Camera, image: ImageInfo) {
                    MainActivity.bitmap =
                        if (image.isYuvToBitmap) CameraUtil.rotateBitmap(
                            image.bitmap!!,
                            image.degrees
                        ) else image.bitmap
                    setResult(RESULT_OK, Intent())
                    finish()
                }
            })

        }

        binding.front.setOnClickListener {
            binding.camera.openCamera(true)
        }

        binding.back.setOnClickListener {
            binding.camera.openCamera(false)
        }

    }

}