package com.example.amado.runtracker;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by Amado on 19/06/2015.
 */
public class LocationListCursorLoader extends SQLiteCursorLoader {

    private long mRunId;

    public LocationListCursorLoader(Context c, long runId){
        super(c);
        mRunId = runId;
    }

    @Override
    protected Cursor loadCursor() {
        return RunManager.get(getContext()).queryLocationsForRun(mRunId);
    }
}
