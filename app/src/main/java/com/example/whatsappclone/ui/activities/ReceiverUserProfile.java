package com.example.whatsappclone.ui.activities;

import static com.example.whatsappclone.utils.Utils.decodeImage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceiverUserProfile extends AppCompatActivity {

    private CircleImageView civReceiverProfilePicture;
    private TextView tvReceiverNameUserProfile, tvAccountLogin, tvReceiverStatus;
    private ImageView ivBackArrowReceiverProfile;
    private String profilePicture, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_user_profile);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        civReceiverProfilePicture = findViewById(R.id.civ_receiver_profile_picture);
        tvReceiverStatus = findViewById(R.id.tv_receiver_status);
        tvReceiverNameUserProfile = findViewById(R.id.tv_receiver_name_user_profile);
        tvAccountLogin = findViewById(R.id.tv_account_login);
        ivBackArrowReceiverProfile = findViewById(R.id.iv_back_arrow_receiver_profile);
        // show Data
        getIntentValues();
        listeners();
    }

    private void getIntentValues() {
        String userId = getIntent().getStringExtra(getString(R.string.receiver_id));
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);

        firebaseDatabase.getReference()
                .child(Constants.USER_COLLECTION_NAME)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            userModel.getUserId(dataSnapshot.getKey());
                            try {
                                if (dataSnapshot.getKey().equals(userId)) {
                                    username = userModel.getUsername();
                                    tvReceiverNameUserProfile.setText(username);
                                    tvAccountLogin.setText(userModel.getPhone() != null ? userModel.getPhone() : userModel.getEmail());
                                    tvReceiverStatus.setText(userModel.getStatus() != null ? userModel.getStatus() : getString(R.string.hey_there_i_am_using_whatsapp));
                                    profilePicture = userModel.getProfilePicture();
                                    if (userModel.getProfilePicture() != null && !userModel.getProfilePicture().startsWith(getString(R.string.http))) {
                                        civReceiverProfilePicture.setImageBitmap(decodeImage(userModel.getProfilePicture()));
                                    } else {
                                        Picasso.with(ReceiverUserProfile.this).load(userModel.getProfilePicture()).placeholder(R.drawable.man).into(civReceiverProfilePicture);
                                    }
                                }
                            } catch (Exception e) {
                                Utils.showLog(getString(R.string.error), e.getMessage());
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void listeners() {
        ivBackArrowReceiverProfile.setOnClickListener(view -> onSupportNavigateUp());

        civReceiverProfilePicture.setOnClickListener(view -> {
            Intent intent = new Intent(ReceiverUserProfile.this, ViewProfilePictureActivity.class);
            intent.putExtra(getString(R.string.username), username);
            intent.putExtra(getString(R.string.profileImage), profilePicture);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}