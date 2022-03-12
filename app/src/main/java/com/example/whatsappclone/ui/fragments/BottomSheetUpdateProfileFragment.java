package com.example.whatsappclone.ui.fragments;

import static com.example.whatsappclone.utils.Utils.checkAndRequestPermission;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

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

    public interface BottomSheetListener {
        void onOptionClick(String text);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_update_profile, container, false);
        CircleImageView civActionCamera = view.findViewById(R.id.civ_action_camera);
        CircleImageView civActionGallery = view.findViewById(R.id.civ_action_gallery);
        ImageView ivRemoveProfileApp = view.findViewById(R.id.iv_remove_profile_app);

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
        ivRemoveProfileApp.setOnClickListener(view13 -> {
            bottomSheetListener.onOptionClick(getString(R.string.remove_profile));
            dismiss();
        });

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