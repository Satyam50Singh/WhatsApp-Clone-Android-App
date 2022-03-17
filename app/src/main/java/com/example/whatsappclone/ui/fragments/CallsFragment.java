package com.example.whatsappclone.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.CallAdapter;
import com.example.whatsappclone.models.UserModel;

import java.util.ArrayList;

public class CallsFragment extends Fragment {

    private RecyclerView rcvUserCallList;
    private CallAdapter callAdapter;
    private ArrayList<UserModel> userWithPhoneList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_calls, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View rootView) {
        rcvUserCallList = rootView.findViewById(R.id.rcv_user_call_list);
        loadUsers();
        callAdapter = new CallAdapter(getContext(), userWithPhoneList);
        rcvUserCallList.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvUserCallList.setAdapter(callAdapter);
    }

    private void loadUsers() {
        UserModel userModel = new UserModel();
        userModel.setPhone("+91-7019765765");
        userModel.setUsername("Thakur Satyam Singh");
        userModel.setProfilePicture("https://www.flaticon.com/free-sticker/happy_6983864");
        userWithPhoneList.add(userModel);
        userWithPhoneList.add(userModel);
        userWithPhoneList.add(userModel);
        userWithPhoneList.add(userModel);
        userWithPhoneList.add(userModel);
        userWithPhoneList.add(userModel);
        userWithPhoneList.add(userModel);
        userWithPhoneList.add(userModel);

    }
}