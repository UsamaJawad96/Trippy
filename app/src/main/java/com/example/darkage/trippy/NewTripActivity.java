package com.example.darkage.trippy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewTripActivity extends AppCompatActivity {
    //private FirebaseAnalytics mFirebaseAnalytics;
    int d;
    int m;
    int y;
    TextView tv;
    TextView dp;
    Calendar mCurr;
    boolean locationSet;
    String date;
    String dName;
    String dId;
    String tripName;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    LatLng langlat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("New Trip");
       // mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        locationSet=false;
        dp=findViewById(R.id.date_picker);
        tv=findViewById(R.id.date_picker_view);
        mCurr=Calendar.getInstance();

        TextView ds=findViewById(R.id.destination_view);
        ds.setText(Html.fromHtml("<u>Destination</u>"));

        d=mCurr.get(Calendar.DAY_OF_MONTH);
        m=mCurr.get(Calendar.MONTH);
        y=mCurr.get(Calendar.YEAR);
        m=m+1;
        tv.setText(d+"/"+m+"/"+y);

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd=new DatePickerDialog(NewTripActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            month+=1;

                            String selectedDate=(dayOfMonth+"/"+month+"/"+year);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                Date strDate = sdf.parse(selectedDate);
                                if (new Date().after(strDate)) {
                                    //Toast.makeText(getApplicationContext(),"pldsa",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    y=year;
                                    m=month;
                                    d=dayOfMonth;
                                    tv.setText(d + "/" + m + "/" + y);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }



                        // tv.setText(dayOfMonth+"/"+month+"/"+year);
                    }
                },y,m,d);
                dpd.show();
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd=new DatePickerDialog(NewTripActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month+=1;

                        String selectedDate=(dayOfMonth+"/"+month+"/"+year);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date strDate = sdf.parse(selectedDate);
                            if (new Date().after(strDate)) {
                                //Toast.makeText(getApplicationContext(),"pldsa",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                y=year;
                                m=month;
                                d=dayOfMonth;
                                tv.setText(d + "/" + m + "/" + y);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }



                        // tv.setText(dayOfMonth+"/"+month+"/"+year);
                    }
                },y,m,d);
                dpd.show();
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(this,Integer.toString(resultCode),Toast.LENGTH_SHORT).show();
        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                dId=place.getId();
                dName= (String) place.getName();
                String toastMsg = String.format("Place: ", place.getName());
                //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                langlat = place.getLatLng();

                TextView ls=findViewById(R.id.new_trip_locationset);
                ls.setVisibility(View.VISIBLE);
                locationSet=true;
//                String newURL="https://www.google.com/maps/search/?api=1&query="+destinationName+ "&query_place_id=" + destinationId;
//                Uri gmmIntentUri = Uri.parse(newURL);
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                if (mapIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(mapIntent);
//                }
            }
        }
        else if (requestCode==1)
        {
            if (resultCode == RESULT_OK) {
                Intent i=new Intent();
                setResult(Activity.RESULT_OK,data);
                finish();
            }
        }
        else if (requestCode==REQ_CODE_SPEECH_INPUT)
        {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                TextView tn=findViewById(R.id.new_trip_name);
                tn.setText(result.get(0));
            }
        }
        else
            finish();
    }

    public void mapClick(View view) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder;
        builder = new PlacePicker.IntentBuilder();

        startActivityForResult(builder.build(this), 5);
    }


    public void recordbtn(View view){
        promptSpeechInput();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say Trip Name");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void btnclik(View view){
        TextView tn=findViewById(R.id.new_trip_name);

        if (TextUtils.isEmpty(tn.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Enter Trip Name",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!locationSet)
        {
            Toast.makeText(getApplicationContext(),"Please select Trip Location",Toast.LENGTH_SHORT).show();
            return;
        }

        String date=d+"/"+m+"/"+y;

        tripName= tn.getText().toString();
        tripName=tripName.trim();

        if (tripName.length()==0)
        {
            Toast.makeText(getApplicationContext(),"Enter Trip Name",Toast.LENGTH_SHORT).show();
            return;
        }

        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(tripName);
        boolean b = m.find();

        if (b)
        {
            Toast.makeText(getApplicationContext(), "Special Characters are not allowed",Toast.LENGTH_SHORT).show();
            return;
        }

        if (tripName.startsWith("0") || tripName.startsWith("1") || tripName.startsWith("2") || tripName.startsWith("3") || tripName.startsWith("4") || tripName.startsWith("5") ||
                tripName.startsWith("6") || tripName.startsWith("7") || tripName.startsWith("9") || tripName.startsWith("8"))
        {
            Toast.makeText(getApplicationContext(), "Trip name cannot start with a number",Toast.LENGTH_SHORT).show();
            return;
        }

        //dId="s";
        dName=langlat.toString();

        String []x=dName.split("/");

        Intent i=new Intent(this,NewTripBuddies.class);
        i.putExtra("date",date);
        i.putExtra("tn",tripName);
        i.putExtra("tid",dId);
        i.putExtra("dn",langlat.toString());
        i.putExtra("lat",langlat.latitude);
        i.putExtra("long",langlat.longitude);
        startActivityForResult(i,1);
    }
}
