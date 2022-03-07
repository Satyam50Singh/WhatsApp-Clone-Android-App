package com.example.whatsappclone.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetUpdateProfileFragment extends BottomSheetDialogFragment {

    private BottomSheetListener bottomSheetListener;
    CircleImageView civActionCamera, civActionGallery;
    ImageView ivRemoveProfileApp;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_update_profile, container, false);
        civActionCamera = view.findViewById(R.id.civ_action_camera);
        civActionGallery = view.findViewById(R.id.civ_action_gallery);
        ivRemoveProfileApp = view.findViewById(R.id.iv_remove_profile_app);

        civActionCamera.setOnClickListener(view1 -> {
            bottomSheetListener.onOptionClick("Camera Clicked");
            dismiss();
        });
        civActionGallery.setOnClickListener(view12 -> {
            bottomSheetListener.onOptionClick("Gallery Clicked");
            dismiss();
        });
        ivRemoveProfileApp.setOnClickListener(view13 -> Utils.showToastMessage(getContext(), "Remove Profile"));

        return view;
    }

    public interface BottomSheetListener {
        void onOptionClick(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            bottomSheetListener = (BottomSheetListener) context;
        } catch (Exception e) {
            Utils.showToastMessage(getContext(), e.getMessage());
            throw new ClassCastException();
        }
    }
}