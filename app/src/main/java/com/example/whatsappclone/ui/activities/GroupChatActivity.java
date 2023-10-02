package com.example.whatsappclone.ui.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.GroupChatAdapter;
import com.example.whatsappclone.models.MessageModel;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private ArrayList<MessageModel> groupChatRecord = new ArrayList<>();
    private final ArrayList<UserModel> groupChatUsers = new ArrayList<>();
    private GroupChatAdapter groupChatAdapter;
    private RecyclerView rcvUserChatGC;
    private EditText etMessageGC;
    private String senderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        senderId = firebaseAuth.getUid();
        groupChatRecord = new ArrayList<>();
        ImageView ivBackArrowGC = findViewById(R.id.iv_back_arrow_gc);
        rcvUserChatGC = findViewById(R.id.rcv_user_chat_gc);
        etMessageGC = findViewById(R.id.et_message_gc);
        LinearLayout llSentBtnGC = findViewById(R.id.ll_send_btn_gc);
        etMessageGC.requestFocus();

        ivBackArrowGC.setOnClickListener(view -> finish());

        loadGroupChatMessages();
        loadGroupChatUsers();

        groupChatAdapter = new GroupChatAdapter(GroupChatActivity.this, groupChatRecord, groupChatUsers);
        rcvUserChatGC.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        rcvUserChatGC.setAdapter(groupChatAdapter);
        rcvUserChatGC.postDelayed(() -> rcvUserChatGC.scrollToPosition(rcvUserChatGC.getAdapter().getItemCount() - 1), 1000);

        llSentBtnGC.setOnClickListener(view -> sendMessageToGroup());
    }

    private void loadGroupChatMessages() {
        firebaseDatabase.getReference()
                .child(Constants.GROUP_CHAT_COLLECTION_NAME)
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
                .child(Constants.USER_COLLECTION_NAME)
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
        String randomKey = firebaseDatabase.getReference().push().getKey();
        MessageModel message = new MessageModel(randomKey, senderId, messageText, new Date().getTime());
        firebaseDatabase.getReference()
                .child(Constants.GROUP_CHAT_COLLECTION_NAME)
                .child(randomKey)
                .setValue(message)
                .addOnSuccessListener(unused -> Utils.showLog(getString(R.string.sent_message_status), getString(R.string.success)))
                .addOnFailureListener(e -> Utils.showLog(getString(R.string.sent_message_status), getString(R.string.failure)));
        rcvUserChatGC.scrollToPosition(rcvUserChatGC.getAdapter().getItemCount() - 1);
    }
}