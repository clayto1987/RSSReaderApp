package com.example.clayto.rssreaderapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clayto.sqlite.Article;
import com.example.clayto.sqlite.Category;
import com.example.clayto.sqlite.DatabaseHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/*
 * Activity that displays all information about a specific article
 */
public class DetailsActivity extends Activity {

    protected String title, author, publishDate, description, link, fontSizePref, themePref, selectedCategory;
    private TextView titleTextView, authorTextView, dateTextView, descriptionTextView, linkTextView;
    private ProgressDialog mProgressDialog;
    boolean isNewArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        try {

            //get the title, date, description, link for the user selected article from the intent (coming from either NewArticlesActivity or SavedArticlesActivity
            Intent intent = getIntent();
            title = intent.getStringExtra(getResources().getString(R.string.titles));
            publishDate = intent.getStringExtra(getResources().getString(R.string.publish_dates));
            description = intent.getStringExtra(getResources().getString(R.string.descriptions));
            link = intent.getStringExtra(getResources().getString(R.string.links));
            isNewArticle = intent.getBooleanExtra(getResources().getString(R.string.is_new),false);

            //If the article is new, get the category of the article or if it is an article from the database get the author
            if(isNewArticle) {
                selectedCategory = intent.getStringExtra(getResources().getString(R.string.selected_category));
            } else {
                author = intent.getStringExtra(getResources().getString(R.string.author));
            }


            //display all article information in the view
            String htmlLink = "<a href=\"" + link + "\"> Read More</a>";

            titleTextView = (TextView)findViewById(R.id.title);
            authorTextView = (TextView)findViewById(R.id.author);
            dateTextView = (TextView)findViewById(R.id.publish_date);
            descriptionTextView = (TextView)findViewById(R.id.description);
            linkTextView = (TextView)findViewById(R.id.link);

            titleTextView.setText(Html.fromHtml(title));
            dateTextView.setText(Html.fromHtml(publishDate));
            linkTextView.setText(Html.fromHtml(htmlLink));
            linkTextView.setMovementMethod(LinkMovementMethod.getInstance());

            //if the article is being retrieved from the database set the author and description
            //if it isn't saved the author and description will be obtained by parsing through the html
            if(!isNewArticle) {
                authorTextView.setText(author);
                descriptionTextView.setText(description);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Details failed to load.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //get shared preferences saved from settings activity for user font and color preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        themePref = sharedPref.getString(getString(R.string.pref_colorScheme_key),"");
        fontSizePref = sharedPref.getString(getString(R.string.pref_fontSize_key),"");

        //set the background color and font size based on user preferences
        if (themePref.equals("Dark")) {
            findViewById(R.id.details_activity_layout).setBackgroundColor(Color.BLACK);
            titleTextView.setTextColor(Color.WHITE);
            authorTextView.setTextColor(Color.WHITE);
            dateTextView.setTextColor(Color.WHITE);
            descriptionTextView.setTextColor(Color.WHITE);
            linkTextView.setTextColor(Color.WHITE);
        } else {
            findViewById(R.id.details_activity_layout).setBackgroundColor(Color.WHITE);
            titleTextView.setTextColor(Color.BLACK);
            authorTextView.setTextColor(Color.BLACK);
            dateTextView.setTextColor(Color.BLACK);
            descriptionTextView.setTextColor(Color.BLACK);
            linkTextView.setTextColor(Color.BLACK);
        }

        if(fontSizePref.toLowerCase().equals("small")) {
            titleTextView.setTextSize(10);
            authorTextView.setTextSize(8);
            dateTextView.setTextSize(8);
            descriptionTextView.setTextSize(8);
            linkTextView.setTextSize(8);
        } else if(fontSizePref.toLowerCase().equals("large")) {
            titleTextView.setTextSize(27);
            authorTextView.setTextSize(24);
            dateTextView.setTextSize(24);
            descriptionTextView.setTextSize(24);
            linkTextView.setTextSize(24);
        } else {
            titleTextView.setTextSize(18);
            authorTextView.setTextSize(16);
            dateTextView.setTextSize(16);
            descriptionTextView.setTextSize(16);
            linkTextView.setTextSize(16);
        }

        //if the article is new (not saved yet) parse through the html found at the article URL
        //to get the article description and author
        if(isNewArticle) {

            try {
                ParseHTML htmlParser = new ParseHTML();
                htmlParser.execute();
            } catch (Exception e) {
                Log.e("DetailsActivity.onStart","Can't parse HTML");
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        //if the article is new (not saved) enable the download button, otherwise disable the button
        menu.findItem(R.id.action_download_article).setEnabled(isNewArticle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            //when the user clicks the home button, close the activity and start the home screen activity
            case R.id.action_go_home:
                finish();
                Intent goHomeIntent = new Intent(DetailsActivity.this,HomeScreenActivity.class);
                startActivity(goHomeIntent);
                return true;
            //when the user clicks the download button save the article to the sqlite database
            case R.id.action_download_article:
                saveArticleToDatabase();
                return true;
            //when the user clicks the settings button start the settings activity
            case R.id.action_settings:
                Intent settingsIntent = new Intent(DetailsActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            //if the user clicks the home button finish the activity
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /*
     * Saves the article to the sqlite database
     */
    private void saveArticleToDatabase() {

        try {

            //instatiate database helper to connect to the database and create tables if necessary
            DatabaseHelper db = new DatabaseHelper(this);

            //if the article hasn't been saved before, continue.
            if(!db.articleWithTitleExists(title)) {

                //get the category object from the database that this article belongs to
                Category category = db.getCategoryByName(selectedCategory);

                //if category was retrieved successfully
                if(category != null && category.getId() > 0) {

                    //create an article object and save it to the database
                    Article article = new Article(title,publishDate,author,description,link,category.getId());
                    Log.d("Article",article.toString());
                    long articleID = db.createArticle(article);
                    Log.d("ID of Article saved", Long.toString(articleID));
                    Log.d("Article Count", "Article count: " + db.getArticleCount());
                    Toast.makeText(DetailsActivity.this,"Article successfully saved to the database",Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(DetailsActivity.this,"Article has already been saved to the database",Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e("DetailsActivity.saveArticleToDatabase","Couldn't save article to database");
        }

    }

    /*
     * AsyncTask that runs on another thread to connect to the article's URL and gets the contents of the article from the HTML page
     */
    class ParseHTML extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            //start a progress dialog to show the user they are waiting on something
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(DetailsActivity.this);
            mProgressDialog.setTitle("RSS Reader");
            mProgressDialog.setMessage("Gathering Article...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(link).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36").get();
                // Get the html document body and article content
                Element htmlBody = document.body();
                Elements mainContent = htmlBody.select("#main_column");

                //Get the elements that contain the author and article content
                Elements authorContent = mainContent.select(".first_byline");
                Elements articleContent = mainContent.select(".article > p");

                //if article content was retrieved successfully set the description
                if(articleContent != null) {
                    description = articleContent.text();
                } else {
                    description = "";
                }

                //if author was retrieved successfully set the author
                if(authorContent != null && authorContent.size() > 0) {
                    author = authorContent.get(0).text();
                } else {
                    author = "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.e("DetailsActivity doInBackground", "Error in doInBackground method parsing html");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView and close progress dialog
            authorTextView.setText(author);
            descriptionTextView.setText(description);
            mProgressDialog.dismiss();
        }
    }
}
