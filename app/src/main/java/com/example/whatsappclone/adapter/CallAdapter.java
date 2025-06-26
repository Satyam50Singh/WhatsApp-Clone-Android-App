package com.example.whatsappclone.adapter;

import static com.example.whatsappclone.utils.Utils.decodeImage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<UserModel> localDataSet;
    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 19991;

    public CallAdapter(Context context, ArrayList<UserModel> localDataSet) {
        this.context = context;
        this.localDataSet = localDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.sample_call_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            UserModel user = localDataSet.get(position);
            holder.tvUserName.setText(user.getUsername());
            holder.tvPhone.setText(user.getPhone());
            if (user.getProfilePicture() != null) {
                if (user.getProfilePicture().startsWith(context.getString(R.string.http))) {
                    Picasso.get().load(user.getProfilePicture()).placeholder(R.drawable.man).into(holder.civProfileImage);
                } else {
                    holder.civProfileImage.setImageBitmap(decodeImage(user.getProfilePicture()));
                }
            }
            holder.ivCall.setOnClickListener(view -> {
                // make call
                Intent mIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91" + user.getPhone()));// Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    //You already have permission
                    try {
                        context.startActivity(mIntent);
                    } catch (SecurityException e) {
                        e.fillInStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Utils.showLog(context.getString(R.string.error), e.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView civProfileImage;
        private final TextView tvUserName;
        private final TextView tvPhone;
        private final ImageView ivCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civProfileImage = itemView.findViewById(R.id.civ_user_call_profile);
            tvUserName = itemView.findViewById(R.id.tv_user_call_name);
            tvPhone = itemView.findViewById(R.id.tv_user_call_phone);
            ivCall = itemView.findViewById(R.id.iv_call);
        }
    }
}
