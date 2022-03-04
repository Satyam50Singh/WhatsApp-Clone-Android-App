package com.example.whatsappclone.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    TextView tvCreateAnAccount;
    Button btnSignIn, btnGoogle;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    GoogleSignInClient googleSignInClient;

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
        btnGoogle = findViewById(R.id.btn_google_sign_in);
        tvCreateAnAccount.setOnClickListener(view -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
        btnSignIn.setOnClickListener(view -> userSignIn());
        btnGoogle.setOnClickListener(view -> userSignInByGoogle());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        // Configure Google Sign In
        googleSignInClient = GoogleSignIn.getClient(this, Auth.getGoogleSignInOptions(this));
    }

    // method for user login
    private void userSignIn() {
        try {
            if (Utils.credentialsValidation(SignInActivity.this, etEmail, etPassword)) {
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();
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
                                } else {
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

    // method for user login by google account
    private void userSignInByGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constants.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Utils.showProgressDialog(SignInActivity.this, getString(R.string.login), getString(R.string.login_to_your_account));
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Utils.showLog(getString(R.string.error),getString(R.string.google_sign_in_failed));
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Utils.hideProgressDialog();
                            try {
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                UserModel userModel = new UserModel();
                                userModel.setUserId(firebaseUser.getUid());
                                userModel.setUsername(firebaseUser.getDisplayName());
                                userModel.setProfilePicture(firebaseUser.getPhotoUrl().toString());
                                firebaseDatabase.getReference().child(Constants.COLLECTION_NAME).child(task.getResult().getUser().getUid()).setValue(userModel);
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                finish();
                            } catch (Exception e) {
                                Utils.showLog(getString(R.string.error),getString(R.string.google_sign_in_failed)+ e.getMessage());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Utils.hideProgressDialog();
                            Utils.showLog(getString(R.string.error),getString(R.string.google_sign_in_failed));
                        }
                    }
                });
    }

}