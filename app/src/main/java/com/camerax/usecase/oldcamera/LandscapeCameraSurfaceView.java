package com.camerax.usecase.oldcamera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import camerax.core.tools.CameraUtil;

/**
 * A basic Camera preview class
 * by JFZ
 * 2025/2/14
 * desc：旧版的横向相机使用
 **/
public class LandscapeCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = "";

    //没有相机
    public static final int NO_CAMERA = -1000;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera mPreviewCamera;
    private int mCameraId;
    private boolean isCameraPreview = false;
    private int newRotation;
    private int rotationDegrees;
    private OrientationEventListener orientationEventListener;

    private byte[] currentFrame;

    private boolean isStartCapture = false;

    private float aspectRatio = 0f;

    private int mCurrentFacing = Camera.CameraInfo.CAMERA_FACING_BACK;

    private OnCameraCallback cameraCallback;

    public LandscapeCameraSurfaceView(Context context) {
        super(context);
    }

    public void init() {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        openCamera(false);
        orientationEventListener = new OrientationEventListener(getContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) return;
                // 将角度映射为 Surface 旋转常量
                if (orientation >= 315 || orientation < 45) {
                    newRotation = Surface.ROTATION_0;
                } else if (orientation >= 45 && orientation < 135) {
                    newRotation = Surface.ROTATION_270;
                } else if (orientation >= 135 && orientation < 225) {
                    newRotation = Surface.ROTATION_180;
                } else {
                    newRotation = Surface.ROTATION_90;
                }
                updateDegrees(newRotation);
//                Log.e("OrientationHelper","orientation:" + orientation + ",newRotation:" + newRotation);
            }
        };
        orientationEventListener.enable();
        mHolder.addCallback(this);
    }

    private Camera getCameraInstance(int facing) {
        Camera c = null;
        try {
            int numberOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == facing) {
                    mCameraId = i;
                    c = Camera.open(i);
                }
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public Camera openCamera(boolean isFront) {
        if (isFront) {
            mCurrentFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCurrentFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        return openCamera(mCurrentFacing);
    }

    public Camera openCamera(int facing) {
        this.mCurrentFacing = facing;
        releaseCamera();
        mCamera = getCameraInstance(facing);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        newRotation = wm.getDefaultDisplay().getRotation();
        updateDegrees(newRotation);
        updateCameraRotation();
        currentFrame = null;
        isCameraPreview = false;
        if (mCamera != null) {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (!isCameraPreview) {
                        if (cameraCallback != null) {
                            cameraCallback.onPreview();
                        }
                    }
                    isCameraPreview = true;
                    mPreviewCamera = camera;
                    if (currentFrame == null && isStartCapture) {
                        currentFrame = data;
                        isStartCapture = false;
                    }
                }
            });
            mCamera.setErrorCallback(new Camera.ErrorCallback() {
                @Override
                public void onError(int error, Camera camera) {
                    Log.e(TAG, "相机失败code:" + error);
                    Toast.makeText(getContext(), "相机启动失败：" + error + ",请重新启动", Toast.LENGTH_SHORT).show();
//                    Camera.CAMERA_ERROR_UNKNOWN：未知错误，通常表示相机的硬件或软件出现了问题。
//                    Camera.CAMERA_ERROR_SERVER_DIED：相机服务进程崩溃或被终止，通常是由于系统问题导致相机无法正常工作。
                    if (cameraCallback != null) {
                        cameraCallback.onError(error, "");
                    }
                }
            });

            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        } else {
            if (cameraCallback != null) {
                cameraCallback.onError(NO_CAMERA, "not facing camera");
            }
        }
        return mCamera;
    }

    private void updateDegrees(int rotation) {
        if (rotation == Surface.ROTATION_0) {
            rotationDegrees = 0;
        } else if (rotation == Surface.ROTATION_270) {
            rotationDegrees = 270;
        } else if (rotation == Surface.ROTATION_180) {
            rotationDegrees = 180;
        } else {
            rotationDegrees = 90;
        }
    }

    private void updateCameraRotation() {
        if (mCamera != null) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, info);
            // 计算最终角度
            int result;
            int orientation = OldCameraUtil.compatOrientation(info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (orientation + rotationDegrees) % 360;
                result = (360 - result) % 360; // 镜像
                mCamera.setDisplayOrientation(result);
            } else { // back-facing
                result = (orientation - rotationDegrees + 360) % 360;
                mCamera.setDisplayOrientation(result);
            }

        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        if (cameraCallback != null) {
            cameraCallback.onSurfaceCreated();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void setAspectRatio(int width, int height) {
        if (width > 0 && height > 0) {
            aspectRatio = (float) width / (float) height;
            mHolder.setFixedSize(width, height);
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (aspectRatio == 0f) {
            setMeasuredDimension(width, height);
        } else {
            // Performs center-crop transformation of the camera frames
            int newWidth;
            int newHeight;
            float actualRatio = (width > height) ? aspectRatio : 1f / aspectRatio;
            if (width < height * actualRatio) {
                newHeight = height;
                newWidth = Math.round(height * actualRatio);
            } else {
                newWidth = width;
                newHeight = Math.round(width / actualRatio);
            }

//            Log.d(TAG, "Measured dimensions set: $newWidth x $newHeight")
            setMeasuredDimension(newWidth, newHeight);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        newRotation = wm.getDefaultDisplay().getRotation();
        updateDegrees(newRotation);
        updateCameraRotation();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        if (mCamera == null) {
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters != null) {
                // 获取支持的拍照尺寸列表
                List<Camera.Size> supportedSizes = parameters.getSupportedPictureSizes();
                if (supportedSizes != null && supportedSizes.size() > 0) {
                    // 选择一个适当的尺寸
//                    Camera.Size optimalSize = getMaxSize(supportedSizes);
                    Camera.Size optimalSize = getOptimalPreviewSize(supportedSizes, getWidth(), getHeight());

                    // 设置拍照尺寸
                    parameters.setPictureSize(optimalSize.width, optimalSize.height);
                    parameters.setPreviewSize(optimalSize.width, optimalSize.height);
                }
                mCamera.setParameters(parameters);
            }
        } catch (Exception e) {
        }

        // 将设置应用到 Camera 对象
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Error starting camera preview: " + e.getMessage());
        }

    }

    private Camera.Size getMaxSize(List<Camera.Size> sizes) {
        Camera.Size maxSize = null;
        for (Camera.Size size : sizes) {
            if (maxSize == null || (size.width * size.height > maxSize.width * maxSize.height)) {
                maxSize = size;
            }
        }
        if (maxSize != null) {
            return maxSize;
        }
        return sizes.get(0);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        int previewRatio = Math.max(width, height) / Math.min(width, height);
        double RATIO_4_3_VALUE = 4.0 / 3.0;
        double RATIO_16_9_VALUE = 16.0 / 9.0;
        double targetRatio;
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            targetRatio = RATIO_4_3_VALUE;
        } else {
            targetRatio = RATIO_16_9_VALUE;
        }
        final double ASPECT_TOLERANCE = 0.05; // 宽高比容忍误差
        Camera.Size bestSize = null;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;

            // 优先选择分辨率大的
            if (bestSize == null || size.width * size.height > bestSize.width * bestSize.height) {
                bestSize = size;
            }
        }
        Log.e("size", "size:" + bestSize.width + "," + bestSize.height);
        return bestSize;
    }


    public void takePicture(TakePictureCallback callback) {
        currentFrame = null;
        if (mCamera != null) {
            isStartCapture = true;
            if (isCameraPreview) {
                try {
                    mCamera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            if (callback != null) {
                                callback.onTakePicture(camera, buildImageInfo(data, false, camera));
                            }
                            isStartCapture = false;
                            currentFrame = null;
                            camera.startPreview();
                        }
                    });
                } catch (RuntimeException e) {
                    Log.e(TAG, "拍照失败");
                    postDelayed(() -> {
                        if (callback != null && currentFrame != null && currentFrame.length > 0) {
                            callback.onTakePicture(mPreviewCamera, buildImageInfo(currentFrame, true, mPreviewCamera));
                        }
                    }, 250);
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "相机未预览");
                postDelayed(() -> {
                    if (callback != null && currentFrame != null && currentFrame.length > 0) {
                        callback.onTakePicture(mPreviewCamera, buildImageInfo(currentFrame, true, mPreviewCamera));
                    }
                }, 250);
            }
        }
    }

    private ImageInfo buildImageInfo(byte[] data, boolean isYuv, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);

        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setData(data);
        imageInfo.setYuv(isYuv);

        int degrees = OldCameraUtil.getOldRotationDegrees(getContext(), mCameraId);
        imageInfo.setDegrees(degrees);

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (isYuv) {
            bitmap = yuvToJpeg(data, camera);
        }

        int orientation = getContext().getResources().getConfiguration().orientation;
        imageInfo.setPortrait(orientation == Configuration.ORIENTATION_PORTRAIT);

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            imageInfo.setFront(true);
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                bitmap = CameraUtil.flipBitmapVertically(bitmap);
            }
        } else {
            imageInfo.setFront(false);
        }
        imageInfo.setBitmap(bitmap);
        return imageInfo;
    }

    public void takePicture2(TakePictureCallback2 callback) {
        currentFrame = null;
        if (mCamera != null && isCameraPreview) {
            isStartCapture = true;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                    HandlerThread handlerThread = new HandlerThread("PixelCopier");
                    handlerThread.start();
                    PixelCopy.request(this, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
                                @Override
                                public void onPixelCopyFinished(int copyResult) {
                                    if (callback != null) {
                                        if (copyResult == PixelCopy.SUCCESS) {
                                            callback.onTakePicture(buildImageInfo2(bitmap, false));
                                        } else {
                                            postDelayed(() -> {
                                                if (currentFrame != null && currentFrame.length > 0 && mPreviewCamera != null) {
                                                    callback.onTakePicture(buildImageInfo2(yuvToJpeg(currentFrame, mPreviewCamera), true));
                                                }
                                            }, 250);
                                        }
                                    }
                                    handlerThread.quitSafely();
                                }
                            }, new Handler()
                    );
                } else {
                    Log.e(
                            "tag",
                            "版本 Build.VERSION.SDK_INT < Build.VERSION_CODES.N not support taskShotPicture now"
                    );
                    postDelayed(() -> {
                        if (currentFrame != null && currentFrame.length > 0 && mPreviewCamera != null) {
                            callback.onTakePicture(buildImageInfo2(yuvToJpeg(currentFrame, mPreviewCamera), true));
                        }
                    }, 250);
                }
            } catch (Exception e) {
                e.printStackTrace();
                postDelayed(() -> {
                    if (currentFrame != null && currentFrame.length > 0 && mPreviewCamera != null) {
                        callback.onTakePicture(buildImageInfo2(yuvToJpeg(currentFrame, mPreviewCamera), true));
                    }
                }, 250);
            }
        }
    }

    private ImageInfo2 buildImageInfo2(Bitmap bitmap, boolean isYuv) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);

        ImageInfo2 imageInfo = new ImageInfo2();
        imageInfo.setYuv(isYuv);

        int degrees = OldCameraUtil.getOldRotationDegrees(getContext(), mCameraId);
        imageInfo.setDegrees(degrees);

        int orientation = getContext().getResources().getConfiguration().orientation;
        imageInfo.setPortrait(orientation == Configuration.ORIENTATION_PORTRAIT);

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            imageInfo.setFront(true);
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                bitmap = CameraUtil.flipBitmapVertically(bitmap);
            }
        } else {
            imageInfo.setFront(false);
        }
        imageInfo.setBitmap(bitmap);
        return imageInfo;
    }


    private Bitmap yuvToJpeg(byte[] data, Camera camera) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
        // 输出到 JPEG
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean success = yuvImage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, out);
        if (success) {
            byte[] jpegData = out.toByteArray();
            return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
        } else {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (orientationEventListener != null) {
            orientationEventListener.disable();
        }
        super.onDetachedFromWindow();
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            currentFrame = null;
            isStartCapture = false;
            isCameraPreview = false;
            cameraCallback = null;
        }
    }

    public void setCameraCallback(OnCameraCallback cameraCallback) {
        this.cameraCallback = cameraCallback;
    }

    public int getCameraId() {
        return mCameraId;
    }
}
