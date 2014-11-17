package com.example.clayto.rssreaderapp;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Clayto on 2014-10-07.
 */
public class CustomAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> titles, publishDates;
    private final String fontSizePreference, themePreference;

    static class ViewHolder {
        public TextView title;
        public TextView publishDate;
    }

    public CustomAdapter(Activity context, ArrayList<String> titles, ArrayList<String> publishDates, String fontSizePref, String themePref) {
        super(context, R.layout.list_item, titles);
        this.context = context;
        this.titles = titles;
        this.publishDates = publishDates;
        this.fontSizePreference = fontSizePref;
        this.themePreference = themePref;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) rowView.findViewById(R.id.title);
            viewHolder.publishDate = (TextView) rowView.findViewById(R.id.publish_date);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String t = titles.get(position);
        holder.title.setText(t);
        String pd = "Posted on: " + publishDates.get(position);
        holder.publishDate.setText(pd);

        if(fontSizePreference.toLowerCase().equals("small")) {
            holder.title.setTextSize(10);
            holder.publishDate.setTextSize(8);
        } else if(fontSizePreference.toLowerCase().equals("large")) {
            holder.title.setTextSize(24);
            holder.publishDate.setTextSize(18);
        } else {
            holder.title.setTextSize(16);
            holder.publishDate.setTextSize(12);
        }

        if (themePreference.equals("Dark")) {
            holder.title.setTextColor(Color.WHITE);
            holder.publishDate.setTextColor(Color.WHITE);
        } else {
            holder.title.setTextColor(Color.BLACK);
            holder.publishDate.setTextColor(Color.BLACK);
        }

        return rowView;
    }

}

