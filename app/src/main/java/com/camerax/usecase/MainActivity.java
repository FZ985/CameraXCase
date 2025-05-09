package com.camerax.usecase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    public static Bitmap bitmap = null;
    public static Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hasPermission();
    }

    @SuppressLint("NonConstantResourceId")
    public void openCamera(View view) {
        if (!hasPermission()) return;
        int id = view.getId();
        if (id == R.id.openCamera) {
            start(CameraXActivity.class, 0);
        } else if (id == R.id.frontCamera) {
            start(CameraXActivity.class, 1);
        } else if (id == R.id.backCamera) {
            start(CameraXActivity.class, 2);
        } else if (id == R.id.analysis) {
            start(CameraXActivity.class, 3);
        } else if (id == R.id.capture) {
            startActivityForResult(new Intent(this, ImageAnalysisCaptureActivity.class), 100);
        } else if (id == R.id.camerax_test) {
            startActivityForResult(new Intent(this, CameraXTestActivity.class), 100);
        } else if (id == R.id.controller_capture) {
            if (!hasAudioPermission()) return;
            startActivityForResult(new Intent(this, ControllerCaptureActivity.class), 100);
//            startActivityForResult(new Intent(this, ControllerCaptureActivity.class), 101);
        } else if (id == R.id.old_camera) {
            startActivityForResult(new Intent(this, OldCameraActivity.class), 100);
//            startActivityForResult(new Intent(this, ControllerCaptureActivity.class), 101);
        }
    }

    private void start(Class<?> cls, int type) {
        startActivityForResult(new Intent(this, cls).putExtra("type", type), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100
                && resultCode == Activity.RESULT_OK) {
            if (bitmap != null) {
                ImageView iv = findViewById(R.id.image);
                iv.setImageBitmap(bitmap);
            }
        }

        if (requestCode == 101
                && resultCode == Activity.RESULT_OK) {
            if (uri != null) {
                ImageView iv = findViewById(R.id.image);
                iv.setImageURI(uri);
            }
        }
    }

    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA,
            }, 100);
            return false;
        }
        return true;
    }

    private boolean hasAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO,
            }, 100);
            return false;
        }
        return true;
    }
}