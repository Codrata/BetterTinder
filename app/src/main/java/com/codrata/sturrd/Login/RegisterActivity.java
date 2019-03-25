package com.codrata.sturrd.Login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.codrata.sturrd.MainActivity;
import com.codrata.sturrd.R;

import java.util.HashMap;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButtonGroup;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEmail, mPassword, mName;
    private Button mRegistration;
    private String currentUId;
    private DatabaseReference usersDb;
    private FusedLocationProviderClient client;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    private RadioRealButtonGroup mRadioGroup, mRadioGroup1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        client = LocationServices.getFusedLocationProviderClient(this);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mName = findViewById(R.id.name);

        mRegistration = findViewById(R.id.register);

        mRadioGroup = findViewById(R.id.radioRealButtonGroup);
        mRadioGroup.setPosition(0);
        mRadioGroup1 = findViewById(R.id.radioRealButtonGroup1);
        mRadioGroup1.setPosition(0);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final String accountFocus;
                int selectFocus = mRadioGroup1.getPosition();
                final String accountType;
                int selectId = mRadioGroup.getPosition();



                switch (selectId){
                    case 1:
                        accountType = "Female";
                        break;
                    default:
                        accountType = "Male";
                }

                switch (selectFocus){
                    case 1:
                        accountFocus = "Just Hookup";
                        break;
                    default:
                        accountFocus = "Wanna Date";
                }


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                        }else{
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            Map userInfo = new HashMap<>();
                            userInfo.put("name", name);
                            userInfo.put("sex", accountType);
                            userInfo.put("wanna", accountFocus);
                            userInfo.put("profileImageUrl", "default");
                            switch(accountType){
                                case "Male":
                                    userInfo.put("interest", "Female");
                                    break;
                                case "Female":
                                    userInfo.put("interest", "Male");
                                    break;
                            }
                            switch(accountFocus){
                                case "Wanna Date":
                                    userInfo.put("wanna", "Date");
                                    break;
                                case "Just Hookup":
                                    userInfo.put("wanna", "Hookup");
                                    break;
                            }
                            currentUserDb.updateChildren(userInfo);
                            locationUpdate();
                            requestPermissions();
                        }
                    }
                });
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
    public void locationUpdate() {
        if (ActivityCompat.checkSelfPermission(RegisterActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    final double longitude = location.getLongitude();
                    final double latitude = location.getLatitude();

                    String latitudeString = String.valueOf(latitude);
                    String longitudeString = String.valueOf(longitude);

                    //usersDb.child("latitude").child(latitudeString).setValue(true);
                    //usersDb.child("longitude").child(longitudeString).setValue(true);

                    Map userLatLng = new HashMap();
                    userLatLng.put("latitude", latitudeString);
                    userLatLng.put("longitude", longitudeString);

                    currentUId = mAuth.getCurrentUser().getUid();

                    usersDb.child(currentUId).child("LatLng").updateChildren(userLatLng);
                }

            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}
