package com.example.darkage.trippy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class TripActivity extends AppCompatActivity {
    private FirebaseUser currUser;
    String trip_nam;
    String date;
    UserClass trip_admin;
    String destinationId;
    String destinationName;
    ArrayList<UserClass> buds;
    TripClass trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        currUser=FirebaseAuth.getInstance().getCurrentUser();

        Intent data=getIntent();
        trip=(TripClass)data.getSerializableExtra("tripdetails");
//        String trip_name=data.getStringExtra("tn");
//        String date=data.getStringExtra("date");
//        String destinationId=data.getStringExtra("tid");
//        String destinationName=data.getStringExtra("dn");
//        ArrayList<UserClass> buds=new ArrayList<>();
//        buds.addAll((ArrayList<UserClass>)data.getSerializableExtra("buddies"));

        TextView a=findViewById(R.id.trip_activity_name);
        TextView b=findViewById(R.id.trip_activity_admin);
        TextView c=findViewById(R.id.trip_activity_date);
        TextView d=findViewById(R.id.trip_activity_buddies);

        a.setText(trip.getTrip_name());
        b.setText(trip.getTrip_admin_name());
        c.setText(trip.getDate());
        d.setText(Integer.toString(trip.getTotal_buddies()));
    }


    public void viewLocation(View view){
                destinationName=trip.getDestinationName();
                destinationId=trip.getDestinationId();
                String newURL="https://www.google.com/maps/search/?api=1&query="+destinationName+ "&query_place_id=" + destinationId;
                Uri gmmIntentUri = Uri.parse(newURL);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
    }

    public void backbtn(View view){
        finish();
    }
}
