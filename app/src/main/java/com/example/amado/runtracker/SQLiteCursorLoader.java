package com.example.amado.runtracker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by Amado on 18/06/2015.
 */
public abstract class SQLiteCursorLoader extends android.support.v4.content.AsyncTaskLoader<Cursor> {
    private Cursor mCursor;

    public SQLiteCursorLoader(Context context){
        super(context);
    }

    protected abstract Cursor loadCursor();


    @Override
    public Cursor loadInBackground() {
        Cursor cursor = loadCursor();
        if(cursor!=null){
            cursor.getCount();
        }
        return cursor;
    }

    @Override
    public void deliverResult(Cursor data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if(mCursor!=null){
            deliverResult(mCursor);
        }
        if(takeContentChanged() || mCursor == null){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor data) {
        if(data !=null&&!loadCursor().isClosed()){
            data.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if(mCursor!= null &&!mCursor.isClosed()){
            mCursor.close();
        }
        mCursor=null;
    }
}
