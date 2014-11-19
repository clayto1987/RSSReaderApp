package com.example.clayto.rssreaderapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {

    private String title, publishDate, description, link, fontSizePref, themePref;
    private TextView titleTextView, dateTextView, descriptionTextView, linkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        try {

            Intent intent = getIntent();
            int arrayListPosition = intent.getIntExtra(RSSFeedsFragment.SELECTED_ARRAY_POSITION,0);
            title = intent.getStringArrayListExtra(RSSFeedsFragment.TITLES_ARRAYLIST).get(arrayListPosition);
            publishDate = intent.getStringArrayListExtra(RSSFeedsFragment.PUBLISH_DATES_ARRAYLIST).get(arrayListPosition);
            description = intent.getStringArrayListExtra(RSSFeedsFragment.DESCRIPTIONS_ARRAYLIST).get(arrayListPosition);
            link = intent.getStringArrayListExtra(RSSFeedsFragment.LINKS_ARRAYLIST).get(arrayListPosition);

            String htmlLink = "<a href=\"" + link + "\"> Read More</a>";

            titleTextView = (TextView)findViewById(R.id.title);
            dateTextView = (TextView)findViewById(R.id.publish_date);
            descriptionTextView = (TextView)findViewById(R.id.description);
            linkTextView = (TextView)findViewById(R.id.link);

            titleTextView.setText(Html.fromHtml(title));
            dateTextView.setText(Html.fromHtml(publishDate));
            descriptionTextView.setText(Html.fromHtml(description));
            linkTextView.setText(Html.fromHtml(htmlLink));
            linkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        } catch (Exception e) {
            Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        themePref = sharedPref.getString(getString(R.string.pref_colorScheme_key),"");
        fontSizePref = sharedPref.getString(getString(R.string.pref_fontSize_key),"");

        if (themePref.equals("Dark")) {
            findViewById(R.id.details_activity_layout).setBackgroundColor(Color.BLACK);
            titleTextView.setTextColor(Color.WHITE);
            dateTextView.setTextColor(Color.WHITE);
            descriptionTextView.setTextColor(Color.WHITE);
            linkTextView.setTextColor(Color.WHITE);
        } else {
            findViewById(R.id.details_activity_layout).setBackgroundColor(Color.WHITE);
            titleTextView.setTextColor(Color.BLACK);
            dateTextView.setTextColor(Color.BLACK);
            descriptionTextView.setTextColor(Color.BLACK);
            linkTextView.setTextColor(Color.BLACK);
        }

        if(fontSizePref.toLowerCase().equals("small")) {
            titleTextView.setTextSize(10);
            dateTextView.setTextSize(8);
            descriptionTextView.setTextSize(8);
            linkTextView.setTextSize(8);
        } else if(fontSizePref.toLowerCase().equals("large")) {
            titleTextView.setTextSize(27);
            dateTextView.setTextSize(24);
            descriptionTextView.setTextSize(24);
            linkTextView.setTextSize(24);
        } else {
            titleTextView.setTextSize(18);
            dateTextView.setTextSize(16);
            descriptionTextView.setTextSize(16);
            linkTextView.setTextSize(16);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_view_article_offline:
                return true;
            case R.id.action_download_article:
                Intent parseArticleIntent = new Intent(DetailsActivity.this,FullArticleActivity.class);
                parseArticleIntent.putExtra(getResources().getString(R.string.article_title),title);
                parseArticleIntent.putExtra(getResources().getString(R.string.article_publish_date),publishDate);
                parseArticleIntent.putExtra(getResources().getString(R.string.article_url_link),link);
                startActivity(parseArticleIntent);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(DetailsActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
