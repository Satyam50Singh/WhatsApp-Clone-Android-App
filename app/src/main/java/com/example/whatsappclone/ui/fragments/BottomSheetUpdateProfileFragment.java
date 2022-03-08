package com.example.whatsappclone.ui.fragments;

import static com.example.whatsappclone.utils.Utils.checkAndRequestPermission;
import static com.example.whatsappclone.utils.Utils.takePictureFromCamera;

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
    private CircleImageView civActionCamera, civActionGallery;
    private ImageView ivRemoveProfileApp;

    public interface BottomSheetListener {
        void onOptionClick(String text);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_update_profile, container, false);
        civActionCamera = view.findViewById(R.id.civ_action_camera);
        civActionGallery = view.findViewById(R.id.civ_action_gallery);
        ivRemoveProfileApp = view.findViewById(R.id.iv_remove_profile_app);

        civActionCamera.setOnClickListener(view1 -> {
            if (checkAndRequestPermission(getActivity())) {
                bottomSheetListener.onOptionClick(getString(R.string.camera));
            }
            dismiss();
        });
        civActionGallery.setOnClickListener(view12 -> {
            bottomSheetListener.onOptionClick(getString(R.string.gallery));
            dismiss();
        });
        ivRemoveProfileApp.setOnClickListener(view13 -> Utils.showToastMessage(getContext(), "Remove Profile"));

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            bottomSheetListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            Utils.showToastMessage(getContext(), e.getMessage());
            throw new ClassCastException();
        }
    }
}