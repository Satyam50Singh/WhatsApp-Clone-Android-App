package com.example.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.StarredMessageModel;
import com.example.whatsappclone.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class StarredMessageAdapter extends RecyclerView.Adapter<StarredMessageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<StarredMessageModel> localDataSet;

    public StarredMessageAdapter(Context context, ArrayList<StarredMessageModel> data) {
        this.context = context;
        this.localDataSet = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Utils.showLog("tag",localDataSet.get(position).getMessageText());
        holder.tvReceiverMessage.setText(localDataSet.get(position).getMessageText());
        Date date = new Date(localDataSet.get(position).getMessageTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:MM");
        String messageTime = dateFormat.format(date);
        holder.tvReceiverTime.setText(messageTime);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverMessage, tvReceiverTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverMessage = itemView.findViewById(R.id.tv_receiver_message);
            tvReceiverTime = itemView.findViewById(R.id.tv_receiver_time);
        }
    }
}
