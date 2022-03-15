package com.example.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.StatusModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private Context context;
    private ArrayList<StatusModel> localDataSet;

    public StatusAdapter(Context context, ArrayList<StatusModel> localDataSet) {
        this.context = context;
        this.localDataSet = localDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_status_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUserName.setText(localDataSet.get(position).getName());
//        Picasso.with(context).load(localDataSet.get(position).getStatuses().get(0).getImageUrl()).placeholder(R.drawable.man).into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
//        ImageView ivImage;
        TextView tvUserName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //ivImage =  itemView.findViewById(R.id.iv_gallery);
            tvUserName = itemView.findViewById(R.id.tv_status_username);
        }
    }
}
