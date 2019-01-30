package com.codrata.sturrd;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.codrata.sturrd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.codrata.sturrd.Login.ChooseLoginRegistrationActivity;

import java.util.HashMap;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class SettingsActivity extends AppCompatActivity {

    private RadioRealButtonGroup mRadioGroup, mRadioGroup1;
    private Button mLogOut;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String  interest = "Male";
    private String  wanna = "Date";


    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRadioGroup = findViewById(R.id.radioRealButtonGroup);
        mRadioGroup1 = findViewById(R.id.radioRealButtonGroup1);
        mLogOut = findViewById(R.id.logOut);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

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

                    if(dataSnapshot.child("wanna").getValue()!=null)
                        wanna = dataSnapshot.child("wanna").getValue().toString();

                    if(wanna.equals("Date"))
                        mRadioGroup1.setPosition(0);
                    else if(wanna.equals("Hookup"))
                        mRadioGroup1.setPosition(1);
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

        switch(mRadioGroup1.getPosition()){
            case 0:
                wanna = "Date";
                break;
            case 1:
                wanna = "Hookup";
                break;
        }

        Map userInfo = new HashMap();
        userInfo.put("interest", interest);
        userInfo.put("wanna", wanna);
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
