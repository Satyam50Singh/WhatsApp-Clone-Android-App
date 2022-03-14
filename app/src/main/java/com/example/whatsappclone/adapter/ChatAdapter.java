package com.example.whatsappclone.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.MessageModel;
import com.example.whatsappclone.ui.activities.ChatDetailActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageModel> localDataSet;
    String receiverId;
    final int SENDER_VIEW_TYPE = 1;
    final int RECEIVER_VIEW_TYPE = 2;
    private Activity activity;

    public ChatAdapter(Activity activity, Context context, ArrayList<MessageModel> localDataSet, String receiverId) {
        this.context = context;
        this.activity = activity;
        this.localDataSet = localDataSet;
        this.receiverId = receiverId;
    }

    @Override
    public int getItemViewType(int position) {
        if (localDataSet.get(position).getMessageId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender_layout, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = localDataSet.get(position);
        holder.itemView.setOnLongClickListener(view -> {
            ((ChatDetailActivity) activity).showActionMode();
            ((ChatDetailActivity) activity).sendMessageDetailMode(messageModel);
            return false;
        });
        Date date = new Date(messageModel.getMessageTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.SimpleDateFormat));
        String messageTime = dateFormat.format(date);
        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).tvSenderMessage.setText(messageModel.getMessageText());
            ((SenderViewHolder) holder).tvSenderTime.setText(messageTime);
        } else {
            ((ReceiverViewHolder) holder).tvReceiverMessage.setText(messageModel.getMessageText());
            ((ReceiverViewHolder) holder).tvReceiverTime.setText(messageTime);
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView tvReceiverMessage, tvReceiverTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverMessage = itemView.findViewById(R.id.tv_receiver_message);
            tvReceiverTime = itemView.findViewById(R.id.tv_receiver_time);

        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView tvSenderMessage, tvSenderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderMessage = itemView.findViewById(R.id.tv_sender_message);
            tvSenderTime = itemView.findViewById(R.id.tv_sender_time);
        }
    }

}
