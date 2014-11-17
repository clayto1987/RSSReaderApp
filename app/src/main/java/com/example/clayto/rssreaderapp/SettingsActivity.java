package com.example.clayto.rssreaderapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

/**
 * Created by Clayto on 14-11-15.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getString(getString(R.string.pref_colorScheme_key),"").equals("Dark")) {
            this.setTheme(android.R.style.Theme_Holo);
        } else {
            this.setTheme(android.R.style.Theme_Holo_Light);
        }

        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e){

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

