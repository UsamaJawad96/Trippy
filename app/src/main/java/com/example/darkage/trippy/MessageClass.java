package com.example.darkage.trippy;

import java.util.Date;

public class MessageClass {
    String mText;
    String image64;
    String sender;
    String senderId;
    String messageDate;
    Boolean isImage;
    Long timestamp;

    public MessageClass(String mText, String sender, String senderId, String messageDate) {
        this.mText = mText;
        this.sender = sender;
        this.senderId = senderId;
        this.messageDate = messageDate;
        isImage=false;
        timestamp= new Date().getTime();
    }

    public MessageClass(String image64, String sender, String senderId, String messageDate, Boolean isImage) {
        this.image64 = image64;
        this.sender = sender;
        this.senderId = senderId;
        this.messageDate = messageDate;
        this.isImage = isImage;
        timestamp= new Date().getTime();
    }


    public String getSenderId() {
        return senderId;
    }


    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public String getImage64() {
        return image64;
    }

    public void setImage64(String image64) {
        this.image64 = image64;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }


    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public Boolean getImage() {
        return isImage;
    }

    public void setImage(Boolean image) {
        isImage = image;
    }
}
