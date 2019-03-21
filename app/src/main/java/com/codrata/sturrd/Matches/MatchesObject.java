package com.codrata.sturrd.Matches;


public class MatchesObject {
    private String  userId,
            name,
            lastMessage,
            chatId,
            profileImageUrl,
            distance;

    public MatchesObject(String userId, String name, String profileImageUrl, String chatId, String lastMessage, String distance){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.lastMessage = lastMessage;
        this.chatId = chatId;
        this.distance = distance;
    }

    public String getUserId(){
        return userId;
    }
    public String getName(){
        return name;
    }
    public String getLastMessage(){
        return lastMessage;
    }
    public String getChatId(){
        return chatId;
    }
    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public String getDistance(){return  distance;}


    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }
    public void setChatId(String chatId){
        this.chatId = chatId;
    }
}
