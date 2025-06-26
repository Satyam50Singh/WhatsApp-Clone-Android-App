package com.example.whatsappclone.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Auth;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private GoogleSignInClient googleSignInClient;

    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Objects.requireNonNull(getSupportActionBar()).hide();

        init();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);

        TextView tvCreateAnAccount = findViewById(R.id.tv_create_an_account);
        Button btnLoginWithPhone = findViewById(R.id.btn_login_with_phone);
        Button btnSignIn = findViewById(R.id.btn_sign_in);
        etEmail = findViewById(R.id.et_email_sign_in);
        etPassword = findViewById(R.id.et_password_sign_in);
        Button btnGoogle = findViewById(R.id.btn_google_sign_in);

        tvCreateAnAccount.setOnClickListener(view -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
        btnLoginWithPhone.setOnClickListener(view -> Auth.navigateToLoginWithPhoneActivity(SignInActivity.this));
        btnSignIn.setOnClickListener(view -> userSignIn());
        btnGoogle.setOnClickListener(view -> userSignInByGoogle());

        // Configure Google Sign In
        googleSignInClient = GoogleSignIn.getClient(this, Auth.getGoogleSignInOptions(this));
    }

    private void userSignIn() {
        try {
            if (Utils.credentialsValidation(this, etEmail, etPassword)) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                Utils.showProgressDialog(this, getString(R.string.login), getString(R.string.login_to_your_account));

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            Utils.hideProgressDialog();
                            if (task.isSuccessful()) {
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                Utils.showToastMessage(this, task.getException().getMessage());
                            }
                        });
            }
        } catch (Exception e) {
            Utils.showToastMessage(this, e.getMessage());
        }
    }

    private void userSignInByGoogle() {
        googleSignInClient.signOut(); // Optional: always ask for account
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Utils.showProgressDialog(this, getString(R.string.login), getString(R.string.login_to_your_account));
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    Utils.showToastMessage(this, getString(R.string.google_sign_in_failed_account_is_null));
                    Log.e(TAG, "GoogleSignInAccount is null");
                }
            } catch (ApiException e) {
                Utils.showToastMessage(this, "Google Sign-In failed");
                Log.e(TAG, "Google sign-in error: " + e.getMessage(), e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (idToken == null) {
            Utils.showToastMessage(this, "Google ID Token is null");
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Utils.hideProgressDialog();
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            UserModel userModel = new UserModel();
                            userModel.setUserId(firebaseUser.getUid());
                            userModel.setUsername(firebaseUser.getDisplayName());
                            if (firebaseUser.getEmail() != null) {
                                userModel.setEmail(firebaseUser.getEmail());
                            } else {
                                userModel.setPhone(firebaseUser.getPhoneNumber());
                            }
                            if (firebaseUser.getPhotoUrl() != null) {
                                userModel.setProfilePicture(firebaseUser.getPhotoUrl().toString());
                            }
                            firebaseDatabase.getReference()
                                    .child(Constants.USER_COLLECTION_NAME)
                                    .child(firebaseUser.getUid())
                                    .setValue(userModel);

                            startActivity(new Intent(this, MainActivity.class));
                            finishAffinity();
                        } else {
                            Utils.showToastMessage(this, "Firebase user is null");
                        }
                    } else {
                        Exception e = task.getException();
                        Log.e(TAG, "Firebase sign-in failed: " + (e != null ? e.getMessage() : "Unknown error"));
                        Utils.showToastMessage(this, "Authentication failed: " + (e != null ? e.getMessage() : ""));
                    }
                });
    }
}
