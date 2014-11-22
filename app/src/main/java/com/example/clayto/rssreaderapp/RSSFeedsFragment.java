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
 */
public class RSSFeedsFragment extends ListFragment {

    //public static final String ARG_RSS_CATAGORY = "rss_catagory";
    //public static final String ARG_RSS_TITLES = "rss_titles";
    //public static final String ARG_RSS_PUBLISH_DATES = "rss_publish_dates";
    //public static final String SELECTED_ARRAY_POSITION = "SELECTED_ARRAY_POSITION";
    //public static final String TITLES = "TITLES";
    //public static final String PUBLISH_DATES = "PUBLISH_DATES";
    //public static final String DESCRIPTIONS = "DESCRIPTIONS";
    //public static final String LINKS = "LINKS";
    //public static final String SELECTED_CATEGORY = "SELECTED_CATEGORY";
    //String[] AndroidOS = new String[] { "Cupcake","Donut","Eclair","Froyo","Gingerbread","Honeycomb","Ice Cream SandWich","Jelly Bean","KitKat" };
    //String[] Version = new String[]{"1.5","1.6","2.0-2.1","2.2","2.3","3.0-3.2","4.0","4.1-4.3","4.4"};
    //ArrayList<String> versionName = new ArrayList<String>(Arrays.asList(new String[]{ "Cupcake","Donut","Eclair","Froyo","Gingerbread","Honeycomb","Ice Cream SandWich","Jelly Bean","KitKat" }));
    //ArrayList<String> versionNumber = new ArrayList<String>(Arrays.asList(new String[]{"1.5","1.6","2.0-2.1","2.2","2.3","3.0-3.2","4.0","4.1-4.3","4.4"}));
    private ArrayList<String> titles, publishDates, descriptions, links;
    private String fontSize, themeColor, selectedCategory;

    public RSSFeedsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        //int i = getArguments().getInt(ARG_RSS_CATAGORY);
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

        //String category = getResources().getStringArray(R.array.pref_default_feed_entries)[i];

        //TextView message = (TextView)view.findViewById(R.id.message);
        //String[] categories = new String[] {category, category, category};
//            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable", getActivity().getPackageName());
//            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
//            getActivity().setTitle(planet);
//            return rootView;
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, categories);
        //setListAdapter(adapter);

        CustomAdapter customAdapter = new CustomAdapter(getActivity(),titles,publishDates,fontSize,themeColor);
        setListAdapter(customAdapter);
        return view;
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Intent intent = new Intent(getActivity(),DetailsActivity.class);
        //intent.putExtra(SELECTED_ARRAY_POSITION,position);
        intent.putExtra(getResources().getString(R.string.titles),titles.get(position));
        intent.putExtra(getResources().getString(R.string.publish_dates),publishDates.get(position));
        intent.putExtra(getResources().getString(R.string.descriptions),descriptions.get(position));
        intent.putExtra(getResources().getString(R.string.links),links.get(position));
        intent.putExtra(getResources().getString(R.string.selected_category),selectedCategory);
        intent.putExtra(getResources().getString(R.string.is_new),true);
        startActivity(intent);
//            TextFragment txt = (TextFragment)getFragmentManager().findFragmentById(R.id.fragment2);
//            txt.change(AndroidOS[position],"Version : "+Version[position]);
//            getListView().setSelector(android.R.color.holo_blue_dark);
        //Toast.makeText(getActivity(),"You clicked something",Toast.LENGTH_LONG).show();
    }
}
