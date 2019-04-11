package com.codrata.sturrd.Chat;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codrata.sturrd.R;
import com.codrata.sturrd.SendNotification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends AppCompatActivity {
    private static final String LOG_TAG = "chatActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private LinearLayoutManager mChatLayoutManager;

    private ArrayList<Object> chat;



    private MediaRecorder mMediaRecorder;
    public static final int RequestPermissionCode = 1;
    String audioSavePath = null;
    private File mediaStorageDir;


    private EditText mSendEditText;

    private ImageView   mSendButton,
                        mBack,
                        mImage,
            mRecordButton,
            mCancelRecordingButton;


    private TextView mName, mSendAudio;

    private long mStartTime = 0;


    private String currentUserID, matchId, chatId;

    DatabaseReference mDatabaseUser, mDatabaseChat;
    private StorageReference mStorageReference;



    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - mStartTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            mSendEditText.setHint("Recording " +String.format("%d:%02d", minutes, seconds));
            mSendEditText.setHintTextColor(getResources().getColor(R.color.hint_color_recording));
            timerHandler.postDelayed(this, 500);
        }
    };
    private String filename;
    private ArrayList<Object> resultsChat = new ArrayList<Object>();


    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if (!sendMessageText.isEmpty()) {

            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);

            SendNotification sendNotification = new SendNotification();
            sendNotification.SendNotification(sendMessageText, "new Message!", matchId);

            newMessageDb.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }


    private void getChatId() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    String message = null;
                    String createdByUser = null;


                    if (dataSnapshot.child("text").getValue() != null) {
                        message = dataSnapshot.child("text").getValue().toString();
                    }

                    if (dataSnapshot.child("createdByUser").getValue() != null) {
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }

                    if (message != null && createdByUser != null) {
                        Boolean currentUserBoolean = false;
                        if (createdByUser.equals(currentUserID)) {
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean);
                        resultsChat.add(newMessage);
                        mChatLayoutManager.scrollToPosition(resultsChat.size() - 1);
                        mChatAdapter.notifyDataSetChanged();

                        mRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                            }
                        }, 1000);

                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);





        mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Sturrd");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        matchId = getIntent().getExtras().getString("matchId");

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        //fireBase storage
        mStorageReference = FirebaseStorage.getInstance().getReference();

        getChatId();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(true);
        mChatLayoutManager = new LinearLayoutManager(this);
        mChatLayoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), this);
        // Bind adapter to recycler view object
        mRecyclerView.setAdapter(mChatAdapter);
        // mRecyclerView.setAdapter(mChatAdapter);



        mName = findViewById(R.id.name);
        mImage = findViewById(R.id.image);

        mSendEditText = findViewById(R.id.message);

        mSendButton = findViewById(R.id.send);
        mBack = findViewById(R.id.back);
        mRecordButton = findViewById(R.id.record_button);

        mCancelRecordingButton = (ImageView) findViewById(R.id.cancel_recording);
        mSendAudio = findViewById(R.id.send_audio);




        mSendEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mRecordButton.setVisibility(View.VISIBLE);
                mSendButton.setVisibility(View.GONE);
                mCancelRecordingButton.setVisibility(View.GONE);
                mSendAudio.setVisibility(View.GONE);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSendButton.setVisibility(View.VISIBLE);
                mRecordButton.setVisibility(View.GONE);
                mCancelRecordingButton.setVisibility(View.GONE);
                mSendAudio.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mSendEditText.getText().toString().isEmpty()) {
                    mRecordButton.setVisibility(View.VISIBLE);
                    mSendButton.setVisibility(View.GONE);
                    mCancelRecordingButton.setVisibility(View.GONE);
                    mSendAudio.setVisibility(View.GONE);
                } else {
                    mSendEditText.setVisibility(View.VISIBLE);
                    mCancelRecordingButton.setVisibility(View.GONE);
                    mRecordButton.setVisibility(View.GONE);
                    mSendAudio.setVisibility(View.GONE);
                }
            }
        });


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaRecorder != null) {
                    stopRecording();
                    mMediaRecorder = null;
                }
                finish();
            }
        });


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission() && mSendEditText.getText().toString().isEmpty()) {
                    mCancelRecordingButton.setVisibility(View.VISIBLE);
                    mSendAudio.setVisibility(View.VISIBLE);
                    startRecording();
                    mStartTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    mSendEditText.setEnabled(false);
                    mRecordButton.setVisibility(View.GONE);

                    Toast.makeText(ChatActivity.this, "recording", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission();
                }
            }
        });


        mCancelRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
                mSendAudio.setVisibility(View.GONE);
                timerHandler.removeCallbacks(timerRunnable);
                mSendEditText.setEnabled(true);
                mSendEditText.setHint("message..");
                mSendEditText.setHintTextColor(getResources().getColor(R.color.edit_text_hint_color));
                mCancelRecordingButton.setVisibility(View.GONE);
                mRecordButton.setVisibility(View.VISIBLE);
                Toast.makeText(ChatActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });


        mSendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();

                resultsChat.add(new AudioMessage("blaaa"));
                mChatLayoutManager.scrollToPosition(resultsChat.size() - 1);
                mChatAdapter.notifyDataSetChanged();
                timerHandler.removeCallbacks(timerRunnable);

                Toast.makeText(ChatActivity.this, "Sending", Toast.LENGTH_SHORT).show();
                uploadAudio();
                mSendEditText.setEnabled(true);
                mCancelRecordingButton.setVisibility(View.GONE);
                mRecordButton.setVisibility(View.VISIBLE);
                mSendButton.setVisibility(View.GONE);
                mSendAudio.setVisibility(View.GONE);
                mSendEditText.setHint("message..");
                mSendEditText.setHintTextColor(getResources().getColor(R.color.edit_text_hint_color));
            }
        });


        getMatchInfo();
    }

    private List<Object> getDataSetChat() {
        return resultsChat;
    }


    private void getMatchInfo(){
        DatabaseReference mMatchDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId);
        mMatchDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    String  name = "",
                            profileImageUrl = "default";
                    if(dataSnapshot.child("name").getValue()!=null)
                        name = dataSnapshot.child("name").getValue().toString();
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                    mName.setText(name);
                    if(!profileImageUrl.equals("default"))
                        Glide.with(getApplicationContext()).load(profileImageUrl).apply(RequestOptions.circleCropTransform()).into(mImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //************************************* Audio Methods ************* //
    /**
     * prepare media recorder
     */
    public void MediaRecorderReady(){
        mMediaRecorder =new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(audioSavePath);

    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(ChatActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(ChatActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ChatActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


    private void startRecording() {
        filename = createRandomAudioFileName();
        Log.d(LOG_TAG, "start recording file name " + filename);
        audioSavePath = mediaStorageDir.getAbsolutePath() + "/" + filename;
        Log.d(LOG_TAG, "start recording path " + audioSavePath);
        MediaRecorderReady();
        try {
            Toast.makeText(ChatActivity.this, audioSavePath, Toast.LENGTH_SHORT).show();
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void stopRecording() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        timerHandler.removeCallbacks(timerRunnable);


    }


    public String createRandomAudioFileName(){
        StringBuilder builder = new StringBuilder();
        builder.append("VN-").append(getCurrentTimeAndDate()).append(".3gp");

        return builder.toString();
    }


    public String getCurrentTimeAndDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }


    private void uploadAudio() {
        StorageReference filePath = mStorageReference.child("Audio").child(filename);
        Log.d(LOG_TAG, "file name to be Uploaded " + filename);
        Uri uri = Uri.fromFile(new File(audioSavePath));
        Log.d(LOG_TAG, "file path to be Uploaded " + audioSavePath);


        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ChatActivity.this, "Upload finished", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, "Failed to send audio", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteAudio() {

    }
}
