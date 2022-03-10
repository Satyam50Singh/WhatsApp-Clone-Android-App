package com.example.whatsappclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.material.textfield.TextInputEditText;

public class LoginWithPhoneActivity extends AppCompatActivity {

    private TextInputEditText etMobileNo;
    private TextInputEditText edtOTP1, edtOTP2, edtOTP3, edtOTP4, edtOTP5, edtOTP6;
    private Button btnLogin;
    private TextView tvResend, tvHeading, tvDescription, tvDescription2;
    private LinearLayout llOTP;
    private String OTPValue;

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
                        llOTP.setVisibility(View.VISIBLE);
                        btnLogin.setText(R.string.verify_otp);
                    } else {
                        // verify OTP code
                    }
                } catch (Exception e) {
                    Utils.showLog(getString(R.string.error), e.getMessage());
                }
            }
        });
    }
}