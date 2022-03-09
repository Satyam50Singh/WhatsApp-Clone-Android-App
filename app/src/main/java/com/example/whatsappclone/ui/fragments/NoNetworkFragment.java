package com.example.whatsappclone.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ViewPagerAdapter;
import com.example.whatsappclone.ui.activities.MainActivity;
import com.example.whatsappclone.utils.NetworkManager;
import com.example.whatsappclone.utils.Utils;

public class NoNetworkFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_no_network, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        Button btnReload = rootView.findViewById(R.id.btn_reload);
        btnReload.setOnClickListener(view -> {
            boolean connStatus = NetworkManager.checkNetworkConnectedStatus(getContext());
            if (connStatus) {
                Utils.showToastMessage(getContext(), "Restart Your App!");
            } else {
                Utils.showToastMessage(getContext(), "Connection Status :false");
            }
        });
    }
}