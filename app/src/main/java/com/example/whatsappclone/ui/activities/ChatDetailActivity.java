package com.example.whatsappclone.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ChatAdapter;
import com.example.whatsappclone.models.MessageModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private String senderId, receiverId, username, profileImage, senderRoom, receiverRoom;
    private Toolbar toolbar;
    private ImageView ivBackArrow;
    private TextView tvReceiverName;
    private CircleImageView civProfileImage;
    private RecyclerView rcvUserChat;
    private LinearLayout llSentBtn;
    private EditText etMessage;

    private ArrayList<MessageModel> chatRecord = new ArrayList<>();
    ChatAdapter chatAdapter;

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
        etMessage = findViewById(R.id.et_message);
        etMessage.requestFocus();

        // getting values by intent
        getIntentValues();

        // setting user details in toolbar
        setUserDetailsOnToolbar();

        // click listeners
        ivBackArrow.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        // fetching chat and storing in chatRecord ArrayList
        Utils.showProgressDialog(getContext(), "", getString(R.string.please_wait));
        loadChatMessages();

        chatAdapter = new ChatAdapter(ChatDetailActivity.this, chatRecord);
        rcvUserChat.setLayoutManager(new LinearLayoutManager(ChatDetailActivity.this));
        rcvUserChat.setAdapter(chatAdapter);

        llSentBtn.setOnClickListener(view -> {
            storingMessagesInFirebaseDatabase();
        });
    }

    private void getIntentValues() {
        try {
            senderId = firebaseAuth.getUid();
            receiverId = getIntent().getStringExtra(getString(R.string.userId));
            username = getIntent().getStringExtra(getString(R.string.username));
            profileImage = getIntent().getStringExtra(getString(R.string.profileImage));
            if(senderId != null && receiverId != null){
                senderRoom = senderId + receiverId;
                receiverRoom = receiverId + senderId;
            }
        } catch(Exception e) {
            Utils.showLog("Error : ", e.getMessage());
        }
    }    

    private void setUserDetailsOnToolbar() {
        Picasso.with(getApplicationContext()).load(profileImage).placeholder(R.drawable.man_toolbar).into(civProfileImage);
        tvReceiverName.setText(username);
    }

    private void loadChatMessages() {
        try {
            firebaseDatabase.getReference()
                    .child("Chats")
                    .child(senderId+receiverId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            chatRecord.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                MessageModel message = snapshot1.getValue(MessageModel.class);
                                message.getMessageId(dataSnapshot.getKey());
                                chatRecord.add(message);
                        }
                        chatAdapter.notifyDataSetChanged();
                        Utils.hideProgressDialog();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Utils.hideProgressDialog();
                        }
                    });
        } catch(Exception e) {
            Utils.showLog("Error : ", e.getMessage());
        }
    }

    private void storingMessagesInFirebaseDatabase() {
        try {
            String message = etMessage.getText().toString();
            etMessage.setText("");
            etMessage.requestFocus();
            MessageModel model = new MessageModel(senderId, message, new Date().getTime());
            if(senderId != null && receiverId != null){
                firebaseDatabase.getReference()
                        .child("Chats")
                        .child(senderId + receiverId)
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseDatabase.getReference()
                                .child("Chats")
                                .child(receiverRoom)
                                .push()
                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        });
                    }
                });
            } else {
                Utils.showLog("Ids", "senderId : " + senderId + " receiverId : " + receiverId);
            }
        } catch(Exception e) {
            Utils.showLog("Error : ", e.getMessage());
        }
    }
}