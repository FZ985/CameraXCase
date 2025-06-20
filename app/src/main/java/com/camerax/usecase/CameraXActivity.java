package com.camerax.usecase;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.camerax.usecase.usecase.ImageAnalysisCaseKt;
import com.camerax.usecase.usecase.LensFacingCase;

import camerax.core.tools.CameraUtil;
import camerax.usecase.CameraXView;
import camerax.usecase.UseCase;
import usecase.impl.CaptureCase;
import usecase.impl.CaptureResult;
import usecase.impl.FocusCase;
import usecase.impl.IdCardBackCase;
import usecase.impl.IdCardFrontCase;
import usecase.impl.LayerCase;
import usecase.impl.PreviewResultCase;

/**
 * author : JFZ
 * date : 2023/7/15 16:57
 * description :
 */
public class CameraXActivity extends AppCompatActivity implements PreviewResultCase.PreviewConfirmListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        CameraXView camera = findViewById(R.id.camera);
        ImageView image = findViewById(R.id.image);
        CaptureCase captureCase = new CaptureCase();
        captureCase.setCaptureListener(new CaptureCase.CaptureListener() {
            @Override
            public void onCapture(CaptureResult result) {
                Bitmap bitmap = result.getBitmap();
                if (bitmap != null) {
                    image.setImageBitmap(CameraUtil.rotateBitmap(bitmap, result.getRotationDegrees()));
                } else {
                    if (result.getException() != null) {
                        Toast.makeText(CameraXActivity.this, result.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        int type = getIntent().getIntExtra("type", 0);
        UseCase[] group = null;
        if (type == 0) {
            group = new UseCase[]{
                    new LensFacingCase(),
                    captureCase
            };
        } else if (type == 1) {
            group = new UseCase[]{
                    new FocusCase(),
                    new LayerCase(),
                    new IdCardFrontCase(),
                    new CaptureCase(),
                    new PreviewResultCase(this)
            };
        } else if (type == 2) {
            group = new UseCase[]{
                    new FocusCase(),
                    new LayerCase(),
                    new IdCardBackCase(),
                    new CaptureCase(),
                    new PreviewResultCase(this)
            };
        } else if (type == 3) {
            group = new UseCase[]{new ImageAnalysisCaseKt()};
        }

        camera.preview(this, () -> {
        }, group);
    }

    @Override
    public void onConfirm(Bitmap originalBitmap, Bitmap cropBitmap, RectF rect, int width, int height) {
//        ImageView image = findViewById(R.id.image);
//        image.setImageBitmap(cropBitmap);

//        MainActivity.bitmap = originalBitmap;
        MainActivity.bitmap = cropBitmap;
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }
}
