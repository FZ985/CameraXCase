package camerax.usecase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/7/15 17:11
 * description :相机控件
 */
public class CameraXView extends RelativeLayout {
    private final String tag = "CameraView";

    private PreviewView cameraView;//相机预览容器
    private ProcessCameraProvider cameraProvider;
    private ImageCapture imageCapture;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public static final int EVENT_TAKE_PICTURE = "takePictureEvent".hashCode();
    public static final int EVENT_TAKE_PICTURE_ERROR = "takePictureErrorEvent".hashCode();

    private final List<UseCase> caseGroup = new ArrayList<>();

    private UseCase.CaseDataObserver innerObserver = new UseCase.CaseDataObserver() {
        @Override
        public void onChanged(Object data) {
            if (data == null) return;
            if (data instanceof Integer) {
                int event = (int) data;
                if (event == EVENT_TAKE_PICTURE) {
                    takePicture();
                }
            }
        }
    };

    public CameraXView(@NonNull Context context) {
        this(context, null);
    }

    public CameraXView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraXView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        View view = View.inflate(getContext(), R.layout.camerax_view, this);
        cameraView = view.findViewById(R.id.camera_);
        UseCase useCase = new UseCase() {
            @Override
            public int getCaseId() {
                return 1;
            }
        };
        useCase.postData(1);
    }

    private void takePicture() {
        Log.e("CameraXView", "开始拍照===");
        if (imageCapture != null) {
            imageCapture.takePicture(ContextCompat.getMainExecutor(getContext()), new ImageCapture.OnImageCapturedCallback() {
                @androidx.camera.core.ExperimentalGetImage
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy proxy) {
//                    Image image = proxy.getImage();
                    ByteBuffer buffer = proxy.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    handler.post(() -> {
                        for (UseCase uc : caseGroup) {
                            uc.postData(bm);
                        }
                    });
                    proxy.close();
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    handler.post(() -> {
                        for (UseCase uc : caseGroup) {
                            uc.postData(EVENT_TAKE_PICTURE_ERROR);
                        }
                        Log.e("CameraXView", "拍照失败：" + exception.getMessage());
                    });
                }
            });
        }
    }

    public void preview(@NonNull LifecycleOwner lifecycleOwner, UseCase... useCases) {
        post(() -> {
            try {
                int width = getWidth();
                int height = getHeight();
                int screenAspectRatio = aspectRatio(width, height);
                int rotation = cameraView.getDisplay().getRotation();
                // CameraProvider
                cameraProvider = ProcessCameraProvider.getInstance(getContext()).get();

                // CameraSelector
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Preview
                Preview preview = new Preview.Builder()
                        .setTargetAspectRatio(screenAspectRatio)
                        .setTargetRotation(rotation)
                        .build();

                // ImageCapture
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setJpegQuality(100)
                        .setTargetAspectRatio(screenAspectRatio)
                        .setTargetRotation(rotation)
                        .build();

                // Must unbind the use-cases before rebinding them
                cameraProvider.unbindAll();

                // A variable number of use-cases can be passed here -
                // camera provides access to CameraControl & CameraInfo
                Camera camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture);
                // Attach the viewfinder's surface provider to preview use case
                preview.setSurfaceProvider(cameraView.getSurfaceProvider());
                //添加caseview
                if (useCases != null) {
                    caseGroup.clear();
                    caseGroup.addAll(Arrays.asList(useCases));
                    for (UseCase uc : caseGroup) {
                        RelativeLayout.LayoutParams params = new LayoutParams(width, height);
                        CaseView caseView = new CaseView(getContext(), uc);
                        caseView.setLayoutParams(params);
                        uc.onCreate(getContext(), this, cameraView, caseView, camera, width, height, caseGroup);
                        uc.registerCaseObserver(innerObserver);
                        addView(caseView, params);
                    }
                }
            } catch (Exception e) {
                Log.e(tag, "创建相机失败：" + e.getMessage());
            }
        });
    }

    private int aspectRatio(int width, int height) {
        int previewRatio = Math.max(width, height) / Math.min(width, height);
        double RATIO_4_3_VALUE = 4.0 / 3.0;
        double RATIO_16_9_VALUE = 16.0 / 9.0;
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            for (UseCase uc : caseGroup) {
                uc.unregisterCaseObserver(innerObserver);
                uc.onDestroy();
            }
            caseGroup.clear();
            innerObserver = null;
        } catch (Exception e) {
            Log.e("CameraXView", "销毁 catch:" + e.getMessage());
        }
        super.onDetachedFromWindow();
    }
}
