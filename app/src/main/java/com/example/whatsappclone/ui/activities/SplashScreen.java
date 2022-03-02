package com.example.whatsappclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.whatsappclone.R;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();
        init();
    }
    private void init() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, HomeActivity.class));
            finish();
        }, 1000);
    }
}