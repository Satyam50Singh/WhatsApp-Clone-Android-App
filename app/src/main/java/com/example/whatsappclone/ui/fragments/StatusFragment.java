package com.example.whatsappclone.ui.fragments;

import static com.example.whatsappclone.utils.Utils.encodeImage;
import static com.example.whatsappclone.utils.Utils.getBitmapFromUri;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.StatusAdapter;
import com.example.whatsappclone.models.StatusModel;
import com.example.whatsappclone.ui.activities.SettingsActivity;
import com.example.whatsappclone.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class StatusFragment extends Fragment {

    RecyclerView gvStatus;
    TextView tvNoRecordFound;
    FloatingActionButton fabAddStatus;
    StatusAdapter statusAdapter;
    ArrayList<StatusModel> statusModels = new ArrayList<>();
    Bitmap selectedBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_status, container, false);
        initUi(rootView);
        return rootView;
    }

    private void initUi(View rootView) {
        gvStatus = rootView.findViewById(R.id.rcv_status_list);
        tvNoRecordFound = rootView.findViewById(R.id.tv_no_record);
        fabAddStatus = rootView.findViewById(R.id.fab_add_status);

        StatusModel statusModel = new StatusModel();
        statusModel.setName("Satya Singh");
        statusModels.add(statusModel);
        statusModels.add(statusModel);
        statusModels.add(statusModel);
        statusModels.add(statusModel);
        statusModels.add(statusModel);
        statusAdapter = new StatusAdapter(getContext(), statusModels);
        gvStatus.setLayoutManager(new LinearLayoutManager(getContext()));
        gvStatus.setAdapter(statusAdapter);

        fabAddStatus.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 8979);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri;
        if (data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            selectedBitmap = getBitmapFromUri(selectedImageUri, getContext());
            if (selectedBitmap != null) {
                String profileEncodedString = encodeImage(selectedBitmap); // converting bitmap to base64 string
                
            }
            Utils.showToastMessage(getContext(), selectedBitmap.toString());
        }
    }
}