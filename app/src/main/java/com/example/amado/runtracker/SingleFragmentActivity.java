package com.example.amado.runtracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Amado on 17/06/2015.
 */
public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract android.support.v4.app.Fragment createFragment();

    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}
