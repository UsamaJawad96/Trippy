package com.example.darkage.trippy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;


public class TripHomeFragment extends Fragment {
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    MapView map;

    public TripHomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TripHomeFragment newInstance(String param1, String param2,String param3, String param4) {
        TripHomeFragment fragment = new TripHomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareDialog shareDialog = new ShareDialog(this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_trip_home, container, false);



        TripViewActivity ac= (TripViewActivity) getActivity();
        final TripClass t=ac.gettp();
        if (t!=null)
            setScreen(t,view);

        ShareButton btn=view.findViewById(R.id.fb_share_button);
        btn.setShareContent(ac.shareTrip());

        ImageButton im = view.findViewById(R.id.imageButton2);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet(((TripViewActivity) getActivity()).trip);
            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        map=view.findViewById(R.id.mapview);
        map.onCreate(mapViewBundle);

//        map.onResume();
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(t.getLang(),t.getLat()))).setTitle("bruh");
                googleMap.addMarker(new MarkerOptions().position(new LatLng(t.getLat(), t.getLang())).title(t.getTrip_name())).setTitle(t.getTrip_name());
                gmap = googleMap;
                gmap.setMinZoomPreference(50);
                UiSettings uiSettings = gmap.getUiSettings();
                uiSettings.setZoomControlsEnabled(true);
                uiSettings.setRotateGesturesEnabled(true);
                uiSettings.setScrollGesturesEnabled(true);
                uiSettings.setTiltGesturesEnabled(true);
                uiSettings.setZoomGesturesEnabled(true);
                LatLng ny = new LatLng(t.lat, t.lang);

                CameraPosition.Builder camBuilder = CameraPosition.builder();
                camBuilder.bearing(45);
                camBuilder.tilt(30);
                camBuilder.target(ny);
                camBuilder.zoom(18);


                CameraPosition cp = camBuilder.build();

                gmap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
            }
        });

        return view;
    }

    public void setScreen(TripClass trip,View view){
        TextView t1=(TextView)view.findViewById(R.id.fragment_trip_admin);
        t1.setText(trip.getTrip_admin_name());
        TextView t2=(TextView)view.findViewById(R.id.fragment_total_buddies);
        t2.setText(Integer.toString(trip.getTotal_buddies()));
        TextView t3=(TextView)view.findViewById(R.id.fragment_trip_date);
        t3.setText(trip.getDate());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        map.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        TripViewActivity tc= (TripViewActivity) getActivity();

        setScreen(tc.gettp(),getView());
    }

    void tweet(TripClass trip){
        shareTwitter("Hey There, me and " + Integer.toString(trip.getTotal_buddies()) + " of my buddies are going on a Trip on " + trip.getDate() + " #Trippy");
    }


    private void shareTwitter(String message) {
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, message);
        tweetIntent.setType("text/plain");

        PackageManager packManager = getActivity().getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(tweetIntent);
        } else {
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, message);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(message)));
            startActivity(i);
            Toast.makeText(getActivity(), "Twitter app isn't found", Toast.LENGTH_LONG).show();
        }
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //Log.wtf(TAG, "UTF-8 should always be supported", e);
            return "";
        }
    }
}
