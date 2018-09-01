package com.example.vincent.imagedemo;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageViewValue imageViewValue;
    private EditText etInput;
    private Random random = new Random();
    private MyView myView;

    private WhiteDegreeImageView whiteDegreeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView1();
        initView2();
        initMyView();
    }

    private void initMyView() {
        myView = findViewById(R.id.myView);
    }

    private void initView2() {
        whiteDegreeImageView = findViewById(R.id.wdiv_imageview);
//        whiteDegreeImageView.setAngle(5);

    }

    private void initView1() {
        imageViewValue = findViewById(R.id.imageViewValue);
        etInput = findViewById(R.id.et_input);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float value = 50;
                String numStr = etInput.getText().toString();
                if(!TextUtils.isEmpty(numStr)){
                    value = Float.valueOf(numStr);
                }
//                imageViewValue.setCurrentValue(value);
                whiteDegreeImageView.setTargetAngle(value);
                myView.setTargetAngle(value);
            }
        });
    }
}
