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

import com.codrata.sturrd.Likes.LikesAdapter;
import com.codrata.sturrd.Likes.LikesObject;
import com.codrata.sturrd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class LikesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView mLikes;
    private RecyclerView.LayoutManager mLikesLayoutManager;
    private RecyclerView.Adapter mLikesAdapter;
    private String currentUId;
    private View view;

    public LikesFragment() {
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

        view = inflater.inflate(R.layout.fragment_likes, container, false);

        currentUId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getNewLikes();
        getUserLikeId();

        return view;
    }

    private void getNewLikes(){
        mLikes = view.findViewById(R.id.likesRecycler);
        mLikes.setNestedScrollingEnabled(true);
        mLikes.setHasFixedSize(false);
        mLikesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mLikes.setLayoutManager(mLikesLayoutManager);
        mLikesAdapter = new LikesAdapter(getDataSetLikes(), getContext());
        mLikes.setAdapter(mLikesAdapter);
    }

    private void getUserLikeId() {
        DatabaseReference likeDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId).child("connections").child("likes");
        likeDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot like : dataSnapshot.getChildren()){
                        FetchLikeInformation(like.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchLikeInformation(String key) {
        for(int i = 0; i < resultsLikes.size(); i++){
            if(resultsLikes.get(i).getUserId().equals(key))
                return;
        }

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String  userId = dataSnapshot.getKey(),
                            name = "",
                            profileImageUrl = "";

                    if(dataSnapshot.child("name").getValue()!=null)
                        name = dataSnapshot.child("name").getValue().toString();
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();



                    for(int i = 0; i < resultsLikes.size(); i++){
                        if(resultsLikes.get(i).getUserId().equals(userId))
                            return;
                    }

                    LikesObject obj = new LikesObject(userId, name, profileImageUrl);
                    resultsLikes.add(obj);
                    mLikesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<LikesObject> resultsLikes = new ArrayList<>();
    private List<LikesObject> getDataSetLikes() {
        return resultsLikes;
    }

}
