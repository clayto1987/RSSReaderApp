package com.example.clayto.sqlite;

/**
 * Created by Clayto on 14-11-19.
 */
public class Article {

    long id;
    String createdAt;
    String title;
    String publishDate;
    String author;
    String description;
    String urlLink;
    long categoryID;


    // constructors
    public Article() {
    }

    public Article(String title, String publishDate, String author, String description, String urlLink, long categoryID) {
        this.title = title;
        this.publishDate = publishDate;
        this.author = author;
        this.description = description;
        this.urlLink = urlLink;
        this.categoryID = categoryID;
    }

    public Article(long id, String title, String publishDate, String author, String description, String urlLink, long categoryID) {
        this.id = id;
        this.title = title;
        this.publishDate = publishDate;
        this.author = author;
        this.description = description;
        this.urlLink = urlLink;
        this.categoryID = categoryID;
    }

    // setters
    public void setId(long id) {
        this.id = id;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
    }



    // getters
    public long getId() {
        return this.id;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public String getTitle() {
        return this.title;
    }

    public String getPublishDate() {
        return this.publishDate;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUrlLink() {
        return this.urlLink;
    }

    public long getCategoryID() {
        return this.categoryID;
    }

    public String toString() {
        return "Article ID: " + this.getId()
                + "Article Created At: " + this.getCreatedAt()
                + "Article Title: " + this.getTitle()
                + "Article Publish Date: " + this.getPublishDate()
                + "Article Author: " + this.getAuthor()
                + "Article Description: " + this.getDescription()
                + "Article URL Link: " + this.getUrlLink()
                + "Article Category ID: " + this.getCategoryID();
    }
}
