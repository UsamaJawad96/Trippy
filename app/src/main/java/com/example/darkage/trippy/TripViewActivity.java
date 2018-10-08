package com.example.darkage.trippy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TripViewActivity extends AppCompatActivity {
    TripClass trip;
    TextToSpeech ts;
    public ArrayList<MessageClass> messages;
    FirebaseDatabase database;
    ViewPagerAdapter adapter;
    SensorManager sensorManager;
    Sensor rotationVectorSensor;
    SensorEventListener rvListener;



    GoogleMap map;

    int check=0;

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(rvListener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View Trip");




        Intent data=getIntent();
        trip=(TripClass)data.getSerializableExtra("tripdetails");

        getSupportActionBar().setSubtitle(trip.getTrip_name());

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TripHomeFragment(), "Home");
        adapter.addFragment(new TripChatFragment(),"Chat");
        adapter.addFragment(new TripBuddyListFragment(),"Buddies");
//        adapter.addFragment(new FragmentTwo(), "FRAG2");
//        adapter.addFragment(new FragmentThree(), "FRAG3");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        ts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    ts.setLanguage(Locale.US);
                    ts.setSpeechRate((float) 1);
                }
            }
        });

        messages=new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("messages").child(trip.getTripId());

        myRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.child("isImage").getValue(Boolean.class)!=null)
                    {
                        boolean isIm = ds.child("isImage").getValue(Boolean.class);
                        String dat = ds.child("messageDate").getValue(String.class);
                        String sender = ds.child("sender").getValue(String.class);
                        String senID = ds.child("senderId").getValue(String.class);



                        if (isIm)
                        {
                            String img = ds.child("image64").getValue(String.class);
                            MessageClass mm=new MessageClass(img,sender,senID,dat,true);
                            int check=1;
                            for (int i=0;i<messages.size();i++)
                            {
                                if (messages.get(i).getSenderId().equals(senID))
                                {
                                    check=-1;
                                }
                            }

                            if (check!=-1)
                                messages.add(mm);
                            //check if already added
                            //messages.add(mm);
                        }
                        else
                        {
                            String mesg = ds.child("mText").getValue(String.class);
                            MessageClass mm=new MessageClass(mesg,sender,senID,dat);
                            //check if already added
                            int check=1;

                            for (int i=0;i<messages.size();i++)
                            {
                                if (messages.get(i).getSenderId().equals(senID))
                                {
                                    check=-1;
                                }
                            }

                            if (check!=-1)
                                messages.add(mm);
                        }
                    }



                }
                ((TripChatFragment)adapter.getItem(1)).loadData(messages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

         rvListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // More code goes here
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, sensorEvent.values);

                // Remap coordinate system
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix);
                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                for(int i = 0; i < 3; i++) {
                    orientations[i] = (float)(Math.toDegrees(orientations[i]));
                }

                if(orientations[2] > 55) {
                    if (check%10==0)
                        Toast.makeText(getApplicationContext(),"Screen Rotation Not Allowed",Toast.LENGTH_SHORT).show();
                    check++;
                } else if(orientations[2] < -55) {
                    if (check%10==0)
                        Toast.makeText(getApplicationContext(),"Screen Rotation Not Allowed",Toast.LENGTH_SHORT).show();
                    check++;
                } else if(Math.abs(orientations[2]) < 10) {

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.meuntripview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                Intent i=new Intent();
                i.putExtra("trip",(Serializable)trip);
                setResult(Activity.RESULT_OK,i);
                finish();
                return true;
            case R.id.speak:
                String toSpeak = "Trip Name is " + trip.getTrip_name() + ". Total Number of Buddies are " + Integer.toString(trip.getTotal_buddies());
                //Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                ts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            case R.id.leavet:
                //Toast.makeText(getApplicationContext(),"btn",Toast.LENGTH_SHORT).show();
                makedialog();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void makedialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(TripViewActivity.this);

        builder.setMessage("Are You Sure You Want To Leave This Trip? This Cannot Be Undone")
                .setTitle("Leave Trip")
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                removeBuddy(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                myRef.child("trips").child(trip.getTripId()).child("tripMembers").setValue(trip.getTripMembers());
                dialog.dismiss();
                Intent i=new Intent();
                i.putExtra("trip",(Serializable)trip);
                setResult(Activity.RESULT_OK,i);
                finish();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void viewLocation1(View view){

        String destinationName=trip.getDestinationName();
        String destinationId=trip.getDestinationId();
        String newURL="https://www.google.com/maps/search/?api=1&query="+destinationName+ "&query_place_id=" + destinationId;
        Uri gmmIntentUri = Uri.parse(newURL);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public TripClass gettp(){
        return trip;
    }

    public ArrayList<MessageClass> getmessage(){
        return messages;
    }

    public boolean removeBuddy(String s){
        trip.getTripMembers().remove(s);
        trip.setTotal_buddies(trip.getTotal_buddies());
        return true;
    }

    public void addMessage(MessageClass m)
    {
        //messages.add(m);
        DatabaseReference myRef5=database.getReference();
        myRef5.child("messages").child(trip.getTripId()).push().setValue(m);
        ((TripChatFragment)adapter.getItem(1)).loadData(messages);
    }

    public void pictureMessage(){
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 100);
                }
    }


    protected String genTripId() {
        String acceptableChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * acceptableChars.length());
            salt.append(acceptableChars.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                //imageView.setImageBitmap(photo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteFormat = stream.toByteArray();
                String encodedImage = Base64.encodeToString(byteFormat, Base64.NO_WRAP);


                String userna= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                //mTxt.setText("");

                MessageClass mms=new MessageClass(encodedImage,userna,genTripId(),(new Date().toString()),true);
                //MessageClass mms=new MessageClass(msg,userna,genTripId(),(new Date().toString()));
                messages.add(mms);
                DatabaseReference myRef5=database.getReference();
                myRef5.child("messages").child(trip.getTripId()).setValue(messages);
                ((TripChatFragment)adapter.getItem(1)).loadData(messages);
            }
        }
    }

    public ShareLinkContent shareTrip(){
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setQuote("Hey There, me and " + Integer.toString(trip.getTotal_buddies()) + " of my buddies are going on a Trip on " + trip.getDate() + " #Trippy")
                .setContentTitle(trip.getTrip_name())
                //.setContentDescription("App descr")
                .setContentUrl(Uri.parse("www.trippyforsmd.com"))
                .build();

        return content;
    }

    public void shareTripDialog(){
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setQuote("Hey There, me and " + Integer.toString(trip.getTotal_buddies()) + " of my buddies are going on a Trip on " + trip.getDate() + " #Trippy")
                .setContentTitle(trip.getTrip_name())
                //.setContentDescription("App descr")
                .setContentUrl(Uri.parse("www.trippyforsmd.com"))
                .build();

        ShareDialog sh = new ShareDialog(this);
        sh.show((Activity) getApplicationContext(), content);

    }
}

