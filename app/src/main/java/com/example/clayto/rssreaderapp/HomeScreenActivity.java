package com.example.clayto.rssreaderapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/*
 * Allows user to select what they want to view: saved articles or new articles
 */
public class HomeScreenActivity extends Activity {

    private String themePref, fontSizePref;
    private Button liveFeedsButton, savedFeedsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        liveFeedsButton = (Button)findViewById(R.id.live_feeds_button);
        savedFeedsButton = (Button)findViewById(R.id.saved_feeds_button);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //get the user's preferences and set the background color and font size based on their preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        themePref = sharedPref.getString(getString(R.string.pref_colorScheme_key),"");
        fontSizePref = sharedPref.getString(getString(R.string.pref_fontSize_key),"");

        if (themePref.equals("Dark")) {
            findViewById(R.id.home_screen_layout).setBackgroundColor(Color.BLACK);
            liveFeedsButton.setTextColor(Color.WHITE);
            savedFeedsButton.setTextColor(Color.WHITE);
        } else {
            findViewById(R.id.home_screen_layout).setBackgroundColor(Color.WHITE);
            liveFeedsButton.setTextColor(Color.BLACK);
            savedFeedsButton.setTextColor(Color.BLACK);
        }

        if(fontSizePref.toLowerCase().equals("small")) {
            liveFeedsButton.setTextSize(12);
            savedFeedsButton.setTextSize(12);
        } else if(fontSizePref.toLowerCase().equals("large")) {
            liveFeedsButton.setTextSize(30);
            savedFeedsButton.setTextSize(30);
        } else {
            liveFeedsButton.setTextSize(20);
            savedFeedsButton.setTextSize(20);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle action buttons
        switch(item.getItemId()) {
            //when user clicks on Settings launch the settings activity
            case R.id.action_settings:
                Intent settingsIntent = new Intent(HomeScreenActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //when the user selects live feeds/new articles launch the NewArticlesActivity
    public void viewLiveFeeds(View view) {
        Intent intent = new Intent(HomeScreenActivity.this,NewArticlesActivity.class);
        startActivity(intent);
    }

    //when the user selects saved articles launch the SavedArticlesActivity
    public void viewSavedFeeds(View view) {
        Intent intent = new Intent(HomeScreenActivity.this,SavedArticlesActivity.class);
        startActivity(intent);
    }
}
