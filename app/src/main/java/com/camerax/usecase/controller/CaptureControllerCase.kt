package com.camerax.usecase.controller

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.video.AudioConfig
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import camerax.controller.ControllerCase
import com.camerax.usecase.databinding.CaseImageAnalysisCaptureBinding
import kotlin.math.absoluteValue


/**
 * by JFZ
 * 2025/4/27
 * desc：拍照控制
 **/
class CaptureControllerCase : ControllerCase() {


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

        mCameraView.cameraController?.setEnabledUseCases(CameraController.VIDEO_CAPTURE)
        binding.takePicture.setOnClickListener {
            val mainExecutor = ContextCompat.getMainExecutor(mContext)
//
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
//
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
//                        }
//                        binding.image.setImageURI(outputFileResults.savedUri)
//                    }
//
//                    override fun onError(exception: ImageCaptureException) {
//                        log("onError:" + exception.imageCaptureError + "," + exception.message)
//                    }
//                })


            val contentValues = ContentValues()
            contentValues.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                "VIDEO_" + System.currentTimeMillis()
            )
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            val options = MediaStoreOutputOptions.Builder(
                mContext.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            ).setContentValues(contentValues).build()

            recording = mCameraView.cameraController?.startRecording(
                options,
                AudioConfig.create(true),
                mainExecutor,
                object : Consumer<VideoRecordEvent> {
                    override fun accept(event: VideoRecordEvent) {
                        if (event is VideoRecordEvent.Finalize) {
                            log(
                                "VideoRecordEvent:" + event.error + ",\n" + event.outputResults.outputUri + ",\n" + getRealPathFromUri(
                                    event.outputResults.outputUri
                                )
                            )
                        } else if (event is VideoRecordEvent.Status) {
                        }
                    }
                })
        }

        binding.takeRecordStop.setOnClickListener {
            recording?.stop()
        }
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