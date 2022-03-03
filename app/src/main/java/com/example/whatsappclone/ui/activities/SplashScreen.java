package com.example.whatsappclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.whatsappclone.R;
import com.example.whatsappclone.utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();
        init();
    }
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        // checking is internet available or not.
        NetworkManager.checkNetworkConnectedStatus(SplashScreen.this);
        new Handler().postDelayed(() -> {
            // if user is loggedIn then we navigate to MainActivity
            if(firebaseAuth.getCurrentUser() != null) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }else {
                startActivity(new Intent(SplashScreen.this, HomeActivity.class));
            }
            finish();
        }, 1000);
    }
}