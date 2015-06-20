package com.example.amado.runtracker;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class RunMapActivity extends SingleFragmentActivity {
    public static final String EXTRA_RUN_ID ="com.example.amado.runtracker.run_id";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if(runId != -1){
            return RunMapFragment.newInstance(runId);
        }else{
            return new RunMapFragment();
        }
    }
}
