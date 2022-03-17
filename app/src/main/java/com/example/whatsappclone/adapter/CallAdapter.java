package com.example.whatsappclone.adapter;

import static com.example.whatsappclone.utils.Utils.decodeImage;

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
import com.example.whatsappclone.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder> {

    private Context context;
    private ArrayList<UserModel> localDataSet;

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
        UserModel user = localDataSet.get(position);
        holder.tvUserName.setText(user.getUsername());
        holder.tvPhone.setText(user.getPhone());
        if (user.getProfilePicture().startsWith(context.getString(R.string.http))) {
            Picasso.with(context).load(user.getProfilePicture()).placeholder(R.drawable.man_toolbar).into(holder.civProfileImage);
        } else {
            holder.civProfileImage.setImageBitmap(decodeImage(user.getProfilePicture()));
        }
        holder.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // make call
                Utils.showToastMessage(context, user.getUsername());
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView civProfileImage;
        private TextView tvUserName, tvPhone;
        private ImageView ivCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civProfileImage = itemView.findViewById(R.id.civ_user_call_profile);
            tvUserName = itemView.findViewById(R.id.tv_user_call_name);
            tvPhone = itemView.findViewById(R.id.tv_user_call_phone);
            ivCall = itemView.findViewById(R.id.iv_call);
        }
    }
}
