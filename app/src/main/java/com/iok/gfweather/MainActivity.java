package com.iok.gfweather;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.iok.gfweather.service.MyService;

public class MainActivity extends AppCompatActivity {

    Button mBtnStart;
    Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCtx = this;
        setContentView(R.layout.activity_main);

        mBtnStart = (Button)findViewById(R.id.btn_start);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(mCtx, MyService.class));
            }
        });
    }
}
