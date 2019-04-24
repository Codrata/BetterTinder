package com.codrata.sturrd.NotifyFrags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codrata.sturrd.Likes.LikesAdapter;
import com.codrata.sturrd.Likes.LikesObject;
import com.codrata.sturrd.Moments.MomentsAdapter;
import com.codrata.sturrd.Moments.MomentsObject;
import com.codrata.sturrd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MomentsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView mMoments;
    private RecyclerView.LayoutManager mMomentsLayoutManager;
    private RecyclerView.Adapter mMomentsAdapter;
    private String currentUId;
    private View view;

    public MomentsFragment() {
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

        view = inflater.inflate(R.layout.fragment_moments, container, false);

        currentUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getNewMoments();
        getUserMomentsId();
        return view;
    }

    private void getNewMoments() {
        mMoments = view.findViewById(R.id.momentsRecycler);
        mMoments.setNestedScrollingEnabled(true);
        mMoments.setHasFixedSize(false);
        mMomentsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mMoments.setLayoutManager(mMomentsLayoutManager);
        mMomentsAdapter = new MomentsAdapter(getDataSetMoments(), getContext());
        mMoments.setAdapter(mMomentsAdapter);
    }

    private void getUserMomentsId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId).child("connections").child("matches");
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
        for (int i = 0; i < resultsMoments.size(); i++) {
            if (resultsMoments.get(i).getUserId().equals(key))
                return;
        }

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.getKey(),
                            name = "",
                            profileImageUrl = "";

                    if (dataSnapshot.child("name").getValue() != null)
                        name = dataSnapshot.child("name").getValue().toString();
                    if (dataSnapshot.child("profileImageUrl").getValue() != null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();


                    for (int i = 0; i < resultsMoments.size(); i++) {
                        if (resultsMoments.get(i).getUserId().equals(userId))
                            return;
                    }

                    MomentsObject obj = new MomentsObject(userId, name, profileImageUrl);
                    resultsMoments.add(obj);
                    mMomentsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<MomentsObject> resultsMoments = new ArrayList<>();

    private List<MomentsObject> getDataSetMoments() {
        return resultsMoments;
    }

}
