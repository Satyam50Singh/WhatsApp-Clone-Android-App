package com.example.whatsappclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    TextView tvAlreadyHaveAccount;
    Button btnSignUp;
    TextInputEditText etUsername, etEmail, etPassword;

    private String username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).hide();

        init();
    }

    private void init() {
        tvAlreadyHaveAccount = findViewById(R.id.tv_already_have_account);
        btnSignUp = findViewById(R.id.btn_sign_up);
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tvAlreadyHaveAccount.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, SignInActivity.class)));
        btnSignUp.setOnClickListener(view -> userSignUp());
    }

    private void userSignUp() {
        if (Utils.validateUsername(etUsername) && Utils.credentialsValidation(etEmail, etPassword)) {
            username = etUsername.getText().toString().trim();
            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            Utils.showProgressDialog(SignUpActivity.this, getString(R.string.please_wait));
            new Handler().postDelayed(() -> Utils.hideProgressDialog(), 1000);
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        }
    }
}