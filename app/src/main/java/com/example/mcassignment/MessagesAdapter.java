package com.example.mcassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private static final int VIEW_TYPE_SENT = 0;
    private static final int VIEW_TYPE_RECEIVED = 1;

    private final List<Message> messages;
    private final String currentUserId;

    public MessagesAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                viewType == VIEW_TYPE_SENT ?
                        R.layout.item_message_sent :
                        R.layout.item_message_received,
                parent, false
        );
        return new MessageViewHolder(view, currentUserId, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderId().equals(currentUserId) ?
                VIEW_TYPE_SENT :
                VIEW_TYPE_RECEIVED;
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void updateMessages(List<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final TextView timeText;
        private final TextView senderText;
        private final String currentUserId;
        private final int viewType;

        public MessageViewHolder(View itemView, String currentUserId, int viewType) {
            super(itemView);
            this.currentUserId = currentUserId;
            this.viewType = viewType;
            messageText = itemView.findViewById(R.id.message_text);
            timeText = itemView.findViewById(R.id.time_text);
            senderText = viewType == VIEW_TYPE_RECEIVED ?
                    itemView.findViewById(R.id.sender_text) :
                    null;
        }

        public void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(formatTimestamp(message.getTimestamp()));

            if (viewType == VIEW_TYPE_RECEIVED && senderText != null) {
                senderText.setText(
                        message.getSenderId().equals(currentUserId) ? "You" : "Counselor"
                );
            }
        }

        private String formatTimestamp(String timestamp) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                Date date = inputFormat.parse(timestamp);
                return outputFormat.format(date);
            } catch (Exception e) {
                return timestamp;
            }
        }
    }
}