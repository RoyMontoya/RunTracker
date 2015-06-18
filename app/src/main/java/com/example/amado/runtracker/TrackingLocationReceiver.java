package com.example.amado.runtracker;

import android.content.Context;
import android.location.Location;
import android.util.Log;

/**
 * Created by Amado on 17/06/2015.
 */
public class TrackingLocationReceiver extends LocationReceiver {
    private static final String TAG = "TrackingLocationReceiver";

    @Override
    protected void onLocationReceived(Context context, Location loc) {
        RunManager.get(context).insertLocation(loc);
        Log.d(TAG, "locationreceived");
    }
}
