package com.example.amado.runtracker;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */
public class RunMapFragment extends SupportMapFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOAD_LOCATION= 0;
    private static final String ARG_RUN_ID = "RUN_ID";
    private RunDatabaseHelper.LocationCursor mLocationCursor;

    private GoogleMap mGoogleMap;

    public static RunMapFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment rf = new RunMapFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args !=null){
            long runId = args.getLong(ARG_RUN_ID, -1);
            if(runId!= -1){
                android.support.v4.app.LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_LOCATION, args, this);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        mGoogleMap = getMap();
        mGoogleMap.setMyLocationEnabled(true);

        return v;
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long runId = args.getLong(ARG_RUN_ID, -1);
        return new LocationListCursorLoader(getActivity(), runId);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mLocationCursor = (RunDatabaseHelper.LocationCursor)data;
        updateUI();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
     mLocationCursor.close();
        mLocationCursor=null;
    }


    private void updateUI(){
        if(mGoogleMap == null||mLocationCursor == null)return;

        PolylineOptions line =new PolylineOptions();
        LatLngBounds.Builder latLangBuilder = new LatLngBounds.Builder();
        mLocationCursor.moveToFirst();
        while (!mLocationCursor.isAfterLast()){
            Location loc = mLocationCursor.getLocation();
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            Resources r = getResources();

            if(mLocationCursor.isFirst()){
                String startDate = new Date(loc.getTime()).toString();
                MarkerOptions startMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(r.getString(R.string.run_start))
                        .snippet(r.getString(R.string.run_started_at_formar, startDate));
                mGoogleMap.addMarker(startMarkerOptions);
            }else if(mLocationCursor.isLast()){
                String endDate= new Date(loc.getTime()).toString();
                MarkerOptions finishMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(r.getString(R.string.run_finish))
                        .snippet(r.getString(R.string.run_finished_at_format, endDate));
                mGoogleMap.addMarker(finishMarkerOptions);
            }


            line.add(latLng);
            latLangBuilder.include(latLng);
            mLocationCursor.moveToNext();
        }
        mGoogleMap.addPolyline(line);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        LatLngBounds latLngBounds = latLangBuilder.build();
        CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(latLngBounds, display.getWidth(),
                display.getHeight(), 15);
        mGoogleMap.moveCamera(movement);




    }
}
