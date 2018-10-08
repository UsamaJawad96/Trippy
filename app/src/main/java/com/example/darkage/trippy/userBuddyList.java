package com.example.darkage.trippy;

import java.util.ArrayList;

public class userBuddyList {

    private static userBuddyList myObj;
    ArrayList<String> buddyUsernames;
    ArrayList<String> buddyUserids;

    private userBuddyList(){
        buddyUserids=new ArrayList<>();
        buddyUsernames=new ArrayList<>();
    }

    public static userBuddyList getInstance(){
        if(myObj == null){
            myObj = new userBuddyList();
        }
        return myObj;
    }

    public void addBuddy(String s1,String s2){
        buddyUserids.add(s1);
        buddyUsernames.add(s2);
    }

    public void addUN(String s1) {
        buddyUsernames.add(s1);
    }

    public void clear(){
        buddyUsernames.clear();
        buddyUserids.clear();
    }

}
