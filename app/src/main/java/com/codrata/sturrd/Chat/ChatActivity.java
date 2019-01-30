package com.codrata.sturrd.Chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.codrata.sturrd.R;
import com.codrata.sturrd.SendNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private LinearLayoutManager mChatLayoutManager;

    private EditText mSendEditText;

    private ImageView   mSendButton,
                        mBack,
                        mImage;

    private TextView mName;

    private String currentUserID, matchId, chatId;

    DatabaseReference mDatabaseUser, mDatabaseChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        matchId = getIntent().getExtras().getString("matchId");

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatId();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(true);
        mChatLayoutManager = new LinearLayoutManager(this);
        mChatLayoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), this);
        mRecyclerView.setAdapter(mChatAdapter);


        mName = findViewById(R.id.name);
        mImage = findViewById(R.id.image);

        mSendEditText = findViewById(R.id.message);
        mSendButton = findViewById(R.id.send);
        mBack = findViewById(R.id.back);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        getMatchInfo();
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();

        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);

            SendNotification sendNotification = new SendNotification();
            sendNotification.SendNotification(sendMessageText, "new Message!", matchId);

            newMessageDb.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }

    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;

                    if(dataSnapshot.child("text").getValue()!=null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }

                    if(message!=null && createdByUser!=null){
                        Boolean currentUserBoolean = false;
                        if(createdByUser.equals(currentUserID)){
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean);
                        resultsChat.add(newMessage);
                        mChatLayoutManager.scrollToPosition(resultsChat.size() - 1);
                        mChatAdapter.notifyDataSetChanged();

                        mRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                            }
                        }, 1000);

                    }
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private ArrayList<ChatObject> resultsChat = new ArrayList<ChatObject>();
    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }


    private void getMatchInfo(){
        DatabaseReference mMatchDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId);
        mMatchDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    String  name = "",
                            profileImageUrl = "default";
                    if(dataSnapshot.child("name").getValue()!=null)
                        name = dataSnapshot.child("name").getValue().toString();
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                    mName.setText(name);
                    if(!profileImageUrl.equals("default"))
                        Glide.with(getApplicationContext()).load(profileImageUrl).apply(RequestOptions.circleCropTransform()).into(mImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
