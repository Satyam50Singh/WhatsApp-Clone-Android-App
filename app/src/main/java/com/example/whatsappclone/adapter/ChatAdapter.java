package com.example.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageModel> localDataSet;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 1;

    public ChatAdapter(Context context, ArrayList<MessageModel> localDataSet) {
        this.context = context;
        this.localDataSet = localDataSet;
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
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender_layout, parent, false);
            return new SenderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = localDataSet.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder)holder).tvSenderMessage.setText(messageModel.getMessageText());
        } else {
            ((ReceiverViewHolder)holder).tvReceiverMessage.setText(messageModel.getMessageText());
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
