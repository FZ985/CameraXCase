package com.camerax.usecase.oldcamera

import android.graphics.Bitmap
import android.hardware.Camera
import java.io.Serializable


/**
 * by JFZ
 * 2025/5/9
 * desc：
 **/

interface TakePictureCallback {
    fun onTakePicture(camera: Camera, image: ImageInfo)
}

class ImageInfo : Serializable {
    var data: ByteArray = byteArrayOf()

    var isYuvToBitmap: Boolean = false

    var isFront: Boolean = false

    var degrees: Int = 0

    var isPortrait: Boolean = false

    var bitmap: Bitmap? = null
}

interface OnCameraCallback {

    fun onPreview()

    fun onSurfaceCreated()

    fun onError(error: Int, msg: String)
}

