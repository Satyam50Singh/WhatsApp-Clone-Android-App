package com.example.whatsappclone.ui.activities;

import static com.example.whatsappclone.utils.Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.example.whatsappclone.utils.Utils.PICK_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.example.whatsappclone.utils.Utils.encodeImage;
import static com.example.whatsappclone.utils.Utils.getBitmapFromUri;
import static com.example.whatsappclone.utils.Utils.takePictureFromCamera;
import static com.example.whatsappclone.utils.Utils.takePictureFromGallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.ui.fragments.BottomSheetUpdateProfileFragment;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginWithPhoneActivity extends AppCompatActivity implements BottomSheetUpdateProfileFragment.BottomSheetListener {

    private TextInputEditText etMobileNo, edtOTP1, edtOTP2, edtOTP3, edtOTP4, edtOTP5, edtOTP6;
    private Button btnLogin;
    private TextView tvResend, tvHeading, tvDescription, tvDescription2;
    private LinearLayout llOTP;
    private String backendOTPValue;
    private FirebaseDatabase firebaseDatabase;
    private Bitmap selectedBitmap;
    CircleImageView civProfileImage;
    private String profileEncodedString;

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
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);

        setListeners();
        numberMoveOTP();
    }

    private void setListeners() {
        btnLogin.setOnClickListener(view -> {
            try {
                if (llOTP.getVisibility() == View.GONE) {
                    // send OTP code
                    String mobileNumber = etMobileNo.getText().toString().trim();
                    if (mobileNumber.length() < 10) {
                        etMobileNo.setError(getString(R.string.enter_valid_number));
                        etMobileNo.requestFocus();
                        return;
                    }
                    etMobileNo.setEnabled(false);
                    edtOTP1.requestFocus();
                    // login with phone number
                    loginWithPhoneNumber(mobileNumber);

                    llOTP.setVisibility(View.VISIBLE);
                    btnLogin.setText(R.string.verify_otp);
                    tvHeading.setText(R.string.verification);
                    tvDescription2.setVisibility(View.GONE);
                    tvDescription.setText(R.string.enter_otp_value);
                } else {
                    // verify OTP code
                    verifyOTP();
                }
            } catch (Exception e) {
                Utils.showLog(getString(R.string.error), e.getMessage());
            }
        });
        tvResend.setOnClickListener(view -> {
            loginWithPhoneNumber(etMobileNo.getText().toString().trim());
            verifyOTP();
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
        edtOTP6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    btnLogin.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loginWithPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 10) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91" + phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    LoginWithPhoneActivity.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            Utils.showLog(getString(R.string.success), getString(R.string.verification_successful));
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Utils.showLog(getString(R.string.error), e.getMessage());
                        }

                        @Override
                        public void onCodeSent(@NonNull String backendOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(backendOTP, forceResendingToken);
                            backendOTPValue = backendOTP;
                        }
                    }
            );
        }
    }

    private void verifyOTP() {
        if (edtOTP1.getText().toString().isEmpty() || edtOTP2.getText().toString().isEmpty() || edtOTP3.getText().toString().isEmpty() || edtOTP4.getText().toString().isEmpty() || edtOTP5.getText().toString().isEmpty() || edtOTP6.getText().toString().isEmpty()) {
            return;
        }
        String OTPValue = edtOTP1.getText().toString().trim() +
                edtOTP2.getText().toString().trim() +
                edtOTP3.getText().toString().trim() +
                edtOTP4.getText().toString().trim() +
                edtOTP5.getText().toString().trim() +
                edtOTP6.getText().toString().trim();

        if (backendOTPValue != null) {
            Utils.showProgressDialog(LoginWithPhoneActivity.this, "", getString(R.string.please_wait));
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                    backendOTPValue, OTPValue
            );
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            UserModel userModel = new UserModel();
                            userModel.setUserId(firebaseUser.getUid());
                            userModel.setPhone(etMobileNo.getText().toString().trim());

                            firebaseDatabase.getReference()
                                    .child(Constants.USER_COLLECTION_NAME)
                                    .child(task.getResult().getUser().getUid())
                                    .setValue(userModel);

                            // method for profile setup
                            setUpProfile();
                        } else {
                            Utils.showToastMessage(LoginWithPhoneActivity.this, getString(R.string.incorrect_otp));
                        }
                    });
        } else {
            Utils.showToastMessage(LoginWithPhoneActivity.this, getString(R.string.please_check_connection));
        }
    }

    private void setUpProfile() {
        Dialog dialog = new Dialog(LoginWithPhoneActivity.this);
        dialog.setContentView(R.layout.setup_profile_dialog);
        dialog.setCancelable(false);
        EditText etFullName, etAbout;
        Button btnSave;
        TextView tvSkip;
        FloatingActionButton fabEditProfile;

        etFullName = dialog.findViewById(R.id.et_full_name_setup);
        etAbout = dialog.findViewById(R.id.et_user_about_setup);
        civProfileImage = dialog.findViewById(R.id.civ_edit_profile_setup);
        btnSave = dialog.findViewById(R.id.btn_save_profile_setup);
        tvSkip = dialog.findViewById(R.id.tv_skip_for_now_setup);
        fabEditProfile = dialog.findViewById(R.id.fab_edit_profile_setup);

        fabEditProfile.setOnClickListener(view -> {
            BottomSheetUpdateProfileFragment bottomSheetUpdateProfileFragment = new BottomSheetUpdateProfileFragment();
            bottomSheetUpdateProfileFragment.show(getSupportFragmentManager(), getString(R.string.bottom_sheet_tag));
        });

        tvSkip.setOnClickListener(view -> {
            startActivity(new Intent(LoginWithPhoneActivity.this, MainActivity.class));
            finish();
        });

        btnSave.setOnClickListener(view -> {
            String fullName = etFullName.getText().toString().trim();
            String about = etAbout.getText().toString().trim();

            if (fullName.length() > 0) {
                if (selectedBitmap != null) {
                    profileEncodedString = encodeImage(selectedBitmap); // converting bitmap to base64 string
                }

                // update/insert image in users database
                HashMap<String, Object> objectHashMap = new HashMap<>();
                objectHashMap.put(getString(R.string.username), fullName);
                objectHashMap.put(getString(R.string.status), about);
                objectHashMap.put(getString(R.string.profilePicture), profileEncodedString);
                firebaseDatabase.getReference()
                        .child(Constants.USER_COLLECTION_NAME)
                        .child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(objectHashMap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginWithPhoneActivity.this, MainActivity.class));
                                finish();
                                dialog.dismiss();
                                Utils.showToastMessage(LoginWithPhoneActivity.this, getString(R.string.profile_updated));
                            }
                        });
            } else {
                etFullName.setError(getString(R.string.username_required));
                etFullName.requestFocus();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                try {
                    if (resultCode == Activity.RESULT_OK) {
                        Bundle bundle = null;
                        if (data != null) {
                            bundle = data.getExtras();
                        }
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        selectedBitmap = bitmap;
                        Glide.with(LoginWithPhoneActivity.this)
                                .load(bitmap)
                                .placeholder(getResources().getDrawable(R.drawable.man))
                                .into(civProfileImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PICK_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImageUri;
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        selectedBitmap = getBitmapFromUri(selectedImageUri, LoginWithPhoneActivity.this);
                        Glide.with(LoginWithPhoneActivity.this)
                                .load(selectedBitmap)
                                .placeholder(getResources().getDrawable(R.drawable.man))
                                .into(civProfileImage);
                    }
                }
                break;
        }
    }

    @Override
    public void onOptionClick(String text) {
        if (text.equals(getString(R.string.remove_profile))) {
            Utils.removeProfilePicture(LoginWithPhoneActivity.this, FirebaseAuth.getInstance().getUid());
        } else if (text.equals(getString(R.string.camera))) {
            takePictureFromCamera(this);
        } else {
            takePictureFromGallery(this);
        }
    }
}