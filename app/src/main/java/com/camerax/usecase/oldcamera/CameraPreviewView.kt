package com.camerax.usecase.oldcamera

import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.hardware.camera2.CameraManager
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.widget.RelativeLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


/**
 * by JFZ
 * 2025/5/9
 * desc：
 **/
class CameraPreviewView : RelativeLayout {

    private val cameraView: LandscapeCameraSurfaceView by lazy {
        LandscapeCameraSurfaceView(context)
    }

    private val observer = object : LifecycleEventObserver {
        override fun onStateChanged(
            source: LifecycleOwner,
            event: Lifecycle.Event
        ) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                cameraView.releaseCamera()
            }
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun bindToLifecycle(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(observer)
        owner.lifecycle.addObserver(observer)
        removeAllViews()
        addView(
            cameraView,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )
        cameraView.init()
    }

    fun openCamera(isFront: Boolean) {
        cameraView.openCamera(isFront)
    }

    fun setAspectRatio(width: Int, height: Int) {
        cameraView.setAspectRatio(width, height)
    }

    fun takePicture(callback: TakePictureCallback) {
        cameraView.takePicture(callback)
    }

    fun takePicture2(callback: TakePictureCallback2) {
        cameraView.takePicture2(callback)
    }

    fun getCameraId(): Int {
        return cameraView.cameraId
    }

    fun setCameraCallback(cameraCallback: OnCameraCallback) {
        cameraView.setCameraCallback(cameraCallback)
    }

    fun autoFitSize() {
        try {
            val cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager
            val characteristics =
                cameraManager.getCameraCharacteristics(getCameraId().toString())
            val previewSize = getPreviewOutputSize(
                display,
                characteristics,
                SurfaceHolder::class.java
            )
            Log.e("previewSize", "previewSize:${previewSize.width},${previewSize.height}")
            cameraView.setAspectRatio(previewSize.width, previewSize.height)
        } catch (e: Exception) {
            Log.e("fitSize", "fitSize Error:" + e.message)
        }
    }
}