package com.example.whatsappclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.ui.activities.ChatDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    Context context;
    ArrayList<UserModel> localDataSet;

    public UserListAdapter(Context context, ArrayList<UserModel> localDataSet) {
        this.context = context;
        this.localDataSet = localDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.with(context).load(localDataSet.get(position).getProfilePicture()).placeholder(R.drawable.man).into(holder.civProfileImage);
        holder.tvUsername.setText(localDataSet.get(position).getUsername());
        holder.tvLastMessage.setText(R.string.last_message);
        holder.civProfileImage.setOnClickListener(view -> {

        });
        holder.llUserList.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatDetailActivity.class);
            intent.putExtra(context.getResources().getString(R.string.userId), localDataSet.get(position).getUserId());
            intent.putExtra(context.getResources().getString(R.string.username), localDataSet.get(position).getUsername());
            intent.putExtra(context.getResources().getString(R.string.profileImage), localDataSet.get(position).getProfilePicture());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civProfileImage;
        TextView tvUsername, tvLastMessage;
        LinearLayout llUserList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            llUserList = itemView.findViewById(R.id.ll_user_list);
            civProfileImage = itemView.findViewById(R.id.civ_profile_image);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);

        }
    }
}
