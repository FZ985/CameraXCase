package com.camerax.usecase.usecase

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.PreviewView
import androidx.camera.view.TransformExperimental
import androidx.core.content.ContextCompat
import camerax.core.CameraCase
import camerax.usecase.CameraXView
import camerax.usecase.UseCase
import com.camerax.usecase.mlkit.QrCodeDrawable
import com.camerax.usecase.mlkit.QrCodeViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlin.math.absoluteValue


/**
 * by JFZ
 * 2025/4/26
 * desc：每一帧图像分析, 参考示例
 **/
class ImageAnalysisCaseKt : UseCase() {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    private val barcodeScanner = BarcodeScanning.getClient(options)

    private var imageAnalysis: ImageAnalysis? = null

    override fun <Case : CameraCase<CameraXView?>?> onCaseAttach(
        context: Context,
        cameraView: CameraXView,
        caseList: List<Case?>?
    ) {
        super.onCaseAttach(context, cameraView, caseList)
        imageAnalysis = ImageAnalysis.Builder().build()
    }

    @TransformExperimental
    override fun onCaseCreated() {
        val mlKitAnalyzer = MlKitAnalyzer(
            listOf(barcodeScanner),
            COORDINATE_SYSTEM_VIEW_REFERENCED,
            ContextCompat.getMainExecutor(mContext),
            { result: MlKitAnalyzer.Result? ->
                val barcodeResults = result?.getValue(barcodeScanner)
                if ((barcodeResults == null) ||
                    (barcodeResults.size == 0) ||
                    (barcodeResults.first() == null)
                ) {
                    mPreviewView.overlay.clear()
                    mPreviewView.setOnTouchListener { _, _ -> false } //no-op
                } else {
                    val qrCodeViewModel = QrCodeViewModel(barcodeResults[0])
                    val qrCodeDrawable = QrCodeDrawable(qrCodeViewModel)

                    mPreviewView.setOnTouchListener(qrCodeViewModel.qrCodeTouchCallback)
                    mPreviewView.overlay.clear()
                    mPreviewView.overlay.add(qrCodeDrawable)
                }
            }
        )
        imageAnalysis?.setAnalyzer(
            ContextCompat.getMainExecutor(mContext),
            mlKitAnalyzer
        )

        mPreviewView.previewStreamState.observe(mCameraView.lifecycleOwner) { state ->
            if (state == PreviewView.StreamState.STREAMING) {
                mlKitAnalyzer.updateTransform(mPreviewView.sensorToViewTransform)
            }
        }
    }


    override fun getCameraUseCase(): List<androidx.camera.core.UseCase?>? {
        return listOf(imageAnalysis)
    }

    override fun getCaseId() = "ImageAnalysisCaseKt".hashCode().absoluteValue
}