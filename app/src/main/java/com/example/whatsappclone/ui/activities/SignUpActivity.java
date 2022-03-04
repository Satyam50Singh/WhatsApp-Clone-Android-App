package com.example.whatsappclone.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    TextView tvAlreadyHaveAccount;
    Button btnSignUp;
    TextInputEditText etUsername, etEmail, etPassword;

    private String username, email, password;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

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

    // method for user signup
    private void userSignUp() {
        try {
            if (Utils.validateUsername(SignUpActivity.this, etUsername) && Utils.credentialsValidation(SignUpActivity.this, etEmail, etPassword)) {
                username = etUsername.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);

                Utils.showProgressDialog(SignUpActivity.this, getString(R.string.creating_account), getString(R.string.creating_your_account));

                // this createUserWithEmailAndPassword() will create an new user
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                // sending email for verification
                                sentVerificationEmail(firebaseAuth.getCurrentUser());

                                UserModel userModel = new UserModel(username, email, password);
                                String id = task.getResult().getUser().getUid();
                                firebaseDatabase.getReference().child(Constants.COLLECTION_NAME).child(id).setValue(userModel); // storing values in realtime database
                                Utils.hideProgressDialog();
                                Utils.showToastMessage(SignUpActivity.this, getString(R.string.user_created_successfully));
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Utils.showToastMessage(SignUpActivity.this, task.getException().getMessage());
                                Utils.hideProgressDialog();
                            }
                        });
            }
        } catch (Exception e) {
            Utils.showToastMessage(SignUpActivity.this, e.getMessage());
        }
    }

    private void sentVerificationEmail(FirebaseUser currentUser) {
        currentUser.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Utils.showToastMessage(SignUpActivity.this, getString(R.string.verification_email_sent));
                    }
                })
                .addOnFailureListener(e -> Utils.showToastMessage(SignUpActivity.this, e.getMessage()));

    }
}