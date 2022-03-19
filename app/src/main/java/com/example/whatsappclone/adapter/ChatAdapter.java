package com.example.whatsappclone.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.MessageModel;
import com.example.whatsappclone.ui.activities.ChatDetailActivity;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<MessageModel> localDataSet;
    private Activity activity;
    private String senderRoom, receiverRoom, receiverId;

    final int SENDER_VIEW_TYPE = 1;
    final int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(Context context, ArrayList<MessageModel> localDataSet, String receiverId, Activity activity, String senderRoom, String receiverRoom) {
        this.context = context;
        this.localDataSet = localDataSet;
        this.receiverId = receiverId;
        this.activity = activity;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    public ChatAdapter(Activity activity, Context context, ArrayList<MessageModel> localDataSet, String receiverId) {
        this.context = context;
        this.activity = activity;
        this.localDataSet = localDataSet;
        this.receiverId = receiverId;
    }

    @Override
    public int getItemViewType(int position) {
        if (localDataSet.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())) {
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

        // configuration for android reactions
        int[] reactions = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass() == SenderViewHolder.class) {
                if (pos >= 0) {
                    ((SenderViewHolder) holder).ivSenderFeeling.setImageResource(reactions[pos]);
                    ((SenderViewHolder) holder).ivSenderFeeling.setVisibility(View.VISIBLE);
                }
            } else {
                if (pos >= 0) {
                    ((ReceiverViewHolder) holder).ivReceiverFeeling.setImageResource(reactions[pos]);
                    ((ReceiverViewHolder) holder).ivReceiverFeeling.setVisibility(View.VISIBLE);
                }
            }
            messageModel.setFeeling(pos);
            FirebaseDatabase.getInstance(Constants.DB_PATH).getReference()
                    .child(Constants.CHAT_COLLECTION_NAME)
                    .child(senderRoom)
                    .child(messageModel.getMessageId())
                    .setValue(messageModel);
            FirebaseDatabase.getInstance(Constants.DB_PATH).getReference()
                    .child(Constants.CHAT_COLLECTION_NAME)
                    .child(receiverRoom)
                    .child(messageModel.getMessageId())
                    .setValue(messageModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (pos >= 0) {
                                    ChatDetailActivity.hideActionMode();
                                }
                            }
                        }
                    });
            return true; // true is closing popup, false is requesting a new selection
        });

        // --------------------------------------------------------------------

        Date date = new Date(messageModel.getMessageTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.SimpleDateFormat));
        String messageTime = dateFormat.format(date);
        if (holder.getClass() == SenderViewHolder.class) {
            if (messageModel.getMessageText().startsWith(context.getString(R.string.firebase_url))) {
                ((SenderViewHolder) holder).ivSenderImage.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).tvSenderMessage.setVisibility(View.GONE);
                Picasso.with(context).load(messageModel.getMessageText()).into(((SenderViewHolder) holder).ivSenderImage);
            } else {
                ((SenderViewHolder) holder).tvSenderMessage.setText(messageModel.getMessageText());
            }
            ((SenderViewHolder) holder).tvSenderTime.setText(messageTime);
            if (messageModel.getFeeling() > -1) {
                ((SenderViewHolder) holder).ivSenderFeeling.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).ivSenderFeeling.setImageResource(reactions[messageModel.getFeeling()]);
            } else {
                ((SenderViewHolder) holder).ivSenderFeeling.setVisibility(View.GONE);
            }
            ((SenderViewHolder) holder).tvSenderMessage.setOnTouchListener((view, motionEvent) -> {
                // opening actionbar icons
                ((ChatDetailActivity) activity).showActionMode();
                ((ChatDetailActivity) activity).sendMessageDetailMode(messageModel);
                // opening reaction
                popup.onTouch(view, motionEvent);
                return false;
            });
            ((SenderViewHolder) holder).ivSenderImage.setOnTouchListener((view, motionEvent) -> {
                // opening actionbar icons
                ((ChatDetailActivity) activity).showActionMode();
                ((ChatDetailActivity) activity).sendMessageDetailMode(messageModel);
                // opening reaction
                popup.onTouch(view, motionEvent);
                return false;
            });
        } else {
            if (messageModel.getMessageText().startsWith(context.getString(R.string.firebase_url))) {
                ((ReceiverViewHolder) holder).ivReceiverImage.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).tvReceiverMessage.setVisibility(View.GONE);
                Picasso.with(context).load(messageModel.getMessageText()).into(((ReceiverViewHolder) holder).ivReceiverImage);
            } else {
                ((ReceiverViewHolder) holder).tvReceiverMessage.setText(messageModel.getMessageText());
            }
            ((ReceiverViewHolder) holder).tvReceiverMessage.setText(messageModel.getMessageText());
            ((ReceiverViewHolder) holder).tvReceiverTime.setText(messageTime);
            if (messageModel.getFeeling() >= 0) {
                ((ReceiverViewHolder) holder).ivReceiverFeeling.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).ivReceiverFeeling.setImageResource(reactions[messageModel.getFeeling()]);
            } else {
                ((ReceiverViewHolder) holder).ivReceiverFeeling.setVisibility(View.VISIBLE);
            }

            ((ReceiverViewHolder) holder).tvReceiverMessage.setOnTouchListener((view, motionEvent) -> {
                // opening actionbar icons
                ((ChatDetailActivity) activity).showActionMode();
                ((ChatDetailActivity) activity).sendMessageDetailMode(messageModel);
                // opening reaction
                popup.onTouch(view, motionEvent);
                return false;
            });

            ((ReceiverViewHolder) holder).ivReceiverImage.setOnTouchListener((view, motionEvent) -> {
                // opening actionbar icons
                ((ChatDetailActivity) activity).showActionMode();
                ((ChatDetailActivity) activity).sendMessageDetailMode(messageModel);
                // opening reaction
                popup.onTouch(view, motionEvent);
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView tvReceiverMessage, tvReceiverTime;
        ImageView ivReceiverFeeling, ivReceiverImage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverMessage = itemView.findViewById(R.id.tv_receiver_message);
            tvReceiverTime = itemView.findViewById(R.id.tv_receiver_time);
            ivReceiverFeeling = itemView.findViewById(R.id.iv_receiver_reaction);
            ivReceiverImage = itemView.findViewById(R.id.iv_receiver_image);
        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView tvSenderMessage, tvSenderTime;
        ImageView ivSenderFeeling, ivSenderImage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderMessage = itemView.findViewById(R.id.tv_sender_message);
            tvSenderTime = itemView.findViewById(R.id.tv_sender_time);
            ivSenderFeeling = itemView.findViewById(R.id.iv_sender_reaction);
            ivSenderImage = itemView.findViewById(R.id.iv_sender_image);

        }
    }

}
