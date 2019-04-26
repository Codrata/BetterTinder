package com.codrata.sturrd.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.codrata.sturrd.EditProfileActivity;
import com.codrata.sturrd.R;
import com.codrata.sturrd.SettingsActivity;

public class UserFragment extends Fragment {

    private TextView mName, mJob, mSettings, mEditProfile;

    private ImageView mProfileImage;


    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        mName = view.findViewById(R.id.name);
        mJob = view.findViewById(R.id.job);
        mProfileImage = view.findViewById(R.id.profileImage);

        mSettings = view.findViewById(R.id.settings);
        mEditProfile = view.findViewById(R.id.editProfile);

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        getUserInfo();

        return view;
    }


    private void getUserInfo() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    String  name = "",
                            age = "",
                            profileImageUrl = "";
                    if(dataSnapshot.child("name").getValue()!=null)
                        name = dataSnapshot.child("name").getValue().toString();

                    if(dataSnapshot.child("age").getValue()!=null)
                        age = dataSnapshot.child("age").getValue().toString();

                    if(dataSnapshot.child("profileImageUrl").getValue()!=null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                    mName.setText(name + ", " + age);
                    Glide.with(getContext()).load(profileImageUrl).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}