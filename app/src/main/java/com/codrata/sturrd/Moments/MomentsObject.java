package com.codrata.sturrd.Moments;


public class MomentsObject {
    private String  userId,
            name,
            profileImageUrl;

    public MomentsObject(String userId, String name, String profileImageUrl){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId(){
        return userId;
    }
    public String getName(){
        return name;
    }
    public String getProfileImageUrl(){
        return profileImageUrl;
    }

}
