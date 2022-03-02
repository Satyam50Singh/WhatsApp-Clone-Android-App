package com.example.whatsappclone.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    TextView tvCreateAnAccount;
    Button btnSignIn;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    String email, password;

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
        etEmail = findViewById(R.id.et_email_sign_in);
        etPassword = findViewById(R.id.et_password_sign_in);
        tvCreateAnAccount.setOnClickListener(view -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
        btnSignIn.setOnClickListener(view -> userSignIn());
    }

    // method for user login
    private void userSignIn() {
        try {
            if (Utils.credentialsValidation(SignInActivity.this, etEmail, etPassword)) {
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                firebaseAuth = FirebaseAuth.getInstance();
                Utils.showProgressDialog(SignInActivity.this, getString(R.string.login), getString(R.string.login_to_your_account));

                // this signInWithEmailAndPassword() is used for user login
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Utils.hideProgressDialog();
                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                    finish();
                                }else {
                                    Utils.showToastMessage(SignInActivity.this, task.getException().getMessage());
                                    Utils.hideProgressDialog();
                                }
                            }
                        });

            }
        } catch (Exception e) {
            Utils.showToastMessage(this, e.getMessage());
        }
    }
}