package com.camerax.usecase.controller

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.Recording
import androidx.core.content.ContextCompat
import camerax.controller.ControllerCase
import camerax.core.tools.CameraUtil
import com.camerax.usecase.databinding.CaseImageAnalysisCaptureBinding
import kotlin.math.absoluteValue


/**
 * by JFZ
 * 2025/4/27
 * desc：拍照控制
 **/
class CaptureControllerCase(private val listener: Callback) : ControllerCase() {


    private val binding: CaseImageAnalysisCaptureBinding by lazy {
        CaseImageAnalysisCaptureBinding.inflate(LayoutInflater.from(mContext))
    }

    private var recording: Recording? = null


    @SuppressLint("MissingPermission")
    override fun onCaseCreated() {
        super.onCaseCreated()
        removeView(binding.root)
        addView(
            binding.root, RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        )

//        mCameraView.cameraController?.setEnabledUseCases(CameraController.VIDEO_CAPTURE)
        binding.takePicture.setOnClickListener {
            val mainExecutor = ContextCompat.getMainExecutor(mContext)

            mCameraView.cameraController?.takePicture(
                mainExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bmp = CameraUtil.rotateBitmap(
                            image.toBitmap(),
                            image.imageInfo.rotationDegrees
                        )
                        post {
                            listener.onImage(bmp)
                        }
                        image.close()

                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                        post {
                            Toast.makeText(
                                mContext,
                                "error:" + exception.imageCaptureError + "," + exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })

//            val contentValues = ContentValues()
//            contentValues.put(
//                MediaStore.MediaColumns.DISPLAY_NAME,
//                "IMAGE_" + System.currentTimeMillis()
//            )
//            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            val options = ImageCapture.OutputFileOptions.Builder(
//                mContext.contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues
//            ).build()
//            mCameraView.cameraController?.takePicture(
//                options,
//                mainExecutor,
//                object : ImageCapture.OnImageSavedCallback {
//                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                        log("onImageSaved:" + outputFileResults.savedUri)
//
//                        outputFileResults.savedUri?.let {
//                            log(
//                                "onImageSaved:" + getRealPathFromUri(
//                                    it
//                                )
//                            )
//                            post {
//                                listener.onImage(it)
//                            }
//                        }
//                        binding.image.setImageURI(outputFileResults.savedUri)
//
//                    }
//
//                    override fun onError(exception: ImageCaptureException) {
//                        log("onError:" + exception.imageCaptureError + "," + exception.message)
//                        post {
//                            Toast.makeText(
//                                mContext,
//                                "error:" + exception.imageCaptureError + "," + exception.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                })


//            val contentValues = ContentValues()
//            contentValues.put(
//                MediaStore.MediaColumns.DISPLAY_NAME,
//                "VIDEO_" + System.currentTimeMillis()
//            )
//            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
//            val options = MediaStoreOutputOptions.Builder(
//                mContext.contentResolver,
//                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//            ).setContentValues(contentValues).build()
//
//            recording = mCameraView.cameraController?.startRecording(
//                options,
//                AudioConfig.create(true),
//                mainExecutor,
//                object : Consumer<VideoRecordEvent> {
//                    override fun accept(event: VideoRecordEvent) {
//                        if (event is VideoRecordEvent.Finalize) {
//                            log(
//                                "VideoRecordEvent:" + event.error + ",\n" + event.outputResults.outputUri + ",\n" + getRealPathFromUri(
//                                    event.outputResults.outputUri
//                                )
//                            )
//                        } else if (event is VideoRecordEvent.Status) {
//                        }
//                    }
//                })
        }

        binding.takeRecordStop.setOnClickListener {
            recording?.stop()
        }

        mPreviewView.previewStreamState.observe(mCameraView.lifecycleOwner) { state ->
            binding.text.append("\n")
            binding.text.append("state:${state.name}")
//            when (state) {
//                PreviewView.StreamState.IDLE -> {
//                    // 没有预览
//                }
//
//                PreviewView.StreamState.STREAMING -> {
//                    // ✅ 预览真正开始（图像可见）
//                    Log.d("Camera", "预览已启动")
//                }
//            }
        }

        val cameraInfo = mCameraView.cameraController?.cameraInfo

        cameraInfo?.cameraState?.observe(mCameraView.lifecycleOwner) { cameraState ->
//            when (cameraState.type) {
//                CameraState.Type.OPENING -> Log.d("Camera", "正在打开摄像头")
//                CameraState.Type.OPEN -> Log.d("Camera", "摄像头已打开")
//                CameraState.Type.PENDING_OPEN -> Log.d("Camera", "等待打开摄像头")
//                CameraState.Type.CLOSED -> Log.d("Camera", "摄像头已关闭")
//            }
//
//            cameraState.error?.let { error ->
//                Log.e("Camera", "Camera error: ${error.code}")
//            }

            binding.text.append("\n")
            binding.text.append("info:")
            binding.text.append(cameraState.type.name)

            cameraState.error?.let { error ->
                binding.text.append("\n")
                binding.text.append("info_err:")
                binding.text.append("${error.code}")
            }
        }


    }


    interface Callback {
        fun onImage(bitmap: Bitmap)
    }

    private fun getRealPathFromUri(uri: Uri): String {
        var realPath = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = mContext.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                realPath = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return realPath
    }


    override fun getCaseId() = "CaptureControllerCase".hashCode().absoluteValue


}