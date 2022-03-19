package com.example.whatsappclone.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.StarredMessageModel;
import com.example.whatsappclone.ui.activities.ChatDetailActivity;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class StarredMessageAdapter extends RecyclerView.Adapter<StarredMessageAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<StarredMessageModel> localDataSet;

    public StarredMessageAdapter(Context context, ArrayList<StarredMessageModel> data) {
        this.context = context;
        this.localDataSet = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.starred_message_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Utils.showLog("tag", localDataSet.get(position).getMessageText());
        holder.tvReceiverMessage.setText(localDataSet.get(position).getMessageText());
        holder.tvSenderName.setText(localDataSet.get(position).getSenderName());
        holder.tvReceiverName.setText(localDataSet.get(position).getReceiverName());
        Date date = new Date(localDataSet.get(position).getMessageTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.SimpleDateFormat));
        String messageTime = dateFormat.format(date);
        holder.tvReceiverTime.setText(messageTime);

        holder.ivRemoveStar.setOnClickListener(view -> {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
            firebaseDatabase.getReference()
                    .child(Constants.STARRED_MESSAGES_COLLECTION_NAME)
                    .child(localDataSet.get(position).getMessageId())
                    .removeValue()
                    .addOnSuccessListener(unused -> Utils.showToastMessage(context, context.getString(R.string.message_removed_successfully)));
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverMessage, tvReceiverTime, tvSenderName, tvReceiverName;
        ImageView ivRemoveStar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverMessage = itemView.findViewById(R.id.tv_receiver_message_starred);
            tvReceiverTime = itemView.findViewById(R.id.tv_receiver_time_starred);
            tvSenderName = itemView.findViewById(R.id.tv_sender_name_starred);
            tvReceiverName = itemView.findViewById(R.id.tv_receiver_name_starred);
            ivRemoveStar = itemView.findViewById(R.id.iv_remove_star);
        }
    }
}
