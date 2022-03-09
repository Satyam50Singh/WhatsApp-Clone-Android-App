package com.example.whatsappclone.ui.activities;

import static com.example.whatsappclone.utils.Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.example.whatsappclone.utils.Utils.PICK_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.example.whatsappclone.utils.Utils.decodeImage;
import static com.example.whatsappclone.utils.Utils.encodeImage;
import static com.example.whatsappclone.utils.Utils.getBitmapFromUri;
import static com.example.whatsappclone.utils.Utils.takePictureFromCamera;
import static com.example.whatsappclone.utils.Utils.takePictureFromGallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.ui.fragments.BottomSheetUpdateProfileFragment;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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
        if (intent.getStringExtra(getString(R.string.profileImage)) != null && !intent.getStringExtra(getString(R.string.profileImage)).startsWith(getString(R.string.http))) {
            ivViewProfilePicture.setImageBitmap(decodeImage(intent.getStringExtra(getString(R.string.profileImage))));
        } else {
            Picasso.with(this).load(intent.getStringExtra(getString(R.string.profileImage))).placeholder(R.drawable.man).into(ivViewProfilePicture);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_profile_picture, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}