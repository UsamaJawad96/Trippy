package com.example.darkage.trippy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
    }



    private void updateUI(FirebaseUser currentUser) {
    }

    public void loginBtn(View view)
    {
        Vibrator v= (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(150);

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //prefs.edit().putBoolean("Islogin", Islogin).commit(); // islogin is a boolean value of your login status
        EditText em=findViewById(R.id.login_email);
        String email=em.getText().toString();

        EditText ps=findViewById(R.id.login_password);
        String password=ps.getText().toString();

        if (TextUtils.isEmpty(em.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Enter Valid Email",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(ps.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Enter Valid Password", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getApplicationContext(), "Logging In", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Your login is successful",
                                    Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_FIRST_USER);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed. Try Again",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
            });
//                }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    //e.printStackTrace();
//                    // did not set value
//                    Toast.makeText(getApplicationContext(),"FAIL",Toast.LENGTH_SHORT).show();
//                }
//            });


    }

    public void registerBtn(View view)
    {
        setResult(2);
        finish();
    }


    public void forgotPassword(View view)
    {
        Toast.makeText(getApplicationContext(),"this button is not active",Toast.LENGTH_SHORT).show();
        //new activity asking for email
        //validate email and send link
    }
}
