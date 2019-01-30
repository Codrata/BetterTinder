package com.codrata.sturrd;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codrata.sturrd.Cards.cardObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LikeProfile extends AppCompatActivity {

    private TextView mName,
            mJob,
            mAbout;

    private ImageView mImage;
    private String likeId, name, age, job, about, profileImageUrl, currentUid;

    DatabaseReference mDatabaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_profile);

        likeId = getIntent().getExtras().getString("likeId");


        mName = findViewById(R.id.like_name_profile);
        mJob = findViewById(R.id.like_job_profile);
        mAbout = findViewById(R.id.like_about_profile);
        mImage = findViewById(R.id.like_image_profile);

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(likeId);
        getUserInfo();

    }


        private void getUserInfo() {
            mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                            if (dataSnapshot.child("name").exists()) {
                                name = dataSnapshot.child("name").getValue().toString();
                                if (dataSnapshot.child("age").exists()) {
                                    age = dataSnapshot.child("age").getValue().toString();
                                    mName.setText(name + "," + " " + age);
                                }else {
                                    mName.setText(name + ",");
                                }
                            }else {
                                mName.setText(("No Name"));
                            }



                            if (dataSnapshot.child("job").exists()) {
                                job = dataSnapshot.child("job").getValue().toString();
                                mJob.setText(job);
                            }else {
                                mJob.setText("No Job");
                            }

                            if (dataSnapshot.child("about").exists()) {
                                about = dataSnapshot.child("about").getValue().toString();
                                mAbout.setText("About:" + "\n" + "\n"  + about);
                            }else {
                                mAbout.setText("No About");
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

    }

