package com.example.darkage.trippy;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    List<String> existingUnames;
    boolean usercheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setSubtitle("Register");

        mAuth = FirebaseAuth.getInstance();
        existingUnames=new ArrayList<>();
        usercheck=false;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    existingUnames.add ((String) data.child("userName").getValue());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final EditText unn = (EditText) findViewById(R.id.registerUserName);

        unn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //Toast.makeText(getApplicationContext(),"User Name ",Toast.LENGTH_LONG).show();
                    String temp=unn.getText().toString();

                    if (TextUtils.isEmpty(unn.getText().toString()))
                    { return; }

                    if (temp.length()>=3 && temp.length()<=8){
                        if (existingUnames.contains(temp))
                        {
                            Toast.makeText(getApplicationContext(),"User Name Already Exists",Toast.LENGTH_LONG).show();
                            return;
                        }
                        else
                        {
                            usercheck=true;
                        }
                    }

                }
            }
        });
    }

    public void register(View view){
        EditText em=findViewById(R.id.registerEmail);
        final String email=em.getText().toString();

        EditText ps=findViewById(R.id.registerPassword);
        String password=ps.getText().toString();

        EditText un=findViewById(R.id.registerUserName);
        final String username=un.getText().toString();

//        EditText pn=findViewById(R.id.phone_number);
//        final String phone="+92"+pn.getText().toString();

        if (TextUtils.isEmpty(em.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Enter Valid Email",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(ps.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Enter Valid Password", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (password.length()<6)
        {
            Toast.makeText(getApplicationContext(), "Enter Valid Password of Length 6", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!usercheck)
        {
            Toast.makeText(getApplicationContext(), "Enter Valid UserName", Toast.LENGTH_SHORT).show();
            return;
        }

        //mAuth.createUserWithEmailAndPassword(un,pa);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();

                            UserClass newUser=new UserClass(username,null,user.getUid(),email);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            DatabaseReference myRef = database.getReference();
                            myRef.child("users").child(user.getUid()).setValue(newUser);
                            /*ß
                                ALSO SAVE REGISTERED USER DATA TO DATABASE
                             */
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .setPhotoUri(null)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });

                            setResult(Activity.RESULT_FIRST_USER);
                            finish();
                        } else {
                            
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }
}
