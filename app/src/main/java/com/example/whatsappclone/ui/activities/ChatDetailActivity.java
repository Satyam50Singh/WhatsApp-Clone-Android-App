package com.example.whatsappclone.ui.activities;

import static com.example.whatsappclone.utils.Utils.decodeImage;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.ActionMode;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ChatAdapter;
import com.example.whatsappclone.models.MessageModel;
import com.example.whatsappclone.models.StarredMessageModel;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private String senderId, receiverId, username, profileImage, senderRoom, receiverRoom, messageId;
    private Toolbar toolbar;
    private TextView tvReceiverName, tvReceiverPresence;
    private CircleImageView civProfileImage;
    private RecyclerView rcvUserChat;
    private EditText etMessage;

    private final ArrayList<MessageModel> chatRecord = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private static android.view.ActionMode mActionMode = null;

    private StarredMessageModel starredMessageModel;
    private String messageText;

    private static final int PERMISSION_REQUEST_CODE = 101;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_detail);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));

        View decorView = getWindow().getDecorView();
        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), decorView);
        insetsController.setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        checkReceiverPresence();
    }

    private void checkReceiverPresence() {
        firebaseDatabase.getReference()
                .child(Constants.PRESENCE_COLLECTION_NAME)
                .child(receiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String presence = snapshot.getValue(String.class);
                            if (!presence.isEmpty()) {
                                tvReceiverPresence.setText(presence);
                                tvReceiverPresence.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        firebaseStorage = FirebaseStorage.getInstance();

        // reference to controls
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView ivBackArrow = toolbar.findViewById(R.id.iv_back_arrow);
        civProfileImage = toolbar.findViewById(R.id.civ_chat_profile_image);
        tvReceiverName = toolbar.findViewById(R.id.tv_receiver_name);
        tvReceiverPresence = toolbar.findViewById(R.id.tv_receiver_presence);
        rcvUserChat = findViewById(R.id.rcv_user_chat);
        LinearLayout llSentBtn = findViewById(R.id.ll_send_btn);
        ImageView ivSendImageButton = findViewById(R.id.iv_send_image_button);
        etMessage = findViewById(R.id.et_message);
        etMessage.requestFocus();

        // getting values by intent
        getIntentValues();

        // setting user details in toolbar
        setUserDetailsOnToolbar();

        // click listeners
        ivBackArrow.setOnClickListener(view -> {
            finish();
        });

        // fetching chat and storing in chatRecord ArrayList
        Utils.showProgressDialog(ChatDetailActivity.this, "", getString(R.string.please_wait));
        loadChatMessages();

        chatAdapter = new ChatAdapter(getApplicationContext(), chatRecord, ChatDetailActivity.this, senderRoom, receiverRoom);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatDetailActivity.this);
        rcvUserChat.setLayoutManager(layoutManager);
        rcvUserChat.setAdapter(chatAdapter);
        rcvUserChat.postDelayed(() -> rcvUserChat.scrollToPosition(rcvUserChat.getAdapter().getItemCount() - 1), 1000);

        llSentBtn.setOnClickListener(view -> storingMessagesInFirebaseDatabase());

        tvReceiverName.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailActivity.this, ReceiverUserProfile.class);
            intent.putExtra(getString(R.string.receiver_id), receiverId);
            intent.putExtra(getString(R.string.receiver_name), username);
            intent.putExtra(getString(R.string.profileImage), profileImage);
            startActivity(intent);
        });


        // Register image picker result handler
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        Utils.showProgressDialog(ChatDetailActivity.this, getString(R.string.uploading), getString(R.string.please_wait));

                        Calendar calendar = Calendar.getInstance();
                        StorageReference storageReference = firebaseStorage.getReference()
                                .child(Constants.CHAT_COLLECTION_NAME)
                                .child(calendar.getTimeInMillis() + "");

                        storageReference.putFile(selectedImage)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                            Utils.hideProgressDialog();
                                            String filePath = uri.toString();
                                            if (filePath.isEmpty()) return;

                                            etMessage.requestFocus();
                                            String randomKey = firebaseDatabase.getReference().push().getKey();
                                            MessageModel model = new MessageModel(randomKey, senderId, filePath, new Date().getTime());

                                            if (senderId != null && receiverId != null) {
                                                firebaseDatabase.getReference()
                                                        .child(Constants.CHAT_COLLECTION_NAME)
                                                        .child(senderId + receiverId)
                                                        .child(randomKey)
                                                        .setValue(model)
                                                        .addOnSuccessListener(unused -> firebaseDatabase.getReference()
                                                                .child(Constants.CHAT_COLLECTION_NAME)
                                                                .child(receiverRoom)
                                                                .child(randomKey)
                                                                .setValue(model)
                                                                .addOnSuccessListener(unused1 -> rcvUserChat.scrollToPosition(rcvUserChat.getAdapter().getItemCount() - 1)));
                                            } else {
                                                Utils.showLog("Ids", "senderId : " + senderId + " receiverId : " + receiverId);
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("TAG", "Upload Failed: " + e.getMessage()));
                    }
                }
        );


        ivSendImageButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            PERMISSION_REQUEST_CODE);
                } else {
                    openImagePicker();
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                } else {
                    openImagePicker();
                }
            }
        });

        final Handler handler = new Handler();

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                firebaseDatabase.getReference().child(Constants.PRESENCE_COLLECTION_NAME)
                        .child(senderId).setValue("typing ...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping, 1000);

            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    firebaseDatabase.getReference().child(Constants.PRESENCE_COLLECTION_NAME)
                            .child(senderId).setValue("Online");
                }
            };
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied to access media", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // Or use Constants.FILE_TYPE
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }

    private void getIntentValues() {
        try {
            senderId = firebaseAuth.getUid();
            receiverId = getIntent().getStringExtra(getString(R.string.userId));
            username = getIntent().getStringExtra(getString(R.string.username));
            profileImage = getIntent().getStringExtra(getString(R.string.profileImage));
            if (senderId != null && receiverId != null) {
                senderRoom = senderId + receiverId;
                receiverRoom = receiverId + senderId;
            }
        } catch (Exception e) {
            Utils.showLog(getString(R.string.error), e.getMessage());
        }
    }

    private void setUserDetailsOnToolbar() {
        Picasso.get().load(profileImage).placeholder(R.drawable.man_toolbar).into(civProfileImage);
        if (profileImage != null && !profileImage.startsWith(getString(R.string.http))) {
            civProfileImage.setImageBitmap(decodeImage(profileImage));
        } else {
            Picasso.get().load(profileImage).placeholder(R.drawable.man).into(civProfileImage);
        }
        tvReceiverName.setText(username);
    }

    private void loadChatMessages() {
        try {
            firebaseDatabase.getReference()
                    .child(Constants.CHAT_COLLECTION_NAME)
                    .child(senderId + receiverId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            chatRecord.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                MessageModel message = snapshot1.getValue(MessageModel.class);
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
        } catch (Exception e) {
            Utils.showLog("Error : ", e.getMessage());
        }
    }

    private void storingMessagesInFirebaseDatabase() {
        try {
            String message = etMessage.getText().toString();
            if (message.isEmpty()) {
                return;
            }
            etMessage.setText("");
            etMessage.requestFocus();
            String randomKey = firebaseDatabase.getReference().push().getKey();
            MessageModel model = new MessageModel(randomKey, senderId, message, new Date().getTime());
            if (senderId != null && receiverId != null) {
                firebaseDatabase.getReference()
                        .child(Constants.CHAT_COLLECTION_NAME)
                        .child(senderId + receiverId)
                        .child(randomKey)
                        .setValue(model)
                        .addOnSuccessListener(unused -> firebaseDatabase.getReference()
                                .child(Constants.CHAT_COLLECTION_NAME)
                                .child(receiverRoom)
                                .child(randomKey)
                                .setValue(model)
                                .addOnSuccessListener(unused1 -> rcvUserChat.scrollToPosition(rcvUserChat.getAdapter().getItemCount() - 1)));
            } else {
                Utils.showLog("Ids", "senderId : " + senderId + " receiverId : " + receiverId);
            }
        } catch (Exception e) {
            Utils.showLog(getString(R.string.error), e.getMessage());
        }
    }

    public ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            /* when you start action mode by calling action mode method,
             * system will call this method */
            // inflate menu item here
            actionMode.getMenuInflater().inflate(R.menu.chat_menu, menu);
            actionMode.setTitle(username);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            int id = menuItem.getItemId();

            if (id == R.id.action_copy) {
                copyContentToClipBoard();
                mActionMode.finish();
                return true;
            } else if (id == R.id.action_starred) {
                addToStaredMessagesBox();
                mActionMode.finish();
                return true;
            } else if (id == R.id.action_delete) {
                deleteMessage();
                mActionMode.finish();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            // when user leave contextual action mode, system call this method
            mActionMode = null;
        }
    };

    private void copyContentToClipBoard() {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Text", messageText);
        manager.setPrimaryClip(clipData);
        Utils.showToastMessage(ChatDetailActivity.this, getString(R.string.copied));
    }

    // method for active action contextual mode
    public void showActionMode() {
        if (mActionMode != null) {
            return;
        }

        mActionMode = toolbar.startActionMode(callback);
    }

    public static void hideActionMode() {
        mActionMode.finish();
    }

    private void
    addToStaredMessagesBox() {
        String randomKey = firebaseDatabase.getReference().push().getKey();
        starredMessageModel.setMessageId(randomKey);
        firebaseDatabase.getReference()
                .child(Constants.STARRED_MESSAGES_COLLECTION_NAME)
                .child(randomKey)
                .setValue(starredMessageModel);
    }

    public void sendMessageDetailMode(MessageModel messageModel) {
        messageText = messageModel.getMessageText();
        messageId = messageModel.getMessageId();
        starredMessageModel = new StarredMessageModel();
        starredMessageModel.setId(receiverId);
        starredMessageModel.setMessageText(messageModel.getMessageText());
        starredMessageModel.setMessageTime(messageModel.getMessageTime());
        starredMessageModel.setSenderProfilePicture(profileImage);

        firebaseDatabase.getReference()
                .child(Constants.USER_COLLECTION_NAME)
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            if (messageModel.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                                starredMessageModel.setSenderName(userModel.getUsername());
                                starredMessageModel.setReceiverName(username);
                            } else {
                                starredMessageModel.setSenderName(username);
                                starredMessageModel.setReceiverName(userModel.getUsername());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    // deleting message
    private void deleteMessage() {
        new AlertDialog.Builder(ChatDetailActivity.this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    firebaseDatabase.getReference()
                            .child(Constants.CHAT_COLLECTION_NAME)
                            .child(senderRoom)
                            .child(messageId)
                            .removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Utils.showToastMessage(ChatDetailActivity.this, getString(R.string.message_deleted_successfully));
                                }
                            });
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();

    }


    @Override
    public void onResume() {
        super.onResume();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        if (FirebaseAuth.getInstance().getUid() != null) {
            firebaseDatabase.getReference()
                    .child(Constants.PRESENCE_COLLECTION_NAME)
                    .child(FirebaseAuth.getInstance().getUid())
                    .setValue(getString(R.string.online));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        if (FirebaseAuth.getInstance().getUid() != null) {
            firebaseDatabase.getReference()
                    .child(Constants.PRESENCE_COLLECTION_NAME)
                    .child(FirebaseAuth.getInstance().getUid())
                    .setValue(getString(R.string.offline));
        }
    }
}