package com.example.whatsappclone.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.StarredMessageAdapter;
import com.example.whatsappclone.models.StarredMessageModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StarredMessageActivity extends AppCompatActivity {

    ArrayList<StarredMessageModel> data;
    StarredMessageAdapter starredMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starred_message);
        init();
    }

    private void init() {
        RecyclerView rcvStarredMessages = findViewById(R.id.rcv_starred_messages);
        loadDataSet();
        rcvStarredMessages.setLayoutManager(new LinearLayoutManager(this));
        starredMessageAdapter = new StarredMessageAdapter(StarredMessageActivity.this, this.data);
        rcvStarredMessages.setAdapter(starredMessageAdapter);
    }

    private void loadDataSet() {
        try {
            Utils.showProgressDialog(StarredMessageActivity.this, "", getString(R.string.please_wait));
            data = new ArrayList<>();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
            firebaseDatabase.getReference().child(Constants.STARRED_MESSAGES_COLLECTION_NAME)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            data.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                StarredMessageModel starredMessageModel = dataSnapshot.getValue(StarredMessageModel.class);
                                starredMessageModel.getId(dataSnapshot.getKey());
                                data.add(starredMessageModel);
                            }
                            starredMessageAdapter.notifyDataSetChanged();
                            Utils.hideProgressDialog();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Utils.showToastMessage(StarredMessageActivity.this, getString(R.string.no_record_found));
                            Utils.hideProgressDialog();
                        }
                    });
        } catch (Exception e) {
            Utils.showLog(getString(R.string.error), e.getMessage());
        }

    }
}