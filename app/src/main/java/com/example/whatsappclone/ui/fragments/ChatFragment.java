package com.example.whatsappclone.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.UserListAdapter;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.ui.activities.LoginWithPhoneActivity;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ChatFragment extends Fragment {

    private ArrayList<UserModel> userList = new ArrayList<>();
    private UserListAdapter userListAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView rcvUserList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        rcvUserList = rootView.findViewById(R.id.rcv_user_list);
        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_user_chat_container);
        rcvUserList.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();
        loadUserRecord();

        userListAdapter = new UserListAdapter(getContext(), userList);
        rcvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvUserList.setAdapter(userListAdapter);
    }

    // fetching users from firebase
    private void loadUserRecord() {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
            firebaseDatabase.getReference().child(Constants.USER_COLLECTION_NAME)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                userModel.getUserId(dataSnapshot.getKey());
                                try {
                                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                                        userList.add(userModel);
                                    }
                                } catch (Exception e) {
                                    Utils.showLog(getString(R.string.error), e.getMessage());
                                }

                            }
                            userListAdapter.notifyDataSetChanged();
                            shimmerFrameLayout.hideShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            rcvUserList.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Utils.hideProgressDialog();
                            Utils.showToastMessage(getContext(), getString(R.string.no_record_found));
                        }
                    });
        } catch (Exception e) {
            Utils.showLog(getString(R.string.error), e.getMessage());
        }

    }

    public void searchUser(String s) {
        if (s != null && s.length() > 0) {
            userListAdapter.getFilter().filter(s);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        firebaseDatabase.getReference()
                .child(Constants.PRESENCE_COLLECTION_NAME)
                .child(FirebaseAuth.getInstance().getUid())
                .setValue(getString(R.string.online));
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

    public void sortUserList() {
        final int[] selectedIndex = new int[1];
        final String[] sortTypesList = new String[]{"Ascending Order", "Descending Order"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.select_sort_order))
                .setIcon(R.drawable.ic_sort_by_alpha)
                .setSingleChoiceItems(sortTypesList, -1, (dialog, whichButton) ->
                {
                    selectedIndex[0] = whichButton;
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int selectedChoiceIndex) {
                        if (selectedIndex[0] == 0) {
                            loadUserRecordsWithSorting("ASC");
                        } else if (selectedIndex[0] == 1) {
                            loadUserRecordsWithSorting("DESC");
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadUserRecordsWithSorting(String order) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        if (order.equals("ASC")) {
            firebaseDatabase.getReference().child(Constants.USER_COLLECTION_NAME)
                    .orderByChild("username")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                userModel.getUserId(dataSnapshot.getKey());
                                try {
                                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                                        userList.add(userModel);
                                    }
                                } catch (Exception e) {
                                    Utils.showLog(getString(R.string.error), e.getMessage());
                                }
                            }
                            userListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Utils.hideProgressDialog();
                            Utils.showToastMessage(getContext(), getString(R.string.no_record_found));
                        }
                    });
        } else {
            firebaseDatabase.getReference().child(Constants.USER_COLLECTION_NAME)
                    .orderByChild("username")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                userModel.getUserId(dataSnapshot.getKey());
                                try {
                                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                                        userList.add(userModel);
                                    }
                                } catch (Exception e) {
                                    Utils.showLog(getString(R.string.error), e.getMessage());
                                }
                            }
                            Collections.reverse(userList);
                            userListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Utils.hideProgressDialog();
                            Utils.showToastMessage(getContext(), getString(R.string.no_record_found));
                        }
                    });
        }

    }
}