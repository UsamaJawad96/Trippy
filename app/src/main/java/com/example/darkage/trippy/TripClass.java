package com.example.darkage.trippy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class TripClass implements Serializable {
    String tripId;
    String trip_name;
    //Date trip_date;
    String date;
    String trip_admin_id;
    String trip_admin_name;
    int total_buddies;
    String destinationId;
    String destinationName;
    ArrayList<String> tripMembers;
    double lang;
    double lat;

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
    //    ArrayList<MessageClass> messages;

    public String getDestinationId() {
        return destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public ArrayList<String> getTripMembers() {
        return tripMembers;
    }

    public TripClass(String tripId, String trip_name, String date, String trip_admin,String trip_admin_name, String destinationId, String destinationName, ArrayList<String> tripMembers, double a,double b) {
        this.tripId = tripId;
        this.trip_name = trip_name;
        this.date = date;
        this.trip_admin_id = trip_admin;
        this.trip_admin_name = trip_admin_name;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.tripMembers = tripMembers;
        this.lang=a;
        this.lat=b;
//        this.messages=new ArrayList<>();
//        this.messages.add(new MessageClass(("welcome to "+this.trip_name),"Default","000066669999",(new Date().toString())));
        if (tripMembers==null)
            this.total_buddies = 0;
        else
            this.total_buddies = tripMembers.size();
    }


    public TripClass(String trip_name, String date, String trip_admin, int total_buddies) {
        this.trip_name = trip_name;
        this.date = date;
        this.trip_admin_name = trip_admin;
        this.total_buddies = total_buddies;
    }

    public String getTrip_name() {
        return trip_name;
    }

    public void setTrip_name(String trip_name) {
        this.trip_name = trip_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTrip_admin_id() {
        return trip_admin_id;
    }

    public void setTrip_admin_id(String trip_admin_id) {
        this.trip_admin_id = trip_admin_id;
    }

    public String getTrip_admin_name() {
        return trip_admin_name;
    }

    public void setTrip_admin_name(String trip_admin_name) {
        this.trip_admin_name = trip_admin_name;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setTripMembers(ArrayList<String> tripMembers) {
        this.tripMembers = tripMembers;
    }

    public int getTotal_buddies() {
        if (tripMembers==null)
            return 0;
        return tripMembers.size();
    }

    public void setTotal_buddies(int total_buddies) {
        this.total_buddies = total_buddies;
    }
}
