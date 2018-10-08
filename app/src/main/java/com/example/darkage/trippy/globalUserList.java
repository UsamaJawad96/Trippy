package com.example.darkage.trippy;

import java.util.ArrayList;

public class globalUserList {

        private static globalUserList myObj;
        ArrayList<String> Usernames;
        ArrayList<String> Userids;

        private globalUserList(){
            Userids=new ArrayList<>();
            Usernames=new ArrayList<>();
        }

        public static globalUserList getInstance(){
            if(myObj == null){
                myObj = new globalUserList();
            }
            return myObj;
        }

        public void addBuddy(String s1,String s2){
            Userids.add(s1);
            Usernames.add(s2);
        }

        public void clear(){
            Usernames.clear();
            Userids.clear();
        }


}
