package com.rakib.chatappmini_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.rakib.chatappmini_project.R;
import com.rakib.chatappmini_project.model.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    Context context;
    List<Chat> chatList;

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private static final int MSG_TYPE_JOINING = 2;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;
        ImageView profileImageView;
        TextView dateTimeTextView;

        public ChatViewHolder(@NonNull View view) {
            super(view);
            messageTextView = view.findViewById(R.id.message_tv);
            profileImageView = view.findViewById(R.id.profile_image);
            dateTimeTextView = view.findViewById(R.id.tv_date_time);

        }
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_JOINING) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_chat_item_joined, parent, false);
            return new ChatViewHolder(itemView);
        } else if (viewType == MSG_TYPE_LEFT) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_chat_item_left, parent, false);
            return new ChatViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_chat_item_right, parent, false);
            return new ChatViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.messageTextView.setText(chatList.get(position).getText());
        if (!chatList.get(position).isJoining()) {
            holder.dateTimeTextView.setText(chatList.get(position).getDateTime());
            Glide.with(context)
                    .load(chatList.get(position).getPhotoUrl())
                    .circleCrop()
                    .into(holder.profileImageView);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).isJoining())
            return MSG_TYPE_JOINING;
        else if (chatList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;


    }

}
