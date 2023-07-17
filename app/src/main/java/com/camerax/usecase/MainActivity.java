package com.camerax.usecase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

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
        switch (id) {
            case R.id.openCamera:
                start(CameraXActivity.class, 0);
                break;
            case R.id.frontCamera:
                start(CameraXActivity.class, 1);
                break;
            case R.id.backCamera:
                start(CameraXActivity.class, 2);
                break;
        }
    }

    private void start(Class<?> cls, int type) {
        startActivity(new Intent(this, cls).putExtra("type", type));
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
}