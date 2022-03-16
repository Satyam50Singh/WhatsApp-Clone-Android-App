package com.example.whatsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.Status;
import com.example.whatsappclone.models.StatusModel;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.storyview.StoryModel;
import com.example.whatsappclone.storyview.StoryView;
import com.example.whatsappclone.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private Context context;
    private ArrayList<StatusModel> localDataSet;
    private FragmentActivity activity;

    public StatusAdapter(Context context, ArrayList<StatusModel> localDataSet, FragmentActivity activity) {
        this.context = context;
        this.localDataSet = localDataSet;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_status_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatusModel userStatus = localDataSet.get(position);
        holder.tvUserName.setText(userStatus.getName());
        Date date = new Date(userStatus.getLastUpdated());
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.date_format_ymd));
        String messageTime = dateFormat.format(date);
        holder.tvStatusTime.setText(messageTime);
        // for viewing status
        holder.storyView.setActivityContext((AppCompatActivity) activity);
        holder.storyView.resetStoryVisits();
        ArrayList<StoryModel> uris = new ArrayList<>();
        for (Status status : userStatus.getStatuses()) {
            Date date1 = new Date(status.getTimeStamp());
            SimpleDateFormat dateFormat1 = new SimpleDateFormat(context.getString(R.string.date_and_time_format));
            String messageTime1 = dateFormat.format(date);
            uris.add(new StoryModel(status.getImageUrl(), userStatus.getName(), messageTime1));
        }
        holder.storyView.setImageUris(uris);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvStatusTime;
        StoryView storyView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storyView = itemView.findViewById(R.id.storyView_statuses);
            tvUserName = itemView.findViewById(R.id.tv_status_username);
            tvStatusTime = itemView.findViewById(R.id.tv_status_last_updated_time);
        }
    }
}
