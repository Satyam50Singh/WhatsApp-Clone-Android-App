package com.example.whatsappclone.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.UserListAdapter;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private RecyclerView rcvUserList;
    ArrayList<UserModel> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser  = firebaseAuth.getCurrentUser();
        if(!firebaseUser.isEmailVerified()) {
            Utils.showToastMessage(getContext(), getString(R.string.email_not_verified));
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        rcvUserList = rootView.findViewById(R.id.rcv_user_list);
        userList.add(new UserModel("Satyam Singh", "" , ""));
        userList.add(new UserModel("Sachin Singh", "" , ""));
        userList.add(new UserModel("Vinay Singh", "" , ""));
        userList.add(new UserModel("Neha Pandey", "" , ""));
        userList.add(new UserModel("Satyam Singh", "" , ""));
        userList.add(new UserModel("Sachin Singh", "" , ""));
        userList.add(new UserModel("Vinay Singh", "" , ""));
        userList.add(new UserModel("Neha Pandey", "" , ""));
        UserListAdapter userListAdapter = new UserListAdapter(getContext(), userList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcvUserList.setLayoutManager(layoutManager);
        rcvUserList.setAdapter(userListAdapter);
    }
}