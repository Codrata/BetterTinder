package com.codrata.sturrd.NotifyFrags;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codrata.sturrd.ChatList.ChatListAdapter;
import com.codrata.sturrd.Matches.MatchesAdapter;
import com.codrata.sturrd.Matches.MatchesObject;
import com.codrata.sturrd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MessagesFragment extends Fragment {

    private RecyclerView mMatch, mChat;

    private RecyclerView.Adapter mChatAdapter, mMatchesAdapter;
    private String currentUId;
    private FirebaseAuth mAuth;

    private RecyclerView.LayoutManager mChatLayoutManager, mMatchesLayoutManager;
    private String currentUserID;

    View view;



    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();


        view = inflater.inflate(R.layout.fragment_messages, container, false);

        getChats();
        getUserMatchId();

        return view;
    }

    private void getUserMatchId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");
        matchDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getChats(){
        mChat = view.findViewById(R.id.messages);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        ((LinearLayoutManager) mChatLayoutManager).setReverseLayout(true);
        mChat.setLayoutManager(mChatLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        mChat.addItemDecoration(dividerItemDecoration);
        mChatAdapter = new ChatListAdapter(getDataSetChat(), getContext());
        mChat.setAdapter(mChatAdapter);
    }

    private void FetchMatchInformation(String key) {
        for(int i = 0; i < resultsMatches.size(); i++){
            if(resultsMatches.get(i).getUserId().equals(key))
                return;
        }

        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), getContext());


        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String  userId = dataSnapshot.getKey(),
                            name = "",
                            profileImageUrl = "",
                            distance = "",
                            chatId = "";

                    currentUId = mAuth.getCurrentUser().getUid();

                    if(dataSnapshot.child("name").getValue()!=null)
                        name = dataSnapshot.child("name").getValue().toString();
                    if (dataSnapshot.child("LatLng").child(currentUId).child("distance").getValue() !=null)
                        distance = dataSnapshot.child("LatLng").child(currentUId).child("distance").getValue().toString();
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    if(dataSnapshot.child("connections").child("matches").child(FirebaseAuth.getInstance().getUid()).child("ChatId").getValue()!=null)
                        chatId = dataSnapshot.child("connections").child("matches").child(FirebaseAuth.getInstance().getUid()).child("ChatId").getValue().toString();



                    for(int i = 0; i < resultsMatches.size(); i++){
                        if(resultsMatches.get(i).getUserId().equals(userId))
                            return;
                    }

                    MatchesObject obj = new MatchesObject(userId, name, profileImageUrl, chatId, "", distance);
                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                    if(!chatId.equals(""))
                        FetchLastMessage(chatId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void FetchLastMessage(String key) {

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(key);
        Query query = userDb.orderByKey().limitToLast(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String chatId = dataSnapshot.getRef().getKey();
                    DataSnapshot messageNode = dataSnapshot.getChildren().iterator().next();
                    String messageId = messageNode.getKey();
                    String message = "";

                    if(dataSnapshot.child(messageId).child("text").getValue()!=null)
                        message = dataSnapshot.child(messageId).child("text").getValue().toString();

                    if(message.equals(""))
                        return;


                    for(int i = 0; i < resultsMatches.size(); i++){
                        if(resultsMatches.get(i).getChatId().equals(chatId)) {
                            resultsMatches.get(i).setLastMessage(message);
                            for(int j = 0; j < resultsChat.size(); j++){
                                if(resultsChat.get(j).getChatId().equals(chatId)){
                                    resultsMatches.get(i).setLastMessage(message);
                                    mChatAdapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                            resultsChat.add(resultsMatches.get(i));
                            mChatAdapter.notifyDataSetChanged();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private ArrayList<MatchesObject> resultsChat = new ArrayList<>();
    private List<MatchesObject> getDataSetChat() {
        return resultsChat;
    }

    private ArrayList<MatchesObject> resultsMatches = new ArrayList<>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }

}
