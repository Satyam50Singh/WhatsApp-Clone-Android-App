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

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    TextView tvAlreadyHaveAccount;
    Button btnSignUp;

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
        tvAlreadyHaveAccount.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, SignInActivity.class)));
        btnSignUp.setOnClickListener(view -> {
            Log.d("TAG", "init: ");
            Utils.showToastMessage(this, "Signed Up Successfully");
            Utils.showProgressDialog(SignUpActivity.this, getString(R.string.please_wait));
            new Handler().postDelayed(() -> Utils.hideProgressDialog(), 1000);
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        });
    }
}