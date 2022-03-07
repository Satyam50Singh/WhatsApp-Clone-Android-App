package com.example.whatsappclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.whatsappclone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {

    Button btnEditProfile;
    TextInputEditText etUserAbout, etFullName;
    FloatingActionButton fabEditProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
        etUserAbout = findViewById(R.id.et_user_about);
        etFullName = findViewById(R.id.et_full_name);
        fabEditProfilePicture = findViewById(R.id.fab_edit_profile);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnEditProfile.getText().toString().equals(getString(R.string.edit_profile))) {
                    btnEditProfile.setText(R.string.save);
                    etUserAbout.setEnabled(true);
                    etFullName.setEnabled(true);
                    etUserAbout.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
                    etFullName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
                    fabEditProfilePicture.setVisibility(View.VISIBLE);
                    btnEditProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    btnEditProfile.setText(getString(R.string.edit_profile));
                    etUserAbout.setEnabled(false);
                    etFullName.setEnabled(false);
                    etUserAbout.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                    etFullName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                    fabEditProfilePicture.setVisibility(View.GONE);
                    btnEditProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
                }

            }
        });
    }
}