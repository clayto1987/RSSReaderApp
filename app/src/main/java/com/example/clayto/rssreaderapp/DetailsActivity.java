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

            Intent intent = getIntent();
            //int arrayListPosition = intent.getIntExtra(RSSFeedsFragment.SELECTED_ARRAY_POSITION,0);
            //title = intent.getStringArrayListExtra(RSSFeedsFragment.TITLES_ARRAYLIST).get(arrayListPosition);
            //publishDate = intent.getStringArrayListExtra(RSSFeedsFragment.PUBLISH_DATES_ARRAYLIST).get(arrayListPosition);
            //description = intent.getStringArrayListExtra(RSSFeedsFragment.DESCRIPTIONS_ARRAYLIST).get(arrayListPosition);
            //link = intent.getStringArrayListExtra(RSSFeedsFragment.LINKS_ARRAYLIST).get(arrayListPosition);
            title = intent.getStringExtra(getResources().getString(R.string.titles));
            publishDate = intent.getStringExtra(getResources().getString(R.string.publish_dates));
            description = intent.getStringExtra(getResources().getString(R.string.descriptions));
            link = intent.getStringExtra(getResources().getString(R.string.links));
            isNewArticle = intent.getBooleanExtra(getResources().getString(R.string.is_new),false);

            if(isNewArticle) {
                selectedCategory = intent.getStringExtra(getResources().getString(R.string.selected_category));
            } else {
                author = intent.getStringExtra(getResources().getString(R.string.author));
            }


            String htmlLink = "<a href=\"" + link + "\"> Read More</a>";

            titleTextView = (TextView)findViewById(R.id.title);
            authorTextView = (TextView)findViewById(R.id.author);
            dateTextView = (TextView)findViewById(R.id.publish_date);
            descriptionTextView = (TextView)findViewById(R.id.description);
            linkTextView = (TextView)findViewById(R.id.link);

            titleTextView.setText(Html.fromHtml(title));
            dateTextView.setText(Html.fromHtml(publishDate));
            //descriptionTextView.setText(Html.fromHtml(description));
            linkTextView.setText(Html.fromHtml(htmlLink));
            linkTextView.setMovementMethod(LinkMovementMethod.getInstance());

            if(!isNewArticle) {
                authorTextView.setText(author);
                descriptionTextView.setText(description);
            }

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
        menu.findItem(R.id.action_download_article).setEnabled(isNewArticle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_go_home:
                finish();
                Intent goHomeIntent = new Intent(DetailsActivity.this,HomeScreenActivity.class);
                startActivity(goHomeIntent);
                return true;
            case R.id.action_download_article:
                saveArticleToDatabase();
//                Intent parseArticleIntent = new Intent(DetailsActivity.this,SavedArticlesActivity.class);
//                parseArticleIntent.putExtra(getResources().getString(R.string.article_title),title);
//                parseArticleIntent.putExtra(getResources().getString(R.string.article_publish_date),publishDate);
//                parseArticleIntent.putExtra(getResources().getString(R.string.article_url_link),link);
//                startActivity(parseArticleIntent);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(DetailsActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void saveArticleToDatabase() {

        try {

            DatabaseHelper db = new DatabaseHelper(this);

            if(!db.articleWithTitleExists(title)) {

                Category category = db.getCategoryByName(selectedCategory);

                if(category != null && category.getId() > 0) {

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

    class ParseHTML extends AsyncTask<Void, Void, Void> {

        //String author;
        //String content;

        @Override
        protected void onPreExecute() {
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
                // Get the html document title
                Element htmlBody = document.body();
                Elements mainContent = htmlBody.select("#main_column");

                //Elements titleContent = mainContent.select(".top_head");
                Elements authorContent = mainContent.select(".first_byline");
                Elements articleContent = mainContent.select(".article > p");

                //title = titleContent.get(0).text();
                if(articleContent != null) {
                    description = articleContent.text();
                } else {
                    description = "";
                }

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
            // Set title into TextView
            authorTextView.setText(author);
            descriptionTextView.setText(description);
            mProgressDialog.dismiss();
        }
    }
}
