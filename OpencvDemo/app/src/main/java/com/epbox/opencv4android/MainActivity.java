package com.epbox.opencv4android;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.epbox.opencv4android.permission.PermissionListener;
import com.epbox.opencv4android.permission.PermissionUtil;
import com.epbox.opencv4android.ui.FaceRecognitionActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "FaceRecognitionActivity";
    private TextView tv_face;
    private PermissionUtil permissionUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_face = (TextView) findViewById(R.id.text_view_face_recognition);

        tv_face.setOnClickListener(this);
        permissionUtil = new PermissionUtil(MainActivity.this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_view_face_recognition:{
                permissionUtil.requestPermissions(new String[]{Manifest.permission.CAMERA}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        Intent intentFace = new Intent(MainActivity.this,FaceRecognitionActivity.class);
                        startActivity(intentFace);
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {

                    }

                    @Override
                    public void onShouldShowRationale(List<String> deniedPermission) {

                    }
                });

            } break;
            case R.id.text_view_1:{

            } break;
        }
    }

}

