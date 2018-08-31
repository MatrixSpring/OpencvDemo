package com.epbox.opencv4android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

public class MainActivityB extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        final TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivityB.this, "You clicked textView!", Toast.LENGTH_SHORT).show();
                if (OpenCVLoader.initDebug()) {
                    textView.setText("OpenCV loaded successful!");
                }
            }
        });
    }

    private void openCVFunction(){

    }
}

