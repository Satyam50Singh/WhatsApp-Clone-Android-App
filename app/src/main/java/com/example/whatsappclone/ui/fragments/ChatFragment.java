package com.example.whatsappclone.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.UserListAdapter;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    ArrayList<UserModel> userList = new ArrayList<>();
    UserListAdapter userListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        RecyclerView rcvUserList = rootView.findViewById(R.id.rcv_user_list);
        Utils.showProgressDialog(getContext(), "", getString(R.string.please_wait));
        loadUserRecord();
        userListAdapter = new UserListAdapter(getContext(), userList);
        rcvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvUserList.setAdapter(userListAdapter);
    }

    // fetching users from firebase
    private void loadUserRecord() {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
            firebaseDatabase.getReference().child(Constants.USER_COLLECTION_NAME).addValueEventListener(new ValueEventListener() {
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
                    Utils.hideProgressDialog();
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
        if (s != null || s.length() > 0) {
            userListAdapter.getFilter().filter(s);
        }
    }
}