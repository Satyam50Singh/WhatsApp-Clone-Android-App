package com.example.whatsappclone.ui.activities;

import static com.example.whatsappclone.utils.Utils.decodeImage;

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        if (intent.getStringExtra(getString(R.string.profileImage)) != null && !intent.getStringExtra(getString(R.string.profileImage)).startsWith(getString(R.string.http))) {
            ivViewProfilePicture.setImageBitmap(decodeImage(intent.getStringExtra(getString(R.string.profileImage))));
        } else {
            Picasso.get().load(intent.getStringExtra(getString(R.string.profileImage))).placeholder(R.drawable.man).into(ivViewProfilePicture);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}