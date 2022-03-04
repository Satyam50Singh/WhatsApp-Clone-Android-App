package com.example.whatsappclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private String senderId, receiverId, username, profileImage;
    private Toolbar toolbar;
    private ImageView ivBackArrow;
    private TextView tvReceiverName;
    private CircleImageView civProfileImage;
    private RecyclerView rcvUserChat;
    private LinearLayout llSentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);

        // reference to controls
        toolbar = findViewById(R.id.toolbar);
        ivBackArrow = toolbar.findViewById(R.id.iv_back_arrow);
        civProfileImage = toolbar.findViewById(R.id.civ_chat_profile_image);
        tvReceiverName = toolbar.findViewById(R.id.tv_receiver_name);
        rcvUserChat = findViewById(R.id.rcv_user_chat);
        llSentBtn = findViewById(R.id.ll_send_btn);

        // getting values by intent
        senderId = firebaseAuth.getUid();
        receiverId = getIntent().getStringExtra(getString(R.string.userId));
        username = getIntent().getStringExtra(getString(R.string.username));
        profileImage = getIntent().getStringExtra(getString(R.string.profileImage));

        // setting user details in toolbar
        Picasso.with(getApplicationContext()).load(profileImage).placeholder(R.drawable.man_toolbar).into(civProfileImage);
        tvReceiverName.setText(username);
        ivBackArrow.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });

        llSentBtn.setOnClickListener(view -> {
            Utils.showToastMessage(ChatDetailActivity.this, "Send Message Button Works!");
        });
    }
}