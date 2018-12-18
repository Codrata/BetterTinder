package com.simcoder.tinder;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.simcoder.tinder.Login.ChooseLoginRegistrationActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class SettingsActivity extends AppCompatActivity {

    private RadioRealButtonGroup mRadioGroup;
    private Button mLogOut;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String  interest = "Male",
                    userId;


    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRadioGroup = findViewById(R.id.radioRealButtonGroup);
        mLogOut = findViewById(R.id.logOut);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

    }


    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("interest").getValue()!=null)
                        interest = dataSnapshot.child("interest").getValue().toString();

                    if(interest.equals("Male"))
                        mRadioGroup.setPosition(0);
                    else if(interest.equals("Female"))
                        mRadioGroup.setPosition(1);
                    else
                        mRadioGroup.setPosition(2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void saveUserInformation() {
        switch(mRadioGroup.getPosition()){
            case 0:
                interest = "Male";
                break;
            case 1:
                interest = "Female";
                break;
            case 2:
                interest = "Both";
                break;
        }

        Map userInfo = new HashMap();
        userInfo.put("interest", interest);
        mUserDatabase.updateChildren(userInfo);

    }

    private void logOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SettingsActivity.this, ChooseLoginRegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveUserInformation();
        finish();
        return false;
    }

}
