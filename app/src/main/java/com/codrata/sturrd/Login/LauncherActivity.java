package com.codrata.sturrd.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;
import com.codrata.sturrd.MainActivity;

public class LauncherActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }else{
            Intent intent = new Intent(LauncherActivity.this, ChooseLoginRegistrationActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }
}
