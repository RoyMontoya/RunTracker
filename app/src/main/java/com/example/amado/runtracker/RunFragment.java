package com.example.amado.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

    private BroadcastReceiver mLocationReceiver = new LocationReceiver(){
        @Override
        protected void onLocationReceived(Context context, Location loc) {
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
                mRun = mRunManager.startNewRun();
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

        updateUI();
        return v;
    }

    private void updateUI(){
        boolean started = mRunManager.isTrackingRun();

        if(mRun!= null ){
            mStartedTextView.setText(mRun.getStartDate().toString());

            int durationSeconds =0;
            if(mRun!= null &&mLastLocation!=null){
                durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
                mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
                mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
                mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
            }
            mDurationTextView.setText(Run.formatDuration(durationSeconds));
        }


        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started);
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


}
