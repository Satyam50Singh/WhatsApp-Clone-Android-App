package com.example.whatsappclone.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ChatAdapter;
import com.example.whatsappclone.adapter.GroupChatAdapter;
import com.example.whatsappclone.models.MessageModel;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ImageView ivBackArrowGC;
    private ArrayList<MessageModel> groupChatRecord = new ArrayList<>();
    private ArrayList<UserModel> groupChatUsers = new ArrayList<>();
    GroupChatAdapter groupChatAdapter;
    RecyclerView rcvUserChatGC;
    private EditText etMessageGC;
    private LinearLayout llSentBtnGC;
    private String senderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        senderId = firebaseAuth.getUid();
        groupChatRecord = new ArrayList<>();
        ivBackArrowGC = findViewById(R.id.iv_back_arrow_gc);
        rcvUserChatGC = findViewById(R.id.rcv_user_chat_gc);
        etMessageGC = findViewById(R.id.et_message_gc);
        llSentBtnGC = findViewById(R.id.ll_send_btn_gc);
        etMessageGC.requestFocus();

        ivBackArrowGC.setOnClickListener(view -> startActivity(new Intent(GroupChatActivity.this, MainActivity.class)));

        loadGroupChatMessages();
        loadGroupChatUsers();

        groupChatAdapter = new GroupChatAdapter(GroupChatActivity.this, groupChatRecord, groupChatUsers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(GroupChatActivity.this);
        rcvUserChatGC.setLayoutManager(layoutManager);
        rcvUserChatGC.setAdapter(groupChatAdapter);
        rcvUserChatGC.postDelayed(() -> rcvUserChatGC.scrollToPosition(rcvUserChatGC.getAdapter().getItemCount() - 1), 1000);

        llSentBtnGC.setOnClickListener(view -> {
            sendMessageToGroup();
        });
    }

    private void loadGroupChatMessages() {
        firebaseDatabase.getReference()
                .child("Group Chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        groupChatRecord.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MessageModel message = dataSnapshot.getValue(MessageModel.class);
                            groupChatRecord.add(message);
                        }
                        groupChatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadGroupChatUsers() {
        firebaseDatabase.getReference()
                .child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            groupChatUsers.add(userModel);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessageToGroup() {
        String messageText = etMessageGC.getText().toString();
        etMessageGC.setText("");
        etMessageGC.requestFocus();

        MessageModel message = new MessageModel(senderId, messageText, new Date().getTime());
        firebaseDatabase.getReference()
                .child("Group Chats")
                .push()
                .setValue(message)
                .addOnSuccessListener(unused -> Utils.showLog(getString(R.string.sent_message_status), getString(R.string.success)))
                .addOnFailureListener(e -> Utils.showLog(getString(R.string.sent_message_status), getString(R.string.failure)));
        rcvUserChatGC.scrollToPosition(rcvUserChatGC.getAdapter().getItemCount() - 1);


    }
}