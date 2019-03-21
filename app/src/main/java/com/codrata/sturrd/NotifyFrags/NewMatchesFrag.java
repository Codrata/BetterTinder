package com.codrata.sturrd.NotifyFrags;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class NewMatchesFrag extends Fragment {
    private View view;
    private RecyclerView mMatch;
    private String currentUserID;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private RecyclerView.Adapter mMatchesAdapter;



    public NewMatchesFrag() {
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

        view = inflater.inflate(R.layout.fragment_new_matches, container, false);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        getNewMatches();

        getUserMatchId();

        return view;
    }

    private void getNewMatches(){
        mMatch = view.findViewById(R.id.new_match);
        mMatch.setNestedScrollingEnabled(false);
        mMatch.setHasFixedSize(false);
        mMatchesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        ((LinearLayoutManager) mMatchesLayoutManager).setReverseLayout(true);
        mMatch.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), getContext());
        mMatch.setAdapter(mMatchesAdapter);
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

    private void FetchMatchInformation(String key) {
        for(int i = 0; i < resultsMatches.size(); i++){
            if(resultsMatches.get(i).getUserId().equals(key))
                return;
        }

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


                    if(dataSnapshot.child("name").getValue()!=null)
                        name = dataSnapshot.child("name").getValue().toString();
                    if (dataSnapshot.child("LatLng").child(currentUserID).child("distance").getValue() !=null)
                        distance = dataSnapshot.child("LatLng").child(currentUserID).child("distance").getValue().toString();
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private ArrayList<MatchesObject> resultsMatches = new ArrayList<>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }


}
