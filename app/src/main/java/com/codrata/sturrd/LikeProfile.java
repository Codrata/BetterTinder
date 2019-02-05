package com.codrata.sturrd;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codrata.sturrd.Cards.cardObject;
import com.codrata.sturrd.Fragments.MatchesFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LikeProfile extends AppCompatActivity {

    private TextView mName,
            mJob,
            mAbout,
            mDistance,
            mAge;

    private ImageView mImage;
    private String likeId, name, age, job, about, distance, profileImageUrl, currentUid;
    private FirebaseAuth mAuth;

    DatabaseReference mDatabaseUser, mUserConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_profile);
        likeId = getIntent().getExtras().getString("likeId");
        mAuth = FirebaseAuth.getInstance();

        mName = findViewById(R.id.like_name_profile);
        mJob = findViewById(R.id.like_job_profile);
        mAbout = findViewById(R.id.like_about_profile);
        mImage = findViewById(R.id.like_image_profile);
        mAge = findViewById(R.id.like_age_profile);
        mDistance = findViewById(R.id.like_distance_profile);


        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(likeId);
        mUserConnection = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUid = mAuth.getCurrentUser().getUid();
        getUserInfo();

        FloatingActionButton fabNopeProfile = findViewById(R.id.fabNopeProfile);
        FloatingActionButton fabLikeProfile = findViewById(R.id.fabLikeProfile);

        fabNopeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserConnection.child(likeId).child("connections").child("nopes").child(currentUid).setValue(true);
            }
        });

    }

    private void getUserInfo() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    if (dataSnapshot.child("name").exists() && dataSnapshot.child("age").exists()) {
                        name = dataSnapshot.child("name").getValue().toString();
                        age = dataSnapshot.child("age").getValue().toString();
                        mName.setText(name + ", ");
                        mAge.setText(age);

                    } else {
                            mName.setText(name + ",");
                            mAge.setText("");
                    }

                    if (dataSnapshot.child("job").exists()) {
                        job = dataSnapshot.child("job").getValue().toString();
                        mJob.setText(job);
                    } else {
                        mJob.setText("No Job");
                    }

                    if (dataSnapshot.child("about").exists()) {
                        about = dataSnapshot.child("about").getValue().toString();
                        mAbout.setText(about);
                    } else {
                        mAbout.setText("No About");
                    }

                    if (dataSnapshot.child("LatLng").child("distance").exists()){
                        distance = dataSnapshot.child("LatLng").child("distance").getValue().toString();
                        mDistance.setText(distance + " km");
                    }else {
                        mDistance.setText("");
                    }
                    profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();


                    if (!profileImageUrl.equals("default"))
                        Glide.with(getApplicationContext()).load(profileImageUrl).into(mImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void onClick(View view) {
        mUserConnection.child(likeId).child("connections").child("likes").child(currentUid).setValue(true);
        DatabaseReference currentUserConnectionsDb = mUserConnection.child(currentUid).child("connections").child("likes").child(likeId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "new Connection", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    mUserConnection.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUid).child("ChatId").setValue(key);
                    mUserConnection.child(currentUid).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

                    SendNotification sendNotification = new SendNotification();
                    sendNotification.SendNotification("check it out!", "new Connection!", dataSnapshot.getKey());
                    LikeProfile.super.onBackPressed();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
