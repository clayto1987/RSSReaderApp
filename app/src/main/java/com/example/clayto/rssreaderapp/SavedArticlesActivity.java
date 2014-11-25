package com.example.clayto.rssreaderapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.clayto.sqlite.Article;
import com.example.clayto.sqlite.DatabaseHelper;

import java.util.ArrayList;

/*
 * Provides a list view to display all saved articles to the user and allowing them to select one to view the entire article
 */
public class SavedArticlesActivity extends Activity {

    private String fontSizePref, themePref;
    private ListView listView;
    private DatabaseHelper db;
    private ArrayList<String> titles, publishDates, descriptions, links, authors;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        listView = (ListView)findViewById(R.id.list_view);

        //instantiates DatabaseHelper to connect to the database
        db = new DatabaseHelper(this);

        EventHandler eventHandler = new EventHandler();
        listView.setOnItemClickListener(eventHandler);

    }

    @Override
    protected void onStart() {

        super.onStart();

        //get the users shared preferences to set the theme to their preference and starts to load the articles from the database
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        themePref = sharedPref.getString(getString(R.string.pref_colorScheme_key),"");
        fontSizePref = sharedPref.getString(getString(R.string.pref_fontSize_key), "");

        if (themePref.equals("Dark")) {
            findViewById(R.id.saved_articles_layout).setBackgroundColor(Color.BLACK);
        } else {
            findViewById(R.id.saved_articles_layout).setBackgroundColor(Color.WHITE);
        }

        loadData();

    }

    /*
     * Loads all articles from the database and creates the custom list adapter to display the articles in a listview
     */
    private void loadData() {

        try {

            //get all articles from the database
            ArrayList<Article> allArticles = db.getAllArticles();

            //if some articles exist
            if(allArticles.size() > 0) {

                titles = new ArrayList<String>();
                publishDates = new ArrayList<String>();
                descriptions = new ArrayList<String>();
                links = new ArrayList<String>();
                authors = new ArrayList<String>();

                for(Article singleArticle : allArticles) {
                    titles.add(singleArticle.getTitle());
                    publishDates.add(singleArticle.getPublishDate());
                    authors.add(singleArticle.getAuthor());
                    descriptions.add(singleArticle.getDescription());
                    links.add(singleArticle.getUrlLink());
                }

                CustomAdapter adapter = new CustomAdapter(SavedArticlesActivity.this,titles,publishDates,fontSizePref, themePref);
                listView.setAdapter(adapter);

            }

        } catch (Exception e) {
            Log.e("SavedArticlesActivity.loadData", "Couldn't load articles from database");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_articles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            //when the user clicks the home button close this activity (returning user to home screen)
            case R.id.action_go_home:
                finish();
                return true;
            //when the user clicks the settings button launch the settings activity
            case R.id.action_settings:
                Intent settingsIntent = new Intent(SavedArticlesActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /*
     * Class that handles a user clicking on an article title in the list view
     * upon which it passes all relevant data for that article to the DetailsActivity
     * for the user to view the whole article.
     */
    class EventHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //String item = (String) parent.getItemAtPosition(position);
            Intent intent = new Intent(SavedArticlesActivity.this,DetailsActivity.class);
            //intent.putExtra(SELECTED_ARRAY_POSITION,position);
            intent.putExtra(getResources().getString(R.string.titles),titles.get(position));
            intent.putExtra(getResources().getString(R.string.publish_dates),publishDates.get(position));
            intent.putExtra(getResources().getString(R.string.author),authors.get(position));
            intent.putExtra(getResources().getString(R.string.descriptions),descriptions.get(position));
            intent.putExtra(getResources().getString(R.string.links),links.get(position));
            intent.putExtra(getResources().getString(R.string.is_new),false);
            startActivity(intent);
        }
    }
}
