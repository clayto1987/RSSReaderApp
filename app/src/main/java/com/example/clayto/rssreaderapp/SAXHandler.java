package com.example.clayto.rssreaderapp;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;


/**
 * Class:       SAXHandler
 * Description: Parses through an XML file looking for the data in all title, description, link and publishDate elements
 * Created By:  Clayton on 2014-10-08.
 */
public class SAXHandler extends DefaultHandler {

    private boolean inItem, inTitle, inPublishDate, inDescription, inLink;
    private ArrayList<String> titles, publishDates, descriptions, links;
    private StringBuilder sb;

    public SAXHandler() {
        titles = new ArrayList<String>();
        publishDates = new ArrayList<String>();
        descriptions = new ArrayList<String>();
        links = new ArrayList<String>();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //super.endElement(uri, localName, qName);
        //Log.d("test", "endElement: " + qName);

        if (inItem) {

            if(qName.equals("item")) {
                inItem = false;
            } else if (qName.equals("title")) {
                inTitle = false;
                titles.add(sb.toString());
            } else if (qName.equals("pubDate")) {
                inPublishDate = false;
                publishDates.add(sb.toString());
            } else if (qName.equals("description")) {
                inDescription = false;
                descriptions.add(sb.toString());
            } else if (qName.equals("link")) {
                inLink = false;
                links.add(sb.toString());
            }

        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //super.characters(ch, start, length);
        String s = new String(ch,start,length);
        //Log.d("test", "characters: " + s);

        if(inItem) {

            if (inTitle || inPublishDate || inDescription || inLink) {
                sb.append(s);
            }
            /*if (inTitle) {
                titles.add(s);
            } else if (inPublishDate) {
                publishDates.add(s);
            } else if (inDescription) {
                descriptions.add(s);
            }*/
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        //Log.d("test", "startElement: " + qName);

        if(qName.equals("item")) {
            inItem = true;
        } else {

            sb = new StringBuilder("");

            if (qName.equals("title")) {
                inTitle = true;
            } else if (qName.equals("pubDate")) {
                inPublishDate = true;
            } else if (qName.equals("description")) {
                inDescription = true;
            } else if (qName.equals("link")) {
                inLink = true;
            }

        }

    }

    public ArrayList<String> getTitles() {
        return titles;
    }

    public ArrayList<String> getPublishDates() {
        return  publishDates;
    }

    public ArrayList<String> getDescriptions() {
        return descriptions;
    }

    public ArrayList<String> getLinks() {
        return links;
    }

}

