package com.gui.mdt.thongsieknavclient.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gui.mdt.thongsieknavclient.R;
import java.util.List;
import android.widget.ImageView;
import com.gui.mdt.thongsieknavclient.datamodel.Conversation;
import javax.annotation.Nonnull;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private List<Conversation> conversations;

    public ConversationAdapter(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    @Nonnull
    @Override
    public ConversationViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mvs_conversation_card_header, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nonnull ConversationViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);

        // Bind conversation data to views
        holder.senderNameTextView.setText(conversation.getSender());
        holder.lastMessageTextView.setText(conversation.getBody());
        holder.customerBalanceTextView.setText(String.valueOf(conversation.getDatetime()));
        int messageCount = Integer.parseInt(String.valueOf(conversation.getMessageCount()));
        if (messageCount > 0) {
            holder.badgCount.setVisibility(View.VISIBLE);
            holder.badgCount.setText(""+messageCount);
        } else {
            holder.badgCount.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView senderNameTextView;
        TextView lastMessageTextView;
        TextView customerBalanceTextView;
        TextView badgCount;

        public ConversationViewHolder(@Nonnull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            senderNameTextView = itemView.findViewById(R.id.tvSenderName);
            lastMessageTextView = itemView.findViewById(R.id.tvLastMessage);
            customerBalanceTextView = itemView.findViewById(R.id.tvCustomerBalance);
            badgCount =itemView.findViewById(R.id.tvBadgeCount);
        }
    }
}
