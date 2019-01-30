package com.codrata.sturrd.Cards;

import java.io.Serializable;


public class cardObject implements Serializable {
    private String  userId,
                    name,
                    profileImageUrl,
                    age,
                    about,
                    time,
                    job;
    public cardObject(String userId, String name, String age, String about, String time, String job, String profileImageUrl){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.age = age;
        this.about = about;
        this.time = time;
        this.job = job;
    }

    public String getUserId(){
        return userId;
    }
    public String getName(){
        return name;
    }
    public String getAge(){
        return age;
    }
    public String getAbout(){
        return about;
    }
    public String getJob(){
        return job;
    }
    public String getProfileImageUrl(){
        return profileImageUrl;
    }
}
