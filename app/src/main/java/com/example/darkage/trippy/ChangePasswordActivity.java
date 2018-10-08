package com.example.darkage.trippy;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser currUser;
    TextView op;
    TextView np;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mAuth = FirebaseAuth.getInstance();
        currUser=mAuth.getCurrentUser();
        op=findViewById(R.id.old_password);
        np=findViewById(R.id.new_password);
    }

    public void btnClick(View view)
    {
        if (TextUtils.isEmpty(op.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Enter Valid Email",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(np.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Enter Valid Password", Toast.LENGTH_SHORT).show();
            return;
        }

        String opassword=op.getText().toString();
        final String npassword=np.getText().toString();

        if (opassword.length()<6 || np.length()<6)
        {
            Toast.makeText(getApplicationContext(), "Enter Valid Password of Length 6", Toast.LENGTH_SHORT).show();
            return;
        }

        final String email=currUser.getEmail();
        AuthCredential credential= EmailAuthProvider.getCredential(email,opassword);

        currUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    currUser.updatePassword(npassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                //some problem
                            }else {
                                setResult(Activity.RESULT_OK);
                                finish();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(),"Please Re-Enter Old Password",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }
}
