package com.example.darkage.trippy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.services.events.DisabledEventsStrategy;

public class StartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String mVerificationId;
    String mResendToken;
    List<String> existingUnames;


    private boolean mVerificationInProgress = false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String verificationid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser!=null) {
            if (currentUser.getDisplayName().equals("")) {
                disappear();
            } else {
                Intent i = new Intent(StartActivity.this, MainActivity.class);
                startActivityForResult(i, 1);
            }
        }

        existingUnames=new ArrayList<>();
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
//        else
//        {
//            Intent i=new Intent(StartActivity.this,LoginActivity.class);
//            startActivityForResult(i,2);
//        }

    }

    public void onVerify(View view){
        EditText pn=findViewById(R.id.phone_number_login);
        Vibrator v= (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(150);

        if (TextUtils.isEmpty(pn.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Enter Valid Phone Number",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (pn.getText().toString().length()!=10){
            Toast.makeText(getApplicationContext(),"Enter Valid Phone Number",Toast.LENGTH_SHORT).show();
            return;
        }
        //else
            //Toast.makeText(getApplicationContext(),"DAS",Toast.LENGTH_SHORT).show();

        String phone="+92"+pn.getText().toString();
        startPhoneNumberVerification(phone);
    }

    public void onVerifyCode(View view){
        EditText pn=findViewById(R.id.phone_number_login);
        EditText cd=findViewById(R.id.code_login);

        Vibrator v= (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(150);

        if (TextUtils.isEmpty(pn.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Enter Valid Code",Toast.LENGTH_SHORT).show();
            return;
        }
        else  if (TextUtils.isEmpty(cd.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Enter Valid Code",Toast.LENGTH_SHORT).show();
            return;
        }

        verifyPhoneNumberWithCode(verificationid,cd.getText().toString());


    }

    private void startPhoneNumberVerification(String phoneNumber) {
        TextView tv=findViewById(R.id.enterpn);
        tv.setText("Enter Code");

        TextView tv2=findViewById(R.id.phone_number92);
        tv2.setVisibility(View.INVISIBLE);

        EditText pn=findViewById(R.id.phone_number_login);
        pn.setVisibility(View.GONE);

        EditText pp=findViewById(R.id.code_login);
        pp.setVisibility(View.VISIBLE);

        Button btn=findViewById(R.id.verCode);
        btn.setVisibility(View.VISIBLE);

        Button btn2=findViewById(R.id.abcd);
        btn2.setVisibility(View.GONE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            TextView tv=findViewById(R.id.enterpn);
                            tv.setText("Enter Phone Number");

                            TextView tv2=findViewById(R.id.phone_number92);
                            tv2.setVisibility(View.VISIBLE);

                            EditText pn=findViewById(R.id.phone_number_login);
                            pn.setVisibility(View.VISIBLE);

                            EditText pp=findViewById(R.id.code_login);
                            pp.setVisibility(View.GONE);

                            Button btn=findViewById(R.id.verCode);
                            btn.setVisibility(View.GONE);

                            Button btn2=findViewById(R.id.abcd);
                            btn2.setVisibility(View.VISIBLE);

                            FirebaseUser user = task.getResult().getUser();

                            if (user.getDisplayName()==null) {
                                disappear();
                                return;
                            }
                            else if (user.getDisplayName().equals("")) {
                                disappear();
                                return;
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(StartActivity.this, MainActivity.class);
                                startActivityForResult(i, 1);
                            }
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                            reAppear();
                            Toast.makeText(getApplicationContext(),"Authentication Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setUserName(View view){
        EditText ds=findViewById(R.id.usernameText);
        ds.setVisibility(View.VISIBLE);


        if (TextUtils.isEmpty(ds.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Enter Valid UserName",Toast.LENGTH_SHORT).show();
            return;
        }
        String nam=ds.getText().toString();
        nam=nam.trim();

        if (nam.length()==0)
        {
            Toast.makeText(getApplicationContext(),"Enter Valid UserName",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (nam.length()<3)
        {
            Toast.makeText(getApplicationContext(),"Enter Valid UserName",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (nam.length()>10)
        {
            Toast.makeText(getApplicationContext(),"Enter Valid UserName",Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            if (existingUnames.contains(nam))
            {
                Toast.makeText(getApplicationContext(), "UserName Already Taken",Toast.LENGTH_SHORT).show();
                return;
            }
        }


        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(nam);
        boolean b = m.find();

        if (b)
        {
            Toast.makeText(getApplicationContext(), "Special Characters are not allowed",Toast.LENGTH_SHORT).show();
            return;
        }

        if (nam.startsWith("0") || nam.startsWith("1") || nam.startsWith("2") || nam.startsWith("3") || nam.startsWith("4") || nam.startsWith("5") ||
                nam.startsWith("6") || nam.startsWith("7") || nam.startsWith("9") || nam.startsWith("8"))
        {
            Toast.makeText(getApplicationContext(), "username cannot start with a number",Toast.LENGTH_SHORT).show();
            return;
        }


        FirebaseUser user=mAuth.getCurrentUser();

        UserClass newUser=new UserClass(nam,user.getUid(),null,user.getPhoneNumber());
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference();
        myRef.child("users").child(user.getUid()).setValue(newUser);
                            /*ß
                                ALSO SAVE REGISTERED USER DATA TO DATABASE
                             */
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(ds.getText().toString())
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

        reAppear();
        Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(StartActivity.this, MainActivity.class);
        startActivityForResult(i, 1);
    }

    void disappear(){
        TextView tv=findViewById(R.id.enterpn);
        tv.setText("Please Enter A Username");

        TextView tv2=findViewById(R.id.phone_number92);
        tv2.setVisibility(View.INVISIBLE);

        EditText pn=findViewById(R.id.phone_number_login);
        pn.setVisibility(View.GONE);

        EditText pp=findViewById(R.id.code_login);
        pp.setVisibility(View.GONE);

        Button btn=findViewById(R.id.verCode);
        btn.setVisibility(View.GONE);

        Button btn2=findViewById(R.id.abcd);
        btn2.setVisibility(View.GONE);

        EditText ds=findViewById(R.id.usernameText);
        ds.setVisibility(View.VISIBLE);

        Button bb=findViewById(R.id.setun);
        bb.setVisibility(View.VISIBLE);
    }

    void reAppear(){
        TextView tv=findViewById(R.id.enterpn);
        tv.setText("Enter Phone Number");

        TextView tv2=findViewById(R.id.phone_number92);
        tv2.setVisibility(View.VISIBLE);

        EditText pn=findViewById(R.id.phone_number_login);
        pn.setVisibility(View.VISIBLE);

        EditText pp=findViewById(R.id.code_login);
        pp.setVisibility(View.GONE);

        Button btn=findViewById(R.id.verCode);
        btn.setVisibility(View.GONE);

        Button btn2=findViewById(R.id.abcd);
        btn2.setVisibility(View.VISIBLE);

        EditText ds=findViewById(R.id.usernameText);
        ds.setVisibility(View.GONE);

        Button bb=findViewById(R.id.setun);
        bb.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationid = s;
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode== Activity.RESULT_OK){
//            Intent i=new Intent(StartActivity.this,LoginActivity.class);
//            startActivityForResult(i,2);
        }
        else if (resultCode==Activity.RESULT_FIRST_USER)
        {
            Intent i=new Intent(StartActivity.this,MainActivity.class);
            startActivityForResult(i,1);
        }
        else if (resultCode==2)
        {
//            Intent i=new Intent(StartActivity.this,RegisterActivity.class);
//            startActivityForResult(i,3);
        }
        else
            finish();
    }


}
