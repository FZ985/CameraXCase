package usecase.impl;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.UseCase;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.core.resolutionselector.ResolutionStrategy;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import camerax.core.CameraCase;
import camerax.usecase.CameraXView;

/**
 * author : JFZ
 * date : 2023/7/17 15:10
 * description : 拍照
 */
public class CaptureCase extends BaseUseCase {

    public static final int ID = "CaptureCase".hashCode();

    public static final int EVENT_TAKE_PICTURE = "TakePictureEvent".hashCode();
    public static final int EVENT_TAKE_PICTURE_ERROR = "TakePictureErrorEvent".hashCode();

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int radius;
    private int outsideRadius;

    private int insideRadius;

    private float x, y;

    private final RectF rectF = new RectF();

    private int insideColor = Color.WHITE;

    private ImageCapture imageCapture;

    private boolean isCapturing = false;
    private final List<CaptureListener> captureListeners = new ArrayList<>();

    @Override
    public <Case extends CameraCase<CameraXView>> void onCaseAttach(@NonNull Context context, @NonNull CameraXView cameraView, List<Case> cases) {
        super.onCaseAttach(context, cameraView, cases);
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(cameraView.getFlashMode())
                .setJpegQuality(100)
//                .setResolutionSelector(new ResolutionSelector.Builder()
//                        .setAspectRatioStrategy(CameraUtil.aspectRatioStrategy(cameraView.getWidth(), cameraView.getHeight()))
//                        .build())
                .setResolutionSelector(
                        new ResolutionSelector.Builder()
                                .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                                .build()
                )
                .setTargetRotation(cameraView.getDisplay().getRotation())
                .build();
    }

    @Override
    public void onCaseCreated() {
        outsideRadius = dp2px(45);
        radius = dp2px(35);
        x = (float) mWidth / 2;
        y = mHeight - dp2px(40) - outsideRadius;
        paint.setStyle(Paint.Style.FILL);
        insideRadius = radius;
        rectF.set(x - outsideRadius, y - outsideRadius, x + outsideRadius, y + outsideRadius);
    }

    @Override
    public void onCameraNotify(CameraXView cameraView) {
        if (imageCapture != null) {
            imageCapture.setFlashMode(cameraView.getFlashMode());
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(0xcc444444);
        canvas.drawCircle(x, y, outsideRadius, paint);
        paint.setColor(insideColor);
        canvas.drawCircle(x, y, insideRadius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        boolean contains = rectF.contains(touchX, touchY);
        if (contains) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                insideColor = Color.argb(100, 255, 255, 255);
                invalidateCase();
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                insideColor = Color.WHITE;
                onTap();
            }
            return true;
        } else {
            insideColor = Color.WHITE;
            invalidateCase();
        }
        return false;
    }

    private void onTap() {
        if (mCameraCaseView != null) {
            mCameraCaseView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        ValueAnimator anim = ValueAnimator.ofInt(radius, (int) (radius * 0.9f), radius);
        anim.setDuration(300);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(animation -> {
            insideRadius = (int) animation.getAnimatedValue();
            invalidateCase();
            if (animation.getCurrentPlayTime() >= animation.getDuration()) {
                takePicture();
            }
        });
        anim.start();
    }

    public void takePicture() {
        //避免重复点击
        if (isCapturing) return;
        log("开始拍照===");
        if (imageCapture != null) {
            isCapturing = true;
            imageCapture.takePicture(ContextCompat.getMainExecutor(mContext), new ImageCapture.OnImageCapturedCallback() {
                @androidx.camera.core.ExperimentalGetImage
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy proxy) {
//                    ByteBuffer buffer = proxy.getPlanes()[0].getBuffer();
//                    byte[] bytes = new byte[buffer.remaining()];
//                    buffer.get(bytes);
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    int rotationDegrees = proxy.getImageInfo().getRotationDegrees();
                    isCapturing = false;
                    Bitmap bitmap = proxy.toBitmap();
                    handler.post(() -> {
                        for (CameraCase<CameraXView> ca : getOtherGroupCase()) {
                            if (ca != null) {
                                ca.postData(EVENT_TAKE_PICTURE, new CaptureResult(bitmap, rotationDegrees));
                            }
                        }
                        for (CaptureListener listener : captureListeners) {
                            if (listener != null) {
                                listener.onCapture(new CaptureResult(bitmap, rotationDegrees));
                            }
                        }
                    });
                    proxy.close();
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    isCapturing = false;
                    log("拍照失败：" + exception.getImageCaptureError() + "," + exception.getMessage());
                    handler.post(() -> {
                        for (CaptureListener listener : captureListeners) {
                            if (listener != null) {
                                listener.onCapture(new CaptureResult(exception));
                            }
                        }
                        for (CameraCase<CameraXView> uc : getOtherGroupCase()) {
                            if (uc != null) {
                                uc.postData(EVENT_TAKE_PICTURE_ERROR, exception);
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        isCapturing = false;
        captureListeners.clear();
        super.onDestroy();
    }

    @Nullable
    @Override
    public List<UseCase> getCameraUseCase() {
        List<UseCase> list = new ArrayList<>();
        list.add(imageCapture);
        return list;
    }

    @Override
    public int getCaseId() {
        return ID;
    }

    public void setCaptureListener(CaptureListener captureListener) {
        if (!captureListeners.contains(captureListener)) {
            captureListeners.add(captureListener);
        }
    }

    public interface CaptureListener {
        void onCapture(CaptureResult result);
    }

}
