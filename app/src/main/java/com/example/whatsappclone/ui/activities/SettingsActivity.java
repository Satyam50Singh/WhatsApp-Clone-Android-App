package com.example.whatsappclone.ui.activities;

import static com.example.whatsappclone.utils.Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.example.whatsappclone.utils.Utils.PICK_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.example.whatsappclone.utils.Utils.encodeImage;
import static com.example.whatsappclone.utils.Utils.getBitmapFromUri;
import static com.example.whatsappclone.utils.Utils.takePictureFromCamera;
import static com.example.whatsappclone.utils.Utils.takePictureFromGallery;
import static com.example.whatsappclone.utils.Utils.validateUsername;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.ui.fragments.BottomSheetUpdateProfileFragment;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements BottomSheetUpdateProfileFragment.BottomSheetListener {

    private Button btnEditProfile;
    private TextInputEditText etUserAbout, etFullName;
    private FloatingActionButton fabEditProfilePicture;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    CircleImageView civProfileImage;
    private Bitmap selectedBitmap;
    private String profileEncodedString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);

        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
        etUserAbout = findViewById(R.id.et_user_about);
        etFullName = findViewById(R.id.et_full_name);
        fabEditProfilePicture = findViewById(R.id.fab_edit_profile);
        civProfileImage = findViewById(R.id.civ_edit_profile);

        btnEditProfile.setOnClickListener(view -> {
            if (btnEditProfile.getText().toString().equals(getString(R.string.edit_profile))) {
                btnEditProfile.setText(R.string.save);
                etUserAbout.setEnabled(true);
                etFullName.setEnabled(true);
                etUserAbout.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
                etFullName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
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

    @Override
    public void onOptionClick(String text) {
        if (text.equals(getString(R.string.camera))) {
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
                    if (resultCode == Activity.RESULT_OK && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                        Bundle bundle = data.getExtras();
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
            default:
                return;
        }
    }

    private void updateProfile() {
        String fullName = etFullName.getText().toString().trim();
        if (validateUsername(SettingsActivity.this, etFullName)) {
            String userAbout = etUserAbout.getText().toString().trim();
            profileEncodedString = encodeImage(selectedBitmap); // converting bitmap to base64 string

            // update/insert image in users database
            insertProfileImage();

            btnEditProfile.setText(getString(R.string.edit_profile));
            etUserAbout.setEnabled(false);
            etFullName.setEnabled(false);
            etUserAbout.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            etFullName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            fabEditProfilePicture.setVisibility(View.GONE);
            btnEditProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
        }
    }

    private void insertProfileImage() {
        firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .child("profilePicture").setValue(profileEncodedString);
    }

}