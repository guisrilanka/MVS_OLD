package com.gui.mdt.thongsieknavclient.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.ConversationAdapter;
import com.gui.mdt.thongsieknavclient.adapters.ConversationMessageAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Conversation;
import com.gui.mdt.thongsieknavclient.datamodel.Message;
import com.joanzapata.iconify.widget.IconButton;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MvsConversationsMessageListActivity extends AppCompatActivity {
    NavClientApp mApp;
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
    private List<Message> messages;
//    private ConversationAdapter adapter;
    private RecyclerView recyclerView;
    private ConversationMessageAdapter adapter;
    private String senderName;
    ImageButton btnSend;
    EditText txtSendMessage;
    TextView tvSenderName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_inbox);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Customer List");
        mApp = (NavClientApp) getApplicationContext();

        xProgressDialog = new ProgressDialog(MvsConversationsMessageListActivity .this);
        xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

         txtSendMessage = (EditText) findViewById(R.id.txtSendMessage);
         btnSend = (ImageButton) findViewById(R.id.btnMessageSend);
        tvSenderName=(TextView) findViewById(R.id.tvSenderName);


        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewConversationInbox);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // This is the key
        recyclerView.setLayoutManager(layoutManager);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sending the message here
                String msg = txtSendMessage.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(mApp.getCurrentUserName().toUpperCase(),senderName,msg);
                    int lastPosition = messages.size() - 1;
                    if (lastPosition >= 0) {
                        recyclerView.scrollToPosition(lastPosition);
                    }
                }
                txtSendMessage.setText("");
            }
        });
        // Populate this list with your messages
//        List<Message> messages = createDummyMessages();


        messages = new ArrayList<>();

        // Retrieve data from the Realtime Database
        retrieveDataFromRealtimeDatabase();

        // Initialize the class-level adapter
//        adapter = new ConversationAdapter(conversations);

        retrieveDataFromRealtimeDatabase();
        adapter = new ConversationMessageAdapter(messages, this);
        recyclerView.setAdapter(adapter);

        senderName = getIntent().getStringExtra("senderName");
        tvSenderName.setText(senderName);
        if (senderName != null) {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("push-notifications").child(mApp.getCurrentUserName().toUpperCase());
            retrieveDataFromRealtimeDatabase();
            adapter = new ConversationMessageAdapter(messages, this);
            recyclerView.setAdapter(adapter);
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void retrieveDataFromRealtimeDatabase() {
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    messages.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> childData = (HashMap<String, Object>) childSnapshot.getValue();

                            if (childData != null) {
                                String sender = childData.get("sender").toString();
                                String datetime = childData.get("datetime").toString();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = null;

                                if (sender.equals(mApp.getCurrentUserName().toUpperCase()) && childData.get("reciever").toString().equals(senderName)) {
                                    try {
                                        date = sdf.parse(datetime);

                                        String timeAgo = durationFromNow(date);
                                        String body = childData.get("body").toString();
                                        boolean isRead = (boolean) childData.get("isRead");
                                        String senderName = childData.get("sender").toString();

                                        Message message = new Message();
                                        message.setMessage(body);
                                        message.setName(senderName);
                                        message.setDate(datetime);
                                        messages.add(message);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else if (sender.equals(senderName)) {
                                    try {
                                        date = sdf.parse(datetime);
                                        String timeAgo = durationFromNow(date);
                                        String body = childData.get("body").toString();
                                        boolean isRead = (boolean) childData.get("isRead");
                                        String senderName = childData.get("sender").toString();

                                        Message message = new Message();
                                        message.setMessage(body);
                                        message.setName(senderName);
                                        message.setDate(datetime);
                                        messages.add(message);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors, if any
                    System.out.println("Error: " + databaseError.getMessage());
                }
            });
        }
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
    public void sendMessage(String sender,String receiver,String message){

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("push-notifications").child(receiver);
        DatabaseReference senderReference = FirebaseDatabase.getInstance().getReference().child("push-notifications").child(sender);
        String messageId = userReference.push().getKey();
        String messageIdSender = senderReference.push().getKey();

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(date);

        Map<String, Object> receiverMessageData = new HashMap<>();
        receiverMessageData.put("body", message);
        receiverMessageData.put("datetime", currentDate);
        receiverMessageData.put("isRead", true);
        receiverMessageData.put("reciever",sender) ;
        receiverMessageData.put("sender",sender) ;

        Map<String, Object> senderMessageData = new HashMap<>();
        senderMessageData.put("body", message);
        senderMessageData.put("datetime", currentDate);
        senderMessageData.put("isRead", true);
        senderMessageData.put("reciever", receiver);
        senderMessageData.put("sender", sender);

        // Push the new message data into the user's node in the database
        userReference.child(messageId).setValue(receiverMessageData);

        senderReference.child(messageIdSender).setValue(senderMessageData);

    }

}
