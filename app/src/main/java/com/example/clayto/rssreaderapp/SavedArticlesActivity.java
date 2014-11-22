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

        db = new DatabaseHelper(this);

        EventHandler eventHandler = new EventHandler();
        listView.setOnItemClickListener(eventHandler);

    }

    @Override
    protected void onStart() {

        super.onStart();

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

    private void loadData() {

        try {

            ArrayList<Article> allArticles = db.getAllArticles();

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
            case R.id.action_settings:
                Intent settingsIntent = new Intent(SavedArticlesActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    class EventHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //String item = (String) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
            intent.putExtra(SELECTED_ARRAY_POSITION,position);
            intent.putExtra(TITLES_ARRAYLIST,names);
            intent.putExtra(PUBLISH_DATES_ARRAYLIST,dates);
            intent.putExtra(DESCRIPTIONS_ARRAYLIST,descriptions);
            intent.putExtra(LINKS_ARRAYLIST,links);
            startActivity(intent);
        }
    }
}
