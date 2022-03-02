package com.example.whatsappclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.whatsappclone.R;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    // controls
    Button btnGoToSignIn, btnGoToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).hide();

        init();
    }

    // this method will set references of all the controls.
    private void init() {
        btnGoToSignIn = findViewById(R.id.btn_go_to_sign_in_activity);
        btnGoToSignUp = findViewById(R.id.btn_go_to_sign_up_activity);
        btnGoToSignUp.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
            finish();
        });
        btnGoToSignIn.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, SignInActivity.class));
            finish();
        });
    }
}