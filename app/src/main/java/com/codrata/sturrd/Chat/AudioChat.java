package com.codrata.sturrd.Chat;

public class AudioChat {
    private Boolean currentUser;
    private String mAudioUri;


    public AudioChat(Boolean currentUser, String audioUri) {
        this.currentUser = currentUser;
        this.mAudioUri = audioUri;
    }


    public String getAudioUri() {
        return mAudioUri;
    }

    public Boolean getCurrentUser() {
        return currentUser;
    }


}
