package com.gui.mdt.thongsieknavclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Conversation;
import com.gui.mdt.thongsieknavclient.datamodel.Message;

import java.util.List;

import javax.annotation.Nonnull;


public class ConversationMessageAdapter extends RecyclerView.Adapter<ConversationMessageAdapter.MessageViewHolder> {
    private final List<Message> messages;
    private String userName;
    private final Context context;
    NavClientApp mApp;
    public ConversationMessageAdapter(List<Message> messages,Context context) {
        this.messages = messages;
        this.context = context;
        mApp = (NavClientApp) context.getApplicationContext();
    }
    @Nonnull
    @Override
    public ConversationMessageAdapter.MessageViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mvs_conversation_inbox_card_header, parent, false);
        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@Nonnull ConversationMessageAdapter.MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        // Bind conversation data to views
        holder.senderMessage.setText(message.getMessage());
        holder.senderTimeAgo.setText(message.getDate());
        holder.receiverMessage.setText(message.getMessage());
        holder.receiverTimeAgo.setText(message.getDate());

        String senderName = message.getName();
        System.out.println(mApp.getCurrentUserName());
        if (senderName.equals(mApp.getCurrentUserName().toUpperCase())) {
            holder.senderlayout.setVisibility(View.GONE);
            holder.receiverLayout.setVisibility(View.VISIBLE);

        } else {

            holder.senderlayout.setVisibility(View.VISIBLE);
            holder.receiverLayout.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout senderlayout,receiverLayout;
        private TextView senderMessage,receiverMessage;
        private TextView senderTimeAgo,receiverTimeAgo;
        public MessageViewHolder(View itemView) {
            super(itemView);
            senderlayout=itemView.findViewById(R.id.oppoLayout);
            receiverLayout =itemView.findViewById(R.id.receiverLayout);
            senderMessage=itemView.findViewById(R.id.oppoMessage);
            receiverMessage=itemView.findViewById(R.id.receiverMessage);
            senderTimeAgo=itemView.findViewById(R.id.oppoMessageTime);
            receiverTimeAgo=itemView.findViewById(R.id.receiverMessageTime);
        }
    }

}
