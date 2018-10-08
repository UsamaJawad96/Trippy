package com.example.darkage.trippy;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import android.Manifest;


public class TripChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    EditText mTxt;
    ArrayList<MessageClass> messagelist;
    ImageButton snd;
    ImageButton camera;

    public TripChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static TripChatFragment newInstance(String param1, String param2) {
        TripChatFragment fragment = new TripChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_trip_chat, container, false);
        mRecyclerView=view.findViewById(R.id.messageView);
        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessageViewAdapter(messagelist);
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.HORIZONTAL));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mAdapter.notifyDataSetChanged();
        mTxt=view.findViewById(R.id.messageText);
        snd=(ImageButton)view.findViewById(R.id.messageSend);
        camera=(ImageButton)view.findViewById(R.id.messageCamera);

        snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"BTN",Toast.LENGTH_SHORT).show();

                if (TextUtils.isEmpty(mTxt.getText().toString()))
                {
                    //Toast.makeText(getApplicationContext(),"Enter Trip Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                String msg=mTxt.getText().toString();


                msg=msg.trim();

                if (msg.length()==0)
                {
                    mTxt.setText("");
                    return;

                }

                String userna= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                mTxt.setText("");

                MessageClass mms=new MessageClass(msg,userna,genTripId(),(new Date().toString()));
                messagelist.add(mms);
                mAdapter.notifyDataSetChanged();
                ((TripViewActivity)getActivity()).addMessage(mms);
            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
//                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, 100);
//                }
                if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            1234);
                }
                else
                {
                    ((TripViewActivity)getActivity()).pictureMessage();
                }

            }
        });

        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1234) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                ((TripViewActivity)getActivity()).pictureMessage();
            }
            else {
                Toast.makeText(getActivity(),"Permission not granted",Toast.LENGTH_SHORT).show();
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }

    public void loadData(ArrayList<MessageClass> m)
    {
        messagelist=m;
        if (mAdapter!=null)
            mAdapter=new MessageViewAdapter(messagelist);

        if (mRecyclerView!=null) {
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mLayoutManager.smoothScrollToPosition(mRecyclerView, null, mAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


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




}
