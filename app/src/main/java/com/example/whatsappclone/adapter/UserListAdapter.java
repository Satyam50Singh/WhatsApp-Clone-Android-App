package com.example.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.UserModel;
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
        holder.tvLastMessage.setText("last message");
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civProfileImage;
        TextView tvUsername, tvLastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfileImage = itemView.findViewById(R.id.civ_profile_image);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
        }
    }
}
