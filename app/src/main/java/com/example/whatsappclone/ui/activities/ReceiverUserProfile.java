package com.example.whatsappclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.whatsappclone.R;

public class ReceiverUserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_user_profile);
        getSupportActionBar().setTitle(getIntent().getStringExtra("ReceiverName"));
    }
}