package com.example.clayto.rssreaderapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class FullArticleActivity extends Activity {

    // URL Address
//    String urlOne = "http://www.winnipegfreepress.com/sports/hockey/jets/jets-bring-points-back-from-a-tiring-road-swing-and-face-devils-tuesday-282967141.html";
//    String urlTwo = "http://www.winnipegfreepress.com/sports/hockey/jets/jets-finish-road-swing-in-battle-mode-282902351.html";
//    String urlThree = "http://www.winnipegfreepress.com/sports/hockey/jets/jets-within-whisker-of-win-282902311.html";
//    String urlFour = "http://www.winnipegfreepress.com/sports/hockey/jets/Wild-save-face-with-OT-win-over-Jets-282884101.html";
//    String urlFive = "http://www.winnipegfreepress.com/sports/hockey/jets/Giveaway-gaffe-part-of-the-game-282844931.html";

    private ProgressDialog mProgressDialog;
    private TextView articleTitle, articlePublishDate, articleAuthor, articleDescription;
    private String title, date, link, fontSizePref, themePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_article);

        Intent intent = getIntent();
        title = intent.getStringExtra(getResources().getString(R.string.article_title));
        date = intent.getStringExtra(getResources().getString(R.string.article_publish_date));
        link = intent.getStringExtra(getResources().getString(R.string.article_url_link));

        // Locate the Buttons in activity_main.xml
        articleTitle = (TextView)findViewById(R.id.fullarticle_title);
        articlePublishDate = (TextView)findViewById(R.id.fullarticle_publish_date);
        articleAuthor = (TextView)findViewById(R.id.fullarticle_author_name);
        articleDescription = (TextView)findViewById(R.id.fullarticle_description);

        articleTitle.setText(title);
        articlePublishDate.setText(date);

    }

    @Override
    protected void onStart() {

        super.onStart();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        themePref = sharedPref.getString(getString(R.string.pref_colorScheme_key),"");
        fontSizePref = sharedPref.getString(getString(R.string.pref_fontSize_key),"");

        if (themePref.equals("Dark")) {
            findViewById(R.id.full_article_activity_layout).setBackgroundColor(Color.BLACK);
            articleTitle.setTextColor(Color.WHITE);
            articlePublishDate.setTextColor(Color.WHITE);
            articleDescription.setTextColor(Color.WHITE);
            articleAuthor.setTextColor(Color.WHITE);
        } else {
            findViewById(R.id.details_activity_layout).setBackgroundColor(Color.WHITE);
            articleTitle.setTextColor(Color.BLACK);
            articlePublishDate.setTextColor(Color.BLACK);
            articleDescription.setTextColor(Color.BLACK);
            articleAuthor.setTextColor(Color.BLACK);
        }

        if(fontSizePref.toLowerCase().equals("small")) {
            articleTitle.setTextSize(10);
            articlePublishDate.setTextSize(8);
            articleDescription.setTextSize(8);
            articleAuthor.setTextSize(8);
        } else if(fontSizePref.toLowerCase().equals("large")) {
            articleTitle.setTextSize(27);
            articlePublishDate.setTextSize(24);
            articleDescription.setTextSize(24);
            articleAuthor.setTextSize(24);
        } else {
            articleTitle.setTextSize(18);
            articlePublishDate.setTextSize(16);
            articleDescription.setTextSize(16);
            articleAuthor.setTextSize(16);
        }

        new Content().execute();
    }

    // Title AsyncTask
    private class Content extends AsyncTask<Void, Void, Void> {

        String author;
        String content;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(FullArticleActivity.this);
            mProgressDialog.setTitle("Android Basic JSoup Tutorial");
            mProgressDialog.setMessage("Loading...");
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

//                Elements titleContent = mainContent.select(".top_head");
                Elements authorContent = mainContent.select(".first_byline");
                Elements articleContent = mainContent.select(".article > p");

                //title = titleContent.get(0).text();
                content = articleContent.text();
                author = authorContent.get(0).text();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            articleAuthor.setText(this.author);
            articleDescription.setText(this.content);
            mProgressDialog.dismiss();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_view_this_article_offline:
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
