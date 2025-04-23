package com.camerax.usecase;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.camerax.usecase.usecase.ImageAnalysisCase;
import com.camerax.usecase.usecase.LensFacingCase;

import camerax.usecase.CameraXView;
import camerax.usecase.UseCase;
import usecase.impl.CaptureCase;
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

        int type = getIntent().getIntExtra("type", 0);
        UseCase[] group = null;
        if (type == 0) {
            group = new UseCase[]{
                    new LensFacingCase()
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
            group = new UseCase[]{new ImageAnalysisCase()};
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
