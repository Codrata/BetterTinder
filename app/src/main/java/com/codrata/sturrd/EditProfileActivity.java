package com.codrata.sturrd;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class EditProfileActivity extends AppCompatActivity {

    private EditText    mName,
                        mPhone,
                        mAge,
                        mJob,
                        mAbout;

    private RadioRealButtonGroup mRadioGroup;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String  userId,
                    name,
                    phone,
                    profileImageUrl,
                    userSex,
                    job,
                    age,
                    about;


    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mName = findViewById(R.id.name);
        mPhone = findViewById(R.id.phone);
        mAge = findViewById(R.id.age);
        mJob = findViewById(R.id.job);
        mAbout = findViewById(R.id.about);

        mRadioGroup = findViewById(R.id.radioRealButtonGroup);

        mProfileImage = findViewById(R.id.profileImage);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }


    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("name").getValue()!=null)
                        name = dataSnapshot.child("name").getValue().toString();
                    if(dataSnapshot.child("phone").getValue()!=null)
                        phone = dataSnapshot.child("phone").getValue().toString();
                    if(dataSnapshot.child("sex").getValue()!=null)
                        userSex = dataSnapshot.child("sex").getValue().toString();
                    if(dataSnapshot.child("age").getValue()!=null)
                        age = dataSnapshot.child("age").getValue().toString();
                    if(dataSnapshot.child("job").getValue()!=null)
                        job = dataSnapshot.child("job").getValue().toString();
                    if(dataSnapshot.child("about").getValue()!=null)
                        about = dataSnapshot.child("about").getValue().toString();
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                    mName.setText(name);
                    mPhone.setText(phone);
                    mAge.setText(age);
                    mJob.setText(job);
                    mAbout.setText(about);
                    if(!profileImageUrl.equals("default"))
                        Glide.with(getApplicationContext()).load(profileImageUrl).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                    if(userSex.equals("Male"))
                        mRadioGroup.setPosition(0);
                    else
                        mRadioGroup.setPosition(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void saveUserInformation() {
        name = mName.getText().toString();
        phone = mPhone.getText().toString();
        age = mAge.getText().toString();
        job = mJob.getText().toString();
        about = mAbout.getText().toString();
        if(mRadioGroup.getPosition()==0)
            userSex = "Male";
        else
            userSex = "Female";

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        userInfo.put("age", age);
        userInfo.put("job", job);
        userInfo.put("sex", userSex);
        userInfo.put("about", about);
        mUserDatabase.updateChildren(userInfo);

        if(resultUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mUserDatabase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                Glide.with(getApplication())
                        .load(bitmap) // Uri of the picture
                        .apply(RequestOptions.circleCropTransform())
                        .into(mProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        saveUserInformation();
        return false;
    }

}
