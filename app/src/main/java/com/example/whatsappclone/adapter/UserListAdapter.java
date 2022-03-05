package com.example.whatsappclone.adapter;

import android.app.Dialog;
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
import com.example.whatsappclone.ui.activities.ViewProfilePictureActivity;
import com.example.whatsappclone.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    Context context;
    ArrayList<UserModel> localDataSet;
    FirebaseDatabase firebaseDatabase;

    public UserListAdapter(Context context, ArrayList<UserModel> localDataSet) {
        this.context = context;
        this.localDataSet = localDataSet;
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        Picasso.with(context).load(localDataSet.get(position).getProfilePicture()).placeholder(R.drawable.man).into(holder.civProfileImage);
        holder.tvUsername.setText(localDataSet.get(position).getUsername());
        // showing last message
        String senderRoom = FirebaseAuth.getInstance().getUid() + localDataSet.get(pos).getUserId();
        firebaseDatabase.getReference()
                .child("Chats")
                .child(senderRoom)
                .orderByChild("messageTime")
                .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                        holder.tvLastMessage.setText(snapshot1.child("messageText").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.civProfileImage.setOnClickListener(view -> {

            Dialog dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_profile_dialog);
            ImageView imageView = dialog.findViewById(R.id.iv_dialog_profile);
            TextView textView = dialog.findViewById(R.id.tv_dialog_username);

            Picasso.with(context).load(localDataSet.get(position).getProfilePicture()).placeholder(R.drawable.man).into(imageView);
            textView.setText(localDataSet.get(position).getUsername());

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewProfilePictureActivity.class);
                    intent.putExtra(context.getResources().getString(R.string.username), localDataSet.get(pos).getUsername());
                    intent.putExtra(context.getResources().getString(R.string.profileImage), localDataSet.get(pos).getProfilePicture());
                    context.startActivity(intent);
                }
            });
            dialog.show();
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
