package com.example.whatsappclone.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.StatusAdapter;
import com.example.whatsappclone.models.Status;
import com.example.whatsappclone.models.StatusModel;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StatusFragment extends Fragment {

    private RecyclerView rcvStatusLists;
    private TextView tvNoRecordFound;
    private FloatingActionButton fabAddStatus;
    private StatusAdapter statusAdapter;
    private ArrayList<StatusModel> userStatuses = new ArrayList<>();
    private Bitmap selectedBitmap;
    private ShimmerFrameLayout shimmerFrameLayout;

    private UserModel userModel;
    private int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 01010101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_status, container, false);
        initUi(rootView);

        return rootView;
    }

    private void initUi(View rootView) {
        rcvStatusLists = rootView.findViewById(R.id.rcv_status_list);
        tvNoRecordFound = rootView.findViewById(R.id.tv_no_record);
        fabAddStatus = rootView.findViewById(R.id.fab_add_status);
        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_user_status_container);

        rcvStatusLists.setVisibility(View.GONE);
        fabAddStatus.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();
        loadStatusData();
        statusAdapter = new StatusAdapter(getContext(), userStatuses, getActivity());
        rcvStatusLists.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvStatusLists.setAdapter(statusAdapter);

        fabAddStatus.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType(Constants.FILE_TYPE);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_ACTIVITY_REQUEST_CODE);
        });
    }

    private void loadStatusData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        firebaseDatabase.getReference().child(Constants.USER_STATUS_COLLECTION_NAME)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userStatuses.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            StatusModel status = new StatusModel();
                            status.setName(dataSnapshot.child("name").getValue(String.class));
                            status.setProfileImage(dataSnapshot.child("profileImage").getValue(String.class));
                            status.setLastUpdated(dataSnapshot.child("lastUpdated").getValue(Long.class));
                            ArrayList<Status> statuses = new ArrayList<>();
                            for (DataSnapshot statusSnapshot : dataSnapshot.child(Constants.STATUSES_COLLECTION_NAME).getChildren()) {
                                Status status1 = statusSnapshot.getValue(Status.class);
                                statuses.add(status1);
                            }
                            status.setStatuses(statuses);
                            userStatuses.add(status);
                        }
                        if (userStatuses.size() == 0) {
                            tvNoRecordFound.setVisibility(View.VISIBLE);
                            rcvStatusLists.setVisibility(View.GONE);
                        } else {
                            rcvStatusLists.setVisibility(View.VISIBLE);
                            tvNoRecordFound.setVisibility(View.GONE);
                        }
                        statusAdapter.notifyDataSetChanged();
                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        rcvStatusLists.setVisibility(View.VISIBLE);
                        fabAddStatus.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        rcvStatusLists.setVisibility(View.GONE);
                        Utils.showToastMessage(getContext(), getString(R.string.no_record_found));
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri;
        if (data != null && data.getData() != null) {
            Utils.showProgressDialog(getContext(), "Uploading Image", getString(R.string.please_wait));
            selectedImageUri = data.getData();
            // uploading image to storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            Date date = new Date();
            StorageReference storageReference = storage.getReference()
                    .child("Status")
                    .child(date.getTime() + "");

            // fetching current user detail
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
            firebaseDatabase.getReference().child(Constants.USER_COLLECTION_NAME)
                    .child(FirebaseAuth.getInstance().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userModel = snapshot.getValue(UserModel.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

            storageReference.putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Utils.hideProgressDialog();
                                            StatusModel statusModel = new StatusModel();
                                            statusModel.setName(userModel.getUsername());
                                            statusModel.setProfileImage(userModel.getProfilePicture());
                                            Date d = new Date();

                                            HashMap<String, Object> obj = new HashMap<>();
                                            obj.put("name", statusModel.getName());
                                            obj.put("profileImage", statusModel.getProfileImage());
                                            obj.put("lastUpdated", d.getTime());

                                            String imageUrl = uri.toString();
                                            Status status = new Status(imageUrl, d.getTime());
                                            firebaseDatabase.getReference()
                                                    .child(Constants.USER_STATUS_COLLECTION_NAME)
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .updateChildren(obj);

                                            firebaseDatabase.getReference()
                                                    .child(Constants.USER_STATUS_COLLECTION_NAME)
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .child(Constants.STATUSES_COLLECTION_NAME)
                                                    .push()
                                                    .setValue(status);
                                        }
                                    });
                        }
                    });
        }
    }
}