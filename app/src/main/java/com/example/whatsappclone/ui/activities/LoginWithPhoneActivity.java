package com.example.whatsappclone.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginWithPhoneActivity extends AppCompatActivity {

    private TextInputEditText etMobileNo;
    private TextInputEditText edtOTP1, edtOTP2, edtOTP3, edtOTP4, edtOTP5, edtOTP6;
    private Button btnLogin;
    private TextView tvResend, tvHeading, tvDescription, tvDescription2;
    private LinearLayout llOTP;
    String OTPValue, backendOTPValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        etMobileNo = findViewById(R.id.et_enter_mobile_no);
        btnLogin = findViewById(R.id.btn_phone_login);
        llOTP = findViewById(R.id.ll_otp);
        tvResend = findViewById(R.id.tv_resend_otp);
        tvHeading = findViewById(R.id.tv_heading);
        tvDescription = findViewById(R.id.tv_description);
        tvDescription2 = findViewById(R.id.tv_description2);
        edtOTP1 = findViewById(R.id.edt_otp_1);
        edtOTP2 = findViewById(R.id.edt_otp_2);
        edtOTP3 = findViewById(R.id.edt_otp_3);
        edtOTP4 = findViewById(R.id.edt_otp_4);
        edtOTP5 = findViewById(R.id.edt_otp_5);
        edtOTP6 = findViewById(R.id.edt_otp_6);
        etMobileNo.requestFocus();
        setListeners();
        numberMoveOTP();
    }

    private void setListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (llOTP.getVisibility() == View.GONE) {
                        // send OTP code
                        String mobileNumber = etMobileNo.getText().toString().trim();
                        if (mobileNumber.length() < 10) {
                            etMobileNo.setError(getString(R.string.enter_valid_number));
                            etMobileNo.requestFocus();
                            return;
                        }

                        // login with phone number
                        loginWithPhoneNumber(mobileNumber);

                        llOTP.setVisibility(View.VISIBLE);
                        btnLogin.setText(R.string.verify_otp);
                    } else {
                        // verify OTP code
                        verifyOTP();
                    }
                } catch (Exception e) {
                    Utils.showLog(getString(R.string.error), e.getMessage());
                }
            }
        });
    }

    private void numberMoveOTP() {
        edtOTP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    edtOTP2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    edtOTP3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    edtOTP4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    edtOTP5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    edtOTP6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loginWithPhoneNumber(String phoneNumber) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                LoginWithPhoneActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Utils.showLog(getString(R.string.success), "Verification successfull");

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Utils.showLog(getString(R.string.error), e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String backendOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(backendOTP, forceResendingToken);
                        Utils.showToastMessage(LoginWithPhoneActivity.this, backendOTP);
                        backendOTPValue = backendOTP;
                    }
                }
        );
    }

    private void verifyOTP() {
        if (edtOTP1.getText().toString().isEmpty() || edtOTP2.getText().toString().isEmpty() || edtOTP3.getText().toString().isEmpty() || edtOTP4.getText().toString().isEmpty() || edtOTP5.getText().toString().isEmpty() || edtOTP6.getText().toString().isEmpty()) {
            Utils.showToastMessage(LoginWithPhoneActivity.this, getString(R.string.please_enter_otp));
            return;
        }
        String OTPValue = edtOTP1.getText().toString().trim() +
                edtOTP2.getText().toString().trim() +
                edtOTP3.getText().toString().trim() +
                edtOTP4.getText().toString().trim() +
                edtOTP5.getText().toString().trim() +
                edtOTP6.getText().toString().trim();

        if (backendOTPValue != null) {
            Utils.showProgressDialog(LoginWithPhoneActivity.this,"", getString(R.string.please_wait));
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                    backendOTPValue, OTPValue
            );
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginWithPhoneActivity.this, SettingsActivity.class));
                                finish();
                            }else{
                                Utils.showToastMessage(LoginWithPhoneActivity.this, getString(R.string.incorrect_otp));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else {
            Utils.showToastMessage(LoginWithPhoneActivity.this, getString(R.string.please_check_connection));
        }
    }

}