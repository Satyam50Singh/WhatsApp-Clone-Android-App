package com.example.whatsappclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.utils.Utils;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    TextView tvCreateAnAccount;
    Button btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Objects.requireNonNull(getSupportActionBar()).hide();

        init();
    }

    private void init() {
        tvCreateAnAccount = findViewById(R.id.tv_create_an_account);
        btnSignIn = findViewById(R.id.btn_sign_in);
        tvCreateAnAccount.setOnClickListener(view -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
        btnSignIn.setOnClickListener(view -> {
            Utils.showToastMessage(this, "Sign In Successfully");
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        });
    }
}