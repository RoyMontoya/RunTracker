package com.example.amado.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class RunFragment extends Fragment {
    private static final String ARG_RUN_ID = "RUN_ID";
    private static final String TRACKED ="tracked";
    private static final int LOAD_RUN =0;
    private static final int LOAD_LOCATION = 1;


    private TextView mDurationTextView;
    private TextView mAltitudeTextView;
    private TextView mLongitudeTextView;
    private TextView mLatitudeTextView;
    private TextView mStartedTextView;
    private Button mStartButton;
    private Button mStopButton;
    private Location mLastLocation;
    private Run mRun;
    private RunManager mRunManager;
    private Button mMapButton;


    public static RunFragment newInstance(long runId){
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);

        RunFragment fragment = new RunFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BroadcastReceiver mLocationReceiver = new LocationReceiver(){
        @Override
        protected void onLocationReceived(Context context, Location loc) {
            if(!mRunManager.isTrackingRun(mRun)) return;
            mLastLocation = loc;
            if(isVisible()){
                updateUI();
            }
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }
    };

    public RunFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRunManager = RunManager.get(getActivity());
        Bundle args = getArguments();
        if(args!=null){
            long runId = args.getLong(ARG_RUN_ID, -1);
            if(runId!= -1){
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_RUN, args, new RunLoaderCallbacks());
                lm.initLoader(LOAD_LOCATION, args, new LocationLoaderCallbacks());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_run, container, false);

        mStartedTextView = (TextView)v.findViewById(R.id.run_startedTextView);
        mLatitudeTextView = (TextView)v.findViewById(R.id.run_latitudeTextView);
        mLongitudeTextView = (TextView)v.findViewById(R.id.run_longitudeTextView);
        mAltitudeTextView = (TextView)v.findViewById(R.id.run_altitudeTextView);
        mDurationTextView = (TextView)v.findViewById(R.id.run_durationTextView);

        mStartButton = (Button)v.findViewById(R.id.run_startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRun==null){
                    mRun = mRunManager.startNewRun();
                }else{
                    mRunManager.startTrackingRun(mRun);
                }
                updateUI();
            }
        });

        mStopButton = (Button)v.findViewById(R.id.run_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.stopRun();
                updateUI();
            }
        });

        mMapButton = (Button)v.findViewById(R.id.run_mapButton);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RunMapActivity.class);
                i.putExtra(RunMapActivity.EXTRA_RUN_ID, mRun.getId());
                startActivity(i);
            }
        });

        updateUI();
        return v;
    }

    private void updateUI(){
        boolean started = mRunManager.isTrackingRun();
        boolean trackingThisRun = mRunManager.isTrackingRun(mRun);

        if(mRun!= null ){
            mStartedTextView.setText(mRun.getStartDate().toString());

            int durationSeconds =0;
            if(mRun!= null &&mLastLocation!=null){
                durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
                mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
                mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
                mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
                mMapButton.setEnabled(true);
            }else{
                mMapButton.setEnabled(false);
            }
            mDurationTextView.setText(Run.formatDuration(durationSeconds));
        }


        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started&&trackingThisRun);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    private class RunLoaderCallbacks implements LoaderManager.LoaderCallbacks<Run>{

        @Override
        public Loader<Run> onCreateLoader(int id, Bundle args) {
            return new RunLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Run> loader, Run data) {
            mRun = data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Run> loader) {

        }
    }

    private class LocationLoaderCallbacks implements LoaderManager.LoaderCallbacks<Location>{


        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args) {
            return new LastLocationLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location location) {
            mLastLocation = location;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Location> loader) {

        }
    }


}
