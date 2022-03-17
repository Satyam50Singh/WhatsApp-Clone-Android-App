package com.example.whatsappclone.ui.fragments;

import static com.example.whatsappclone.utils.Utils.encodeImage;
import static com.example.whatsappclone.utils.Utils.getBitmapFromUri;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.StatusAdapter;
import com.example.whatsappclone.models.Status;
import com.example.whatsappclone.models.StatusModel;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.storyview.StoryModel;
import com.example.whatsappclone.storyview.StoryView;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StatusFragment extends Fragment {

    RecyclerView rcvStatusLists;
    TextView tvNoRecordFound;
    FloatingActionButton fabAddStatus;
    StatusAdapter statusAdapter;
    ArrayList<StatusModel> userStatuses = new ArrayList<>();
    Bitmap selectedBitmap;
    private ShimmerFrameLayout shimmerFrameLayout;

    private UserModel userModel;

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
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 8979);
        });
    }

    private void loadStatusData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        firebaseDatabase.getReference().child("User Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userStatuses.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    StatusModel status = new StatusModel();
                    status.setName(dataSnapshot.child("name").getValue(String.class));
                    status.setProfileImage(dataSnapshot.child("profileImage").getValue(String.class));
                    ArrayList<Status> statuses = new ArrayList<>();
                    statuses.clear();
                    for (DataSnapshot statusSnapshot : dataSnapshot.child("statuses").getChildren()) {
                        Status status1 = statusSnapshot.getValue(Status.class);
                        statuses.add(status1);
                    }
                    status.setStatuses(statuses);
                    userStatuses.add(status);
                }
                if (userStatuses.size() <= 0) {
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
            StorageReference storageReference = storage.getReference().child("Status").child(date.getTime() + "");

            // fetching current user detail
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
            firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
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
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Utils.hideProgressDialog();
                                                Utils.showLog("url", uri.toString());
                                                StatusModel statusModel = new StatusModel();
                                                statusModel.setName(userModel.getUsername());
                                                statusModel.setProfileImage(userModel.getProfilePicture());
                                                statusModel.setLastUpdated(date.getTime());

                                                HashMap<String, Object> obj = new HashMap<>();
                                                obj.put("name", statusModel.getName());
                                                obj.put("profileImage", statusModel.getProfileImage());
                                                obj.put("lastUpdated", statusModel.getLastUpdated());

                                                String imageUrl = uri.toString();
                                                Status status = new Status(imageUrl, statusModel.getLastUpdated());
                                                firebaseDatabase.getReference()
                                                        .child("User Status")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .updateChildren(obj);

                                                firebaseDatabase.getReference()
                                                        .child("User Status")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .child("statuses")
                                                        .push()
                                                        .setValue(status);
                                            }
                                        });
                            }
                        }
                    });
        }
    }
}