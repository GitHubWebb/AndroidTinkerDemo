package com.wwx.androidtinkerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wwx.androidtinkerdemo.tinkersdk.config.TinkerHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        TinkerHelper
                .getInstance()
                .checkHotFixUp();
    }
}