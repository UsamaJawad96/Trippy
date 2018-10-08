package com.example.darkage.trippy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.darkage.trippy.adapter.MyAdapter;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<TripClass> trip_data;
    ArrayList<TripClass> trip_history;
    GestureDetector gestureDetector;
//    UserClass currentUser;
    private FirebaseAuth mAuth;
    String userId;
    FirebaseDatabase database;
    private AdView mAdView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private DrawerLayout mDrawerLayout;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_INVITE = 690;
    FloatingActionButton fab;
    PopupMenu popup;
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecyclerView=findViewById(R.id.trip_recycleview);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        fab=findViewById(R.id.floatingActionButton);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.homeitem);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
                        //Toast.makeText(getApplicationContext(),menuItem.getTitle(),Toast.LENGTH_SHORT).show();
                        int id = menuItem.getItemId();
                        Fragment fragment = null;

                        if (id==R.id.view_profile)
                        {
                            fragment=new ProfileFragment();
                            mRecyclerView.setVisibility(View.INVISIBLE);
                            fab.setVisibility(View.INVISIBLE);
                            menuItem.setChecked(true);

                        }
                        else if (id==R.id.homeitem)
                        {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.VISIBLE);
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            fragment=new ProfileFragment();
                            ft.replace(R.id.cts, fragment);
                            ft.remove(fragment);
                            ft.commit();
                            fragment=null;
                            menuItem.setChecked(true);
                        }
                        else if (id==R.id.invite_friends){
                            Intent intent = new AppInviteInvitation.IntentBuilder("Join Trippy, Now!")
                                    .setMessage("Try out Trippy now")
                                    .setDeepLink(Uri.parse("https://r6cmx.app.goo.gl/V9Hh"))
                                    .setCallToActionText("Find Data")
                                    .build();
                            startActivityForResult(intent, REQUEST_INVITE);
                            menuItem.setChecked(false);

                        }
                        else if (id==R.id.triphistory)
                        {
                            Intent i=new Intent(MainActivity.this,TripHistoryActivity.class);
                            i.putExtra("h",trip_history);
                            startActivity(i);
                            menuItem.setChecked(false);

                        }
                        else if (id==R.id.logout_button)
                        {
                            mAuth.signOut();
                            setResult(Activity.RESULT_OK);
                            finish();

                        }
                        else if (id==R.id.addbud)
                        {
                            Intent i=new Intent(MainActivity.this,AddNewBuddyActivity.class);
                            startActivityForResult(i,2);
                            menuItem.setChecked(false);

                        }
                        else if (id==R.id.sensor)
                        {
                            Intent i=new Intent(MainActivity.this,ShakeActivity.class);
                            startActivity(i);
                            menuItem.setChecked(false);
                        }


                        if (fragment != null) {

                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.cts, fragment);
                            ft.commit();
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });


        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
                        if (mRecyclerView.getVisibility()==View.VISIBLE)
                        {
                            navigationView.setCheckedItem(R.id.homeitem);
                        }
                        else
                            navigationView.setCheckedItem(R.id.view_profile);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );





        ///////////////////   Firebase User
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //database.setPersistenceEnabled(true);  //DOES THIS CRASH THE APP?
        final FirebaseUser currUser=mAuth.getCurrentUser();
        userId=currUser.getUid();
        userBuddyList.getInstance().clear();
        globalUserList.getInstance().clear();



        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);



        ///////////////////        ///////////////////

        /////DEFAULT DATA
        trip_data=new ArrayList<>();
        trip_history=new ArrayList<>();
        /////

        final Comparator<TripClass> comparator = new Comparator<TripClass>() {
            @Override
            public int compare(TripClass o1, TripClass o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    return (sdf.parse(o1.getDate()).compareTo(sdf.parse(o2.getDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };


        /////////////////// FIREBASE DATABASE
        DatabaseReference myRef = database.getReference().child("trips");

        myRef.addValueEventListener(new ValueEventListener() {         /////////////////// GET ALL USER TRIPS
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<TripClass> names= new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String da = ds.child("date").getValue(String.class);
                    String did = ds.child("destinationId").getValue(String.class);
                    String dn = ds.child("destinationName").getValue(String.class);
                    int tb = ds.child("total_buddies").getValue(Integer.class);
                    String trd = ds.child("tripId").getValue(String.class);
                    String taid = ds.child("trip_admin_id").getValue(String.class);
                    String tad = ds.child("trip_admin_name").getValue(String.class);
                    String tn = ds.child("trip_name").getValue(String.class);
                    double a= ds.child("lat").getValue(Double.class);
                    double b=ds.child("lang").getValue(Double.class);
                    //get tripmembers

                    ArrayList<String> members=new ArrayList<>();
                    for (DataSnapshot childDataSnapshot : ds.child("tripMembers").getChildren()) {
                        members.add(childDataSnapshot.getValue(String.class));
                    }

                    if (members.contains(currUser.getDisplayName())) {

                        //TripClass tp=new TripClass(trID,trip_name,date,userId,usrname,destinationId,destinationName,tripMembers);
                        //String name = ds.child("name").getValue(String.class);
                        TripClass temp = (new TripClass(trd, tn, da, taid, tad, did, dn, members,b,a));
                        boolean tp = true;

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date strDate = null;
                        try {
                            strDate = sdf.parse(da);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(strDate);
                            cal.add(Calendar.DATE,1);
                            strDate.setTime(cal.getTimeInMillis());

                            if (new Date().after(strDate)) {
                                //Toast.makeText(getApplicationContext(),"pldsa",Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < trip_history.size(); i++) {
                                    if (trip_history.get(i).getTripId().equals(temp.getTripId())) {
                                        tp = false;
                                        break;
                                    }
                                }
                                if (tp)
                                    trip_history.add(temp);
                            }
                            else
                            {
                                for (int i = 0; i < trip_data.size(); i++) {
                                    if (trip_data.get(i).getTripId().equals(temp.getTripId())) {
                                        tp = false;
                                        trip_data.remove(i);
                                        trip_data.add(i,temp);
                                        break;
                                    }
                                }
                                if (tp)
                                    trip_data.add(temp);
                                mAdapter.notifyDataSetChanged();
                            }


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        for (int i=0;i<trip_data.size();i++)
                        {
                            if (trip_data.get(i).getTripId().equals(trd))
                            {
                                trip_data.remove(trip_data.get(i));
                                 break;
                            }
                        }
                    }
                }

                Collections.sort(trip_data,comparator);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        ///////////////////  GET USER BUDDY LIST
        DatabaseReference myRef2 = database.getReference().child("users").child(currUser.getUid()).child("buddyList");
        final userBuddyList bdobj=userBuddyList.getInstance();

        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String un = ds.getValue(String.class);

                    if (!bdobj.buddyUsernames.contains(un))
                        bdobj.addUN(un);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        DatabaseReference ref3 = database.getReference().child("users");

        final globalUserList obj=globalUserList.getInstance();

        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String id = ds.child("id").getValue(String.class);
                    String un= ds.child("userName").getValue(String.class);

                    if (!obj.Usernames.contains(un)) {
                        obj.addBuddy(id, un);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        ///////////////////        ///////////////////



        /////////////////// RECYCLER VIEW SETUP

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(trip_data);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(this);

        mAdapter.notifyDataSetChanged();
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //Toast.makeText(c,"onSingleTap",Toast.LENGTH_SHORT).show();

                View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {

                    //Toast.makeText(getApplicationContext(), Integer.toString(mRecyclerView.getChildAdapterPosition(child)), Toast.LENGTH_LONG).show();
                    int curr=mRecyclerView.getChildAdapterPosition(child);


                    Intent i=new Intent(MainActivity.this,TripViewActivity.class);
                    i.putExtra("tripdetails",(Serializable)trip_data.get(curr));
                    startActivityForResult(i,4);

                }
                return true;
            }


            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

                if (child != null) {

                    //Toast.makeText(getApplicationContext(), "Long Press " + Integer.toString(mRecyclerView.getChildAdapterPosition(child)), Toast.LENGTH_SHORT).show();
                    final int curr=mRecyclerView.getChildAdapterPosition(child);

                }
            }
        });
        ///////////////////        ///////////////////


    }

    /////////////////// MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trippy_home_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
                    mDrawerLayout.closeDrawers();
                }
                else
                {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key);
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            case R.id.logout:
                mAuth.signOut();
                setResult(Activity.RESULT_OK);
                finish();
                return true;
            case R.id.profile:
                Vibrator v= (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
                Toast.makeText(getApplicationContext(),"Please Use Contextual Menu For Navigation",Toast.LENGTH_SHORT).show();
//                int tripss=trip_data.size()+trip_history.size();
//                Intent i=new Intent(this,UserProfileActivity.class);
//                i.putExtra("history",(Serializable)trip_history);
//                i.putExtra("number",tripss);
//
//                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    ///////////////////        ///////////////////

    public int getData(){
        int tripss=trip_data.size()+trip_history.size();
        return tripss;
    }

    public void newTripBtn(View view){
       // Toast.makeText(getApplicationContext(),"New Trip",Toast.LENGTH_SHORT).show();
        Intent i=new Intent(this,NewTripActivity.class);
        startActivityForResult(i,1);
    }

    protected String genTripId() {
        String acceptableChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 15) { // length of the random string.
            int index = (int) (rnd.nextFloat() * acceptableChars.length());
            salt.append(acceptableChars.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==7)
        {
            if (resultCode!=RESULT_OK)
                finish();
                return;
        }
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    //Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
            return;
        }

        if (data==null)
            return;

        /////////////////// CREATE NEW TRIP
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String trID=genTripId();
                userId=mAuth.getCurrentUser().getUid();
                String usrname=mAuth.getCurrentUser().getDisplayName();

                String trip_name=data.getStringExtra("tn");
                String date=data.getStringExtra("date");
                String destinationId=data.getStringExtra("tid");
                String destinationName=data.getStringExtra("dn");
                double lang=data.getDoubleExtra("long",0);
                double lat=data.getDoubleExtra("lat",0);
                ArrayList<String> tripMembers=new ArrayList<>();
                tripMembers.addAll((ArrayList<String>)data.getSerializableExtra("buddies"));

                TripClass tp=new TripClass(trID,trip_name,date,userId,usrname,destinationId,destinationName,tripMembers,lang,lat);
                tp.setLang(lang);
                tp.setLat(lat);
                trip_data.add(tp);

                //add tp to database
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference myRef = database.getReference();
                myRef.child("trips").child(trID).setValue(tp);
                mAdapter.notifyDataSetChanged();
                addLog(destinationId,destinationName,trID);

                ArrayList<MessageClass> messages=new ArrayList<>();
                messages.add(new MessageClass(("Welcome to "+trip_name),"Default","000066669999",(new Date().toString())));
                DatabaseReference myRef5=database.getReference();
                myRef5.child("messages").child(trID).setValue(messages);

                Vibrator v= (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(150);
            }
        }    ///////////////////   UPDATE TRIP
        else if (requestCode == 4)
        {
            //if (resultCode==RESULT_OK)
            {
                TripClass returnTrip= (TripClass) data.getSerializableExtra("trip");
                String id=returnTrip.getTripId();
                for (int i=0; i<trip_data.size();i++)
                {
                    if (trip_data.get(i).getTripId().equals(id))
                    {
                        trip_data.remove(i);
                        trip_data.add(i,returnTrip);
                        mAdapter.notifyDataSetChanged();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                        DatabaseReference myRef = database.getReference();
                        myRef.child("trips").child(returnTrip.getTripId()).child("tripMembers").setValue(returnTrip.getTripMembers());
                        myRef.child("trips").child(returnTrip.getTripId()).child("total_buddies").setValue(returnTrip.getTotal_buddies());
                        break;
                    }
                }
            }
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }


    /////////////////// FIREBASE ANALYTICS
    @SuppressLint("InvalidAnalyticsName")
    private void addLog(String destinationId, String destinationName, String id) {
        Bundle bundle=new Bundle();
        bundle.putString("DEST_ID", destinationId);
        bundle.putString("DEST_NAME", destinationName);
        mFirebaseAnalytics.logEvent("NEW_TRIP", bundle);
    }
    ///////////////////

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
