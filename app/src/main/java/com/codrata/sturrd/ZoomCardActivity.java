package com.codrata.sturrd;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codrata.sturrd.R;
import com.codrata.sturrd.Cards.cardObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ZoomCardActivity extends AppCompatActivity {

    private TextView    mName,
                        mJob,
                        mDistance,
                        mAbout;

    private ImageView mImage;
    private String cardId, distance;
    DatabaseReference mDatabaseUser, mUserConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_card);

        Intent i = getIntent();
        cardObject mCardObject = (cardObject)i.getSerializableExtra("cardObject");

        cardId = mCardObject.getUserId();

        mName = findViewById(R.id.name);
        mJob = findViewById(R.id.job);
        mAbout = findViewById(R.id.about);
        mImage = findViewById(R.id.image);

        mDistance = findViewById(R.id.distance);


        mName.setText(mCardObject.getName() + ", " + mCardObject.getAge());
        mDistance.setText(mCardObject.getDistance() + " km");
        mJob.setText(mCardObject.getJob());
        mAbout.setText(mCardObject.getAbout());
        //getUserDistance();
        // TODO complete the Fab methods for Like and Nope
        if(!mCardObject.getProfileImageUrl().equals("default"))
            Glide.with(getApplicationContext()).load(mCardObject.getProfileImageUrl()).into(mImage);
    }

    private void getUserDistance(){
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(cardId);

        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    if (dataSnapshot.child("LatLng").child("distance").exists()){

                        distance = dataSnapshot.child("LatLng").child("distance").getValue().toString();

                        mDistance.setText(distance + " km");
                    }else {
                        mDistance = findViewById(R.id.distance);

                        mDistance.setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
