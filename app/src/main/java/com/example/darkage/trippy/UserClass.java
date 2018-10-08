package com.example.darkage.trippy;

import java.io.Serializable;
import java.util.ArrayList;

public class UserClass implements Serializable {
    String id;
    String userName;
    ArrayList<UserClass> buddies;
    String email;
    String phone;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserClass( String userName,ArrayList<UserClass> buddies,String id,  String email) {

        this.id = id;
        this.userName = userName;
        this.buddies = buddies;
        this.email = email;
    }

    public UserClass( String userName,String id,  ArrayList<UserClass> buddies,String phone) {

        this.id = id;
        this.userName = userName;
        this.buddies = buddies;
        this.phone = phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public UserClass(String userName, ArrayList<UserClass> buddies, String idd) {
        this.userName = userName;
        this.buddies = buddies;
        id=idd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<UserClass> getBuddies() {
        return buddies;
    }

    public void setBuddies(ArrayList<UserClass> buddies) {
        this.buddies = buddies;
    }
}
