package com.codrata.sturrd.Chat;

public class AudioChat {
    private Boolean currentUser;
    private String audioUri;


    public AudioChat(Boolean currentUser, String audioUri) {
        this.currentUser = currentUser;
        this.audioUri = audioUri;
    }


    public String getAudioUri() { return audioUri; }

    public void setmAudioUri(String userID){ this.audioUri = audioUri;}

    public Boolean getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(Boolean currentUser){
        this.currentUser = currentUser;
    }



}
