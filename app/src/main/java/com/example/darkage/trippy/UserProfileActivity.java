package com.example.darkage.trippy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser currUser;
    ArrayList<TripClass> th;
    int numberOfTrips;
    int totalBuddies;

    private static final int REQUEST_INVITE = 690;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Profile");
        mAuth = FirebaseAuth.getInstance();
        currUser=mAuth.getCurrentUser();

        Bundle b=getIntent().getExtras();
        th=new ArrayList<>();
        th= (ArrayList<TripClass>) b.getSerializable("history");
        numberOfTrips=b.getInt("number",0);

        totalBuddies=userBuddyList.getInstance().buddyUsernames.size();

        TextView t1=findViewById(R.id.profile_user_name);
        t1.setText(currUser.getDisplayName());

        TextView t2=findViewById(R.id.profile_number_buddies);
        t2.setText(Integer.toString(totalBuddies));

        TextView t3=findViewById(R.id.profile_total_trips);
        t3.setText(Integer.toString(numberOfTrips));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void btnback(View view){
        finish();
    }

    public void triphistorybtn(View view){
        Intent i=new Intent(UserProfileActivity.this,TripHistoryActivity.class);
        i.putExtra("h",th);
        startActivity(i);
    }

    public void resetpButton(View view){
        Intent i=new Intent(UserProfileActivity.this,ChangePasswordActivity.class);
        startActivityForResult(i,1);
    }

    public void newbuddybtn(View view)
    {
        Intent i=new Intent(UserProfileActivity.this,AddNewBuddyActivity.class);
        startActivityForResult(i,2);
    }

    public void inviteBtn(View view){
        onInviteClicked();
    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder("Join Trippy, Now!")
                .setMessage("Try out Trippy now")
                .setDeepLink(Uri.parse("https://r6cmx.app.goo.gl/V9Hh"))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText("Find Data")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1)
        {
            if (resultCode== Activity.RESULT_OK)
            {
                Toast.makeText(getApplicationContext(),"Password Change Successful",Toast.LENGTH_LONG).show();
            }
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
        }
        if (requestCode==2)
        {
            totalBuddies=userBuddyList.getInstance().buddyUsernames.size();


            TextView t2=findViewById(R.id.profile_number_buddies);
            t2.setText(Integer.toString(totalBuddies));
        }
    }
}
