package com.example.whatsappclone.ui.activities;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.StarredMessageAdapter;
import com.example.whatsappclone.models.StarredMessageModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StarredMessageActivity extends AppCompatActivity {

    private ArrayList<StarredMessageModel> data;
    private StarredMessageAdapter starredMessageAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView rcvStarredMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_starred_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0b6156")));
        init();
    }

    private void init() {
        rcvStarredMessages = findViewById(R.id.rcv_starred_messages);
        shimmerFrameLayout = findViewById(R.id.shimmer_starred_message_container);
        loadDataSet();
        starredMessageAdapter = new StarredMessageAdapter(StarredMessageActivity.this, this.data);
        rcvStarredMessages.setLayoutManager(new LinearLayoutManager(this));
        rcvStarredMessages.setAdapter(starredMessageAdapter);
    }

    private void loadDataSet() {
        try {
            rcvStarredMessages.setVisibility(View.GONE);
            shimmerFrameLayout.startShimmer();
            data = new ArrayList<>();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
            firebaseDatabase.getReference()
                    .child(Constants.STARRED_MESSAGES_COLLECTION_NAME)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            data.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                StarredMessageModel starredMessageModel = dataSnapshot.getValue(StarredMessageModel.class);
                                data.add(starredMessageModel);
                            }
                            starredMessageAdapter.notifyDataSetChanged();
                            if (data.size() == 0) {
                                Utils.showToastMessage(StarredMessageActivity.this, getString(R.string.no_star_message));
                            }
                            shimmerFrameLayout.stopShimmer();
                            rcvStarredMessages.setVisibility(View.VISIBLE);
                            shimmerFrameLayout.setVisibility(View.GONE);
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