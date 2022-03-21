package com.example.whatsappclone.adapter;

import static com.example.whatsappclone.utils.Utils.decodeImage;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<UserModel> localDataSet;
    private List<UserModel> localDataSetFull;
    private FirebaseDatabase firebaseDatabase;

    public UserListAdapter(Context context, ArrayList<UserModel> localDataSet) {
        this.context = context;
        this.localDataSet = localDataSet;
        localDataSetFull = new ArrayList<>(localDataSet);
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
        if (localDataSet.get(pos).getProfilePicture() != null && !localDataSet.get(pos).getProfilePicture().startsWith(context.getString(R.string.http))) {
            holder.civProfileImage.setImageBitmap(decodeImage(localDataSet.get(pos).getProfilePicture()));
        } else {
            Picasso.with(context).load(localDataSet.get(position).getProfilePicture()).placeholder(R.drawable.man).into(holder.civProfileImage);
        }
        holder.tvUsername.setText(localDataSet.get(position).getUsername());
        // showing last message & message Time
        String senderRoom = FirebaseAuth.getInstance().getUid() + localDataSet.get(pos).getUserId();
        firebaseDatabase.getReference()
                .child(Constants.CHAT_COLLECTION_NAME)
                .child(senderRoom)
                .orderByChild("messageTime")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                String lastMessageText = snapshot1.child("messageText").getValue().toString();
                                Date date = new Date((Long) snapshot1.child("messageTime").getValue());
                                SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.SimpleDateFormat));
                                String messageTime = dateFormat.format(date);
                                if (lastMessageText.length() > 0 && !lastMessageText.startsWith(context.getString(R.string.firebase_url))) {
                                    holder.tvLastMessage.setText(lastMessageText);
                                } else if (lastMessageText.startsWith(context.getString(R.string.firebase_url))) {
                                    holder.tvLastMessage.setText("Photo");
                                }
                                if (lastMessageText.length() > 0) {
                                    holder.tvTime.setText(messageTime);
                                }
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
            if (localDataSet.get(pos).getProfilePicture() != null && !localDataSet.get(pos).getProfilePicture().startsWith(context.getString(R.string.http))) {
                imageView.setImageBitmap(decodeImage(localDataSet.get(pos).getProfilePicture()));
            } else {
                Picasso.with(context).load(localDataSet.get(position).getProfilePicture()).placeholder(R.drawable.man).into(imageView);
            }
            textView.setText(localDataSet.get(position).getUsername());

            imageView.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, ViewProfilePictureActivity.class);
                intent.putExtra(context.getResources().getString(R.string.username), localDataSet.get(pos).getUsername());
                intent.putExtra(context.getResources().getString(R.string.profileImage), localDataSet.get(pos).getProfilePicture());
                context.startActivity(intent);
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
        TextView tvUsername, tvLastMessage, tvTime;
        LinearLayout llUserList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            llUserList = itemView.findViewById(R.id.ll_user_list);
            civProfileImage = itemView.findViewById(R.id.civ_profile_image);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvTime = itemView.findViewById(R.id.tv_time);

        }
    }

    // search Functionality
    @Override
    public Filter getFilter() {
        return userDataFilter;
    }

    private Filter userDataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<UserModel> filterLocalDataSet = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filterLocalDataSet.addAll(localDataSetFull);
            } else {
                String pattern = charSequence.toString().toLowerCase().trim();
                for (UserModel res : localDataSet) {
                    if (res.getUsername().toLowerCase().contains(pattern)) {
                        filterLocalDataSet.add(res);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterLocalDataSet;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            localDataSet.clear();
            localDataSet.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
