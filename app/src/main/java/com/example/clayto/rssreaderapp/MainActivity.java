package com.example.clayto.rssreaderapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mRSSCatagories;

    private String urlFeed, fontSizePref, themePref, sortingPref;
    private String defaultFeedPref = "";
    private Set<String> subscribedFeedsPref;
    private int maxPostsPref = 10;
    public URL xml_file;
    public BufferedReader in;
    private ArrayList<String> names, dates, descriptions, links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mRSSCatagories = getResources().getStringArray(R.array.pref_default_feed_entries);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mRSSCatagories));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

//        if (savedInstanceState == null) {
//            selectItem(0);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        themePref = sharedPref.getString(getString(R.string.pref_colorScheme_key),"");
        fontSizePref = sharedPref.getString(getString(R.string.pref_fontSize_key),"");

        if(defaultFeedPref.equals("")) {
            defaultFeedPref = sharedPref.getString(getString(R.string.pref_defaultFeed_key),"");
        }

        maxPostsPref = sharedPref.getInt(getString(R.string.pref_maxPosts_key), 10);
        sortingPref = sharedPref.getString(getString(R.string.pref_sorting_key),"");
        subscribedFeedsPref = sharedPref.getStringSet(getString(R.string.pref_subscribedFeeds_key),null);

        /*if (themePref.equals("Dark")) {
            findViewById(R.id.drawer_layout).setBackgroundColor(Color.BLACK);
        } else {
            findViewById(R.id.drawer_layout).setBackgroundColor(Color.WHITE);
        }*/

        if(!defaultFeedPref.equals("")) {
            getRSSFeed(defaultFeedPref);
        } else {
            getRSSFeed(getResources().getStringArray(R.array.pref_default_feed_entries_values)[0]);
        }

    }

    private void getRSSFeed(String feedName){

        //message.setVisibility(View.VISIBLE);
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            //message.setText(R.string.please_wait);
            urlFeed = feedName;
            RSSFeeder feedme = new RSSFeeder();
            feedme.execute();
        } else {
            //message.setText(R.string.no_internet);
            Toast.makeText(MainActivity.this,"Please verify you are connected to the internet.",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_refresh:
                getRSSFeed(urlFeed);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //String category = getResources().getStringArray(R.array.pref_default_feed_entries)[position];
            String categoryURL = getResources().getStringArray(R.array.pref_default_feed_entries_values)[position];
            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString(getString(R.string.pref_defaultFeed_key),categoryURL).commit();
            getRSSFeed(categoryURL);
            mDrawerList.setItemChecked(position, true);
            setTitle(mRSSCatagories[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
            //selectItem(position);
        }
    }

    //private void selectItem(int position) {
//    private void selectItem() {
//        // update the main content by replacing fragments
////        Fragment fragment = new PlanetFragment();
////        Fragment fragment = new MenuFragment();
//        Fragment fragment = new RSSFeedsFragment();
//        Bundle args = new Bundle();
//        args.putStringArrayList(RSSFeedsFragment.ARG_RSS_TITLES, names);
//        args.putStringArrayList(RSSFeedsFragment.ARG_RSS_PUBLISH_DATES,dates);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//        // update selected item and title, then close the drawer
////        mDrawerList.setItemChecked(position, true);
////        setTitle(mRSSCatagories[position]);
////        mDrawerLayout.closeDrawer(mDrawerList);
//    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    class RSSFeeder extends AsyncTask<Void, Void, Void> {

        SAXHandler handler;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                xml_file = new URL(urlFeed);
                in = new BufferedReader(new InputStreamReader(xml_file.openStream()));

                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                handler = new SAXHandler();

                sp.parse(new InputSource(in), handler);

                //Thread.sleep(10000);
            } catch (MalformedURLException ex) {
                Log.e("Test", ex.getMessage());
            } catch (IOException ex) {
                Log.e("Test", ex.getMessage());
            } catch (SAXException ex) {
                Log.e("Test", ex.getMessage());
            } catch (ParserConfigurationException ex) {
                Log.e("Test", ex.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            names = handler.getTitles();
            dates = handler.getPublishDates();
            descriptions = handler.getDescriptions();
            links = handler.getLinks();

            ArrayList<String> namesLimitedSet = new ArrayList<String>();
            ArrayList<String> datesLimitedSet = new ArrayList<String>();
            ArrayList<String> descriptionsLimitedSet = new ArrayList<String>();
            ArrayList<String> linksLimitedSet = new ArrayList<String>();

            if (maxPostsPref <= names.size() ) {

                for(int i = 0; i < maxPostsPref; i++){
                    namesLimitedSet.add(names.get(i));
                    datesLimitedSet.add(dates.get(i));
                    descriptionsLimitedSet.add(descriptions.get(i));
                    linksLimitedSet.add(links.get(i));
                }

            } else {
                namesLimitedSet = names;
                datesLimitedSet = dates;
                descriptionsLimitedSet = descriptions;
                linksLimitedSet = links;
            }

            Fragment fragment = new RSSFeedsFragment();
            Bundle args = new Bundle();
            args.putStringArrayList(RSSFeedsFragment.TITLES_ARRAYLIST, namesLimitedSet);
            args.putStringArrayList(RSSFeedsFragment.PUBLISH_DATES_ARRAYLIST,datesLimitedSet);
            args.putStringArrayList(RSSFeedsFragment.DESCRIPTIONS_ARRAYLIST, descriptionsLimitedSet);
            args.putStringArrayList(RSSFeedsFragment.LINKS_ARRAYLIST,linksLimitedSet);

            args.putString(getString(R.string.pref_fontSize_key),fontSizePref);
            args.putString(getString(R.string.pref_colorScheme_key),themePref);

            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            //CustomAdapter adapter = new CustomAdapter(MainActivity.this,namesLimitedSet,datesLimitedSet,fontSizePref, themePref);
            //setListAdapter(adapter);
            //message.setVisibility(View.GONE);
            //listView.setAdapter(adapter);
        }
    }

}