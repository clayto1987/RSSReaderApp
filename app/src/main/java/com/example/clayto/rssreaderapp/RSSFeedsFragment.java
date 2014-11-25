package com.example.clayto.rssreaderapp;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Clayto on 14-11-15.
 * Displays the titles and dates for all new articles from a specific RSS category.
 */
public class RSSFeedsFragment extends ListFragment {

    private ArrayList<String> titles, publishDates, descriptions, links;
    private String fontSize, themeColor, selectedCategory;

    public RSSFeedsFragment() {

    }

    /*
     * Retrieves all passed in arguments to set the theme and add all articles to the list view using a CustomAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment, container, false);
        titles = getArguments().getStringArrayList(getResources().getString(R.string.titles));
        publishDates = getArguments().getStringArrayList(getResources().getString(R.string.publish_dates));
        descriptions = getArguments().getStringArrayList(getResources().getString(R.string.descriptions));
        links = getArguments().getStringArrayList(getResources().getString(R.string.links));
        selectedCategory = getArguments().getString(getResources().getString(R.string.selected_category));

        fontSize = getArguments().getString(getString(R.string.pref_fontSize_key));
        themeColor = getArguments().getString(getString(R.string.pref_colorScheme_key));

        if (themeColor.equals("Dark")) {
            view.setBackgroundColor(Color.BLACK);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

        CustomAdapter customAdapter = new CustomAdapter(getActivity(),titles,publishDates,fontSize,themeColor);
        setListAdapter(customAdapter);
        return view;
    }

    /*
     * Handles the user clicking on an article upon which it passes all relevant data for that
     * article to the DetailsActivity for the user to read the entire article or save it for later.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Intent intent = new Intent(getActivity(),DetailsActivity.class);
        intent.putExtra(getResources().getString(R.string.titles),titles.get(position));
        intent.putExtra(getResources().getString(R.string.publish_dates),publishDates.get(position));
        intent.putExtra(getResources().getString(R.string.descriptions),descriptions.get(position));
        intent.putExtra(getResources().getString(R.string.links),links.get(position));
        intent.putExtra(getResources().getString(R.string.selected_category),selectedCategory);
        intent.putExtra(getResources().getString(R.string.is_new),true);
        startActivity(intent);

    }
}
