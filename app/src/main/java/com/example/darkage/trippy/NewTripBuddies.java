package com.example.darkage.trippy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;

public class NewTripBuddies extends AppCompatActivity {
    TextView dateView;
    TextView nameView;
    String date;
    String tName;
    String destId;
    String destName;
    double lat,lng;

    ArrayList<String> addedBuddies;
    ArrayList<String> userBuddies;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip_buddies);
        getSupportActionBar().setSubtitle("New Trip");

        Intent i=getIntent();
         date=i.getStringExtra("date");
         tName=i.getStringExtra("tn");
         destId=i.getStringExtra("tid");
         destName=i.getStringExtra("dn");
         lat=i.getDoubleExtra("lat",0);
         lng=i.getDoubleExtra("long",0);

        dateView=findViewById(R.id.new_trip_setDate);
        dateView.setText(date);
        nameView=findViewById(R.id.new_trip_setname);
        nameView.setText(tName);

        addedBuddies=new ArrayList<>();
        userBuddies=new ArrayList<>();
        userBuddies.addAll(userBuddyList.getInstance().buddyUsernames);


        Spinner s = (Spinner) findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, userBuddies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        if (userBuddies.size()==0)
        {
            ImageButton bt=findViewById(R.id.imageButton);
            bt.setEnabled(false);
            Drawable rep=getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark);
            //bt.setBackground(rep);
            //bt.setBackgroundColor(Color.RED);
            bt.setImageDrawable(rep);
        }

        ListView ls=findViewById(R.id.listviewbuddies);
        adapter2= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,addedBuddies);
        ls.setAdapter(adapter2);
        //set spinner
    }

    public void addBuddy(View view){
        Spinner s = (Spinner) findViewById(R.id.spinner);
        String ss=s.getSelectedItem().toString();
        addedBuddies.add(ss);
        userBuddies.remove(ss);
        adapter.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();

        if (userBuddies.size()==0)
        {
            ImageButton bt=findViewById(R.id.imageButton);
            bt.setEnabled(false);
            Drawable rep=getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark);
            //bt.setBackground(rep);
            //bt.setBackgroundColor(Color.RED);
            bt.setImageDrawable(rep);
        }
        //get buddy from spinner
        //add buddy to addedbuddies arraylist
        //remove that buddy from spinner arraylist
        //display on listview
    }

    public void createTripBtn(View view)
    {
       // if (addedBuddies.size()==0)
        //    addedBuddies=null;
        addedBuddies.add(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        Intent i=new Intent();
        i.putExtra("date",date);
        i.putExtra("tn",tName);
        i.putExtra("tid",destId);
        i.putExtra("dn",destName);
        i.putExtra("lat",lat);
        i.putExtra("long",lng);
        i.putExtra("buddies",(Serializable)addedBuddies);
        setResult(Activity.RESULT_OK,i);
        finish();
    }
}
