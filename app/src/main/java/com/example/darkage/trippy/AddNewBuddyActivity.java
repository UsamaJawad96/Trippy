package com.example.darkage.trippy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddNewBuddyActivity extends AppCompatActivity {
    ArrayList<String> userNames=new ArrayList();
    ArrayList<String> userBuddies=new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_buddy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Add Buddy");

        userBuddies=new ArrayList<>();
        userBuddies.addAll(userBuddyList.getInstance().buddyUsernames);

        userNames=new ArrayList<>();
        userNames.addAll(globalUserList.getInstance().Usernames);

        FirebaseUser currUser=FirebaseAuth.getInstance().getCurrentUser();
        userNames.remove(currUser.getDisplayName());

        ArrayList<String> temp= new ArrayList<>();
        temp.addAll(userBuddyList.getInstance().buddyUsernames);

        for (int i=0;i<userNames.size();i++)
        {
            if (temp.contains(userNames.get(i))) {
                userNames.remove(userNames.get(i));
                i--;
            }
        }

//        userNames.add("No more buddies left");

        Spinner s = (Spinner) findViewById(R.id.addbuddyspinner);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, userNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);



        if (userNames.size()==0)
        {
            Toast.makeText(this,"No More Buddies Left To Add",Toast.LENGTH_LONG).show();
            s.setEnabled(false);
            ImageButton bt=findViewById(R.id.addbuddybtn);
            bt.setEnabled(false);
            //Drawable rep=getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark);
            //bt.setBackground(rep);
            bt.setBackgroundColor(0xFF2A2A2A);
            //bt.setImageDrawable(rep);
        }

        ListView ls=findViewById(R.id.addedbuddylist);
        adapter2= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,userBuddies);
        ls.setAdapter(adapter2);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, userNames);
//        AutoCompleteTextView textView = findViewById(R.id.autoCompleteTextView);
//        textView.setAdapter(adapter);
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

    public void addNewBuddy(View view){
        Spinner s = (Spinner) findViewById(R.id.addbuddyspinner);
        String ss=s.getSelectedItem().toString();
        userBuddies.add(ss);
        userNames.remove(ss);
        adapter.notifyDataSetChanged();
        adapter2.notifyDataSetChanged(); //update on cloud?

        if (userNames.size()==0)
        {
            Toast.makeText(this,"No More Buddies Left To Add",Toast.LENGTH_LONG).show();
            s.setEnabled(false);
            ImageButton bt=findViewById(R.id.addbuddybtn);
            bt.setEnabled(false);
            bt.setBackgroundColor(0xFF2A2A2A);
        }


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currUser=FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference myRef = database.getReference();
        myRef.child("users").child(currUser.getUid()).child("buddyList").setValue(userBuddies);
    }


//    public void addBud(View view){
//
////        AutoCompleteTextView a=findViewById(R.id.autoCompleteTextView);
////        String usern=a.getText().toString();
////
////        if (TextUtils.isEmpty(a.getText().toString()))
////        {
////            Toast.makeText(getApplicationContext(),"Enter Valid UserName",Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        if (!userNames.contains(usern))
////        {
////            Toast.makeText(getApplicationContext(),"User Doesn't Exist",Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        int idindex=globalUserList.getInstance().Usernames.indexOf(usern);
////        String id=globalUserList.getInstance().Userids.get(idindex);
////
////        ArrayList<String> un=new ArrayList();
////        userBuddyList.getInstance().addBuddy(id,usern);
////
////        un=userBuddyList.getInstance().buddyUsernames;
////
////        FirebaseDatabase database = FirebaseDatabase.getInstance();
////        FirebaseUser currUser=FirebaseAuth.getInstance().getCurrentUser();
////
////        DatabaseReference myRef = database.getReference();
////        myRef.child("users").child(currUser.getUid()).child("buddyList").setValue(un);
////        setResult(Activity.RESULT_OK);
////        finish();
//    }

}
