package com.example.whatsappclone.ui.activities;

import static com.example.whatsappclone.utils.Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.example.whatsappclone.utils.Utils.PICK_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.example.whatsappclone.utils.Utils.decodeImage;
import static com.example.whatsappclone.utils.Utils.encodeImage;
import static com.example.whatsappclone.utils.Utils.getBitmapFromUri;
import static com.example.whatsappclone.utils.Utils.takePictureFromCamera;
import static com.example.whatsappclone.utils.Utils.takePictureFromGallery;
import static com.example.whatsappclone.utils.Utils.validateUsername;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements BottomSheetUpdateProfileFragment.BottomSheetListener {

    private Button btnEditProfile;
    private TextInputEditText etUserAbout, etFullName, etPhone;
    private FloatingActionButton fabEditProfilePicture;

    private FirebaseDatabase firebaseDatabase;

    private CircleImageView civProfileImage;
    private Bitmap selectedBitmap;
    private String profileEncodedString, userEmail;
    private TextView tvAccountVerificationStatus;

    private LinearLayout llVerify;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0b6156")));

        init();
    }

    private void init() {
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);

        btnEditProfile = findViewById(R.id.btn_edit_profile);
        etUserAbout = findViewById(R.id.et_user_about);
        etPhone = findViewById(R.id.et_user_phone);
        etFullName = findViewById(R.id.et_full_name);
        fabEditProfilePicture = findViewById(R.id.fab_edit_profile);
        civProfileImage = findViewById(R.id.civ_edit_profile);
        tvAccountVerificationStatus = findViewById(R.id.tv_account_verification_status);
        llVerify = findViewById(R.id.ll_verify);

        // profile syncing
        syncProfile();

        // listeners
        btnEditProfile.setOnClickListener(view -> {
            if (btnEditProfile.getText().toString().equals(getString(R.string.edit_profile))) {
                btnEditProfile.setText(R.string.save);
                etUserAbout.setEnabled(true);
                etFullName.setEnabled(true);
                etPhone.setEnabled(true);
                etUserAbout.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
                etFullName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
                etPhone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
                fabEditProfilePicture.setVisibility(View.VISIBLE);
                btnEditProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                updateProfile();
            }
        });

        fabEditProfilePicture.setOnClickListener(view -> {
            BottomSheetUpdateProfileFragment bottomSheetUpdateProfileFragment = new BottomSheetUpdateProfileFragment();
            bottomSheetUpdateProfileFragment.show(getSupportFragmentManager(), getString(R.string.bottom_sheet_tag));
        });
    }

    private void syncProfile() {
        firebaseDatabase.getReference()
                .child(Constants.USER_COLLECTION_NAME)
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            etFullName.setText(userModel.getUsername());
                            etUserAbout.setText(userModel.getStatus());
                            etPhone.setText(userModel.getPhone());
                            userEmail = userModel.getEmail();
                            if (userModel.getProfilePicture() != null && !userModel.getProfilePicture().startsWith(getString(R.string.http))) {
                                civProfileImage.setImageBitmap(decodeImage(userModel.getProfilePicture()));
                            } else {
                                Picasso.get().load(userModel.getProfilePicture()).placeholder(R.drawable.man).into(civProfileImage);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        checkVerificationStatus();
    }

    @SuppressLint("ResourceAsColor")
    private void checkVerificationStatus() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (!firebaseUser.isEmailVerified()) {
            tvAccountVerificationStatus.setText(R.string.not_verified);
            tvAccountVerificationStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9F0B0B")));
        } else {
            tvAccountVerificationStatus.setText(R.string.verified);
            tvAccountVerificationStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#095049")));
        }
    }

    @Override
    public void onOptionClick(String text) {
        if (text.equals(getString(R.string.remove_profile))) {
            Utils.removeProfilePicture(SettingsActivity.this, FirebaseAuth.getInstance().getUid());
        } else if (text.equals(getString(R.string.camera))) {
            takePictureFromCamera(this);
        } else {
            takePictureFromGallery(this);
        }
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
                        Glide.with(SettingsActivity.this)
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
                        selectedBitmap = getBitmapFromUri(selectedImageUri, SettingsActivity.this);
                        Glide.with(SettingsActivity.this)
                                .load(selectedBitmap)
                                .placeholder(getResources().getDrawable(R.drawable.man))
                                .into(civProfileImage);
                    }
                }
                break;
        }
    }

    private void updateProfile() {
        String fullName = etFullName.getText().toString().trim();
        if (validateUsername(SettingsActivity.this, etFullName)) {
            String userAbout = etUserAbout.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            if (selectedBitmap != null) {
                profileEncodedString = encodeImage(selectedBitmap); // converting bitmap to base64 string
            }

            // update/insert image in users database
            HashMap<String, Object> objectHashMap = new HashMap<>();
            objectHashMap.put(getString(R.string.username), fullName);
            objectHashMap.put(getString(R.string.status), userAbout);
            objectHashMap.put(getString(R.string.phone), phone);
            objectHashMap.put(getString(R.string.profilePicture), profileEncodedString);
            firebaseDatabase.getReference()
                    .child(Constants.USER_COLLECTION_NAME)
                    .child(FirebaseAuth.getInstance().getUid())
                    .updateChildren(objectHashMap);

            changeScreenState();
        }
    }

    private void changeScreenState() {
        btnEditProfile.setText(getString(R.string.edit_profile));
        etUserAbout.setEnabled(false);
        etFullName.setEnabled(false);
        etPhone.setEnabled(false);
        etUserAbout.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        etFullName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        fabEditProfilePicture.setVisibility(View.GONE);
        btnEditProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
        finish();
    }
}