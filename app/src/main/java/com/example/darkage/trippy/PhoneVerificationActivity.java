package com.example.darkage.trippy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {
    FirebaseAuth auth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        auth=FirebaseAuth.getInstance();
    }

    public void onclc(View view)
    {
        EditText p=(EditText) findViewById(R.id.pnED);

        if (TextUtils.isEmpty(p.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Enter Valid Phone Number",Toast.LENGTH_SHORT).show();
            return;
        }


        final String pn = "+92" + p.getText().toString();
        if (pn.length()!=13)
        {
            Toast.makeText(getApplicationContext(),"Enter Valid Phone Number",Toast.LENGTH_SHORT).show();
            return;

        }

        Toast.makeText(getApplicationContext(),pn,Toast.LENGTH_LONG).show();



        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                //Log.d(TAG, "onVerificationCompleted:" + credential);

                //signInWithPhoneAuthCredential(credential);

                Toast.makeText(getApplicationContext(),"Verification Successful",Toast.LENGTH_LONG).show();
                auth.getCurrentUser().updatePhoneNumber(credential);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                //Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(getApplicationContext(),"onCodeSent",Toast.LENGTH_LONG).show();

                // ...
            }


        };


        PhoneAuthProvider.getInstance().verifyPhoneNumber(pn,60, TimeUnit.SECONDS,this,mCallbacks);




    }

}

