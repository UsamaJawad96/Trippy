package com.example.darkage.trippy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class NetworkCheck extends AsyncTask<Void, Void, Boolean> {
    Context c;
    public NetworkCheck(Context applicationContext) {
        c=applicationContext;
    }

    protected Boolean doInBackground(Void... params) {
        //do stuff
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        //do stuff
        //how to return a value to the calling method?
    }
}
