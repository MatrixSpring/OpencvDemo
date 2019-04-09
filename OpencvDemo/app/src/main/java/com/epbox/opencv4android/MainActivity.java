package com.epbox.opencv4android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.epbox.opencv4android.ui.RecognitionImage;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RecognitionImage";
    private TextView tv_face;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_face = (TextView) findViewById(R.id.text_view_face_recognition);

        tv_face.setOnClickListener(this);
    }

    private void openCVFunction(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_view_face_recognition:{
                Intent intentFace = new Intent(this,RecognitionImage.class);
                startActivity(intentFace);
            } break;
            case R.id.text_view_1:{

            } break;
        }
    }
}

