package com.gui.mdt.thongsieknavclient.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.ConversationAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Conversation;
import com.gui.mdt.thongsieknavclient.datamodel.Message;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.github.marlonlom.utilities.timeago.TimeAgo;
public class MvsConversationsListActivity extends AppCompatActivity {
    NavClientApp mApp;

    Set<Conversation> processedKeys;

    Toolbar myToolbar;
    Button btnSearch;
    boolean IsMso, IsMvs;
    private RecyclerView recyclerViewSalesCustomers;
    private Bundle extras;
    private String filterCustomerName = "",
            filterCustomerCode = "",
            filterCustomerPriceGroup = "",
            filterSalesPersonCode="",
            formName ="",
            searchText="";


    private ProgressDialog xProgressDialog;
    boolean isSearchButtonClicked = false;
    private EditText txtSearch;
    private DatabaseReference databaseReference;
    private List<Conversation> conversations;
    private ConversationAdapter adapter;
    private String senderName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_list);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Customer List");
        mApp = (NavClientApp) getApplicationContext();

        xProgressDialog = new ProgressDialog(MvsConversationsListActivity .this);
        xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        conversations = new ArrayList<>();
        retrieveDataFromRealtimeDatabase();
        adapter = new ConversationAdapter(conversations);

        RecyclerView recyclerViewConversations = (RecyclerView) findViewById(R.id.recyclerViewConversations);
        recyclerViewConversations.setAdapter(adapter);
        recyclerViewConversations.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewConversations.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());

                if (childView != null && e.getAction() == MotionEvent.ACTION_UP) {
                    int position = rv.getChildAdapterPosition(childView);
                    Conversation selectedConversation = conversations.get(position);
                    Intent intent = new Intent(MvsConversationsListActivity.this, MvsConversationsMessageListActivity.class);
                    intent.putExtra("senderName", selectedConversation.getSender());
                    markMessagesAsReadForSender( selectedConversation.getSender());
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // Handle touch events if needed
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // Handle request to disallow touch event interception if needed
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void retrieveDataFromRealtimeDatabase() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("push-notifications").child(mApp.getCurrentUserName().toUpperCase());
        databaseReference .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conversations.clear();
                processedKeys = new HashSet<>();
                List<Conversation> list = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String sender = childSnapshot.child("sender").getValue(String.class);
                    String body = childSnapshot.child("body").getValue(String.class);
                    Boolean isRead = childSnapshot.child("isRead").getValue(Boolean.class);
                    String dateTime = childSnapshot.child("datetime").getValue(String.class);

                    if (!sender.equals(mApp.getCurrentUserName().toUpperCase())) {
                            Conversation conversaNew = new Conversation();
                            conversaNew.setSender(sender);
                            conversaNew.setRead(isRead);
                            conversaNew.setBody(body);
                            conversaNew.setDatetime(dateTime);
                            list.add(conversaNew);

                            if (!isEqualConversationObject(conversaNew)) {
                                processedKeys.add(conversaNew);
                            }


                    }
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                for (Conversation conversation : processedKeys){
                    int count =0;
                    for(Conversation conversation1 : list){
                        if(conversation.getSender().equals(conversation1.getSender())) {
                            if (conversation1.isRead()==false) {
                                count++;
                            }
                            try {

                            Date date = sdf.parse(conversation1.getDatetime());
                            String timeAgo = durationFromNow(date);
                                conversation.setDatetime(conversation1.getDatetime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            conversation.setBody(conversation1.getBody());
                        }

                    }
                    conversation.setMessageCount(count);
                    conversations.add(conversation);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here, e.g., show an error message
                Log.e("MyApp", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // Check if sender exists in processedKeys
    private boolean isEqualConversationObject(Conversation senderToCheck) {
        for (Conversation conversation : processedKeys) {
            if (conversation.equals(senderToCheck)) {
                return true;
            }
        }
        return false;
    }
    public static String durationFromNow(Date startDate) {

        long now = System.currentTimeMillis();
        long durationMillis = now - startDate.getTime();

        if (durationMillis < 0) {
            return "just now";
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long days = TimeUnit.MILLISECONDS.toDays(durationMillis);

        if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else if (days < 30) {
            return days + " days ago";
        } else {
            long months = days / 30;
            long years = days / 365;

            if (months < 12) {
                return months + " months ago";
            } else {
                return years + " years ago";
            }
        }
    }
    private int countUnreadMessages(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
            Boolean isRead = messageSnapshot.child("isRead").getValue(Boolean.class);
            if (isRead != null && !isRead) {
                count++;
            }
        }
        return count;
    }
    private void markMessagesAsReadForSender(String selectedSender) {
        // Construct the reference to the messages for the selected user
        DatabaseReference userMessagesRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("push-notifications")
                .child(mApp.getCurrentUserName().toUpperCase());

        // Create a map to update the "isRead" status to true for messages sent by "Super Admin"
        Map<String, Object> updateData = new HashMap<>();

        // Query the messages to find those sent by "Super Admin"
        userMessagesRef.orderByChild("sender").equalTo(selectedSender).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Get the unique message ID
                    String messageId = messageSnapshot.getKey();

                    // Update the "isRead" status to true for this message
                    updateData.put(messageId + "/isRead", true);
                }

                // Use updateChildren to apply the updates to the selected messages
                userMessagesRef.updateChildren(updateData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            // Handle the error, e.g., show an error message
                            Log.e("MyApp", "Error updating messages: " + error.getMessage());
                        } else {
                            // Messages updated successfully
                            Log.d("MyApp", "Messages marked as read for sender: " + selectedSender);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error, e.g., show an error message
                Log.e("MyApp", "Error querying messages: " + error.getMessage());
            }
        });
    }
    private boolean isEqualSenderName(String sender){
        for (Conversation conversation : processedKeys) {
            if (conversation.getSender().equals(sender)) {
                return true;
            }
        }
        return false;
    }
}
