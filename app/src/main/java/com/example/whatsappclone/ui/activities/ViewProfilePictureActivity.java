package com.example.whatsappclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.whatsappclone.R;
import com.squareup.picasso.Picasso;

public class ViewProfilePictureActivity extends AppCompatActivity {

    private ImageView ivViewProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_picture);
        init();
        getSetValues();
    }

    private void init() {
        ivViewProfilePicture = findViewById(R.id.iv_view_profile_picture);
    }

    private void getSetValues() {
        Intent intent = getIntent();
        String username = intent.getStringExtra(getString(R.string.username));
        getSupportActionBar().setTitle(username);
        Picasso.with(ViewProfilePictureActivity.this)
                .load(intent.getStringExtra(getString(R.string.profileImage)))
                .placeholder(R.drawable.man)
                .into(ivViewProfilePicture);
    }

    @Override
    public void onBackPressed() {
    }
}