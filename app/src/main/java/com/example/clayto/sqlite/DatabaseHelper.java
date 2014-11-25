package com.example.clayto.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.clayto.rssreaderapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Clayto on 14-11-19.
 * Database helper to connect to article database and query, update, create and delete
 * records from the database
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "RSSArticles";

    // Table Names
    private static final String TABLE_ARTICLES = "articles";
    private static final String TABLE_CATEGORIES = "categories";

    // Common column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CREATED_AT = "created_at";

    // ARTICLES Table - column names
    private static final String COLUMN_ARTICLE_TITLE = "title";
    private static final String COLUMN_ARTICLE_PUBLISH_DATE = "publish_date";
    private static final String COLUMN_ARTICLE_AUTHOR = "author";
    private static final String COLUMN_ARTICLE_DESCRIPTION = "description";
    private static final String COLUMN_ARTICLE_URL_LINK = "url_link";
    private static final String COLUMN_ARTICLE_CATEGORY_ID = "category_id";

    // CATEGORIES Table - column names
    private static final String COLUMN_CATEGORY_NAME = "name";

    // Table Create Statements
    // Articles table create statement
    private static final String CREATE_TABLE_ARTICLES = "CREATE TABLE " + TABLE_ARTICLES
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_CREATED_AT + " TEXT NOT NULL, "
            + COLUMN_ARTICLE_TITLE + " TEXT NOT NULL, "
            + COLUMN_ARTICLE_PUBLISH_DATE + " TEXT NOT NULL, "
            + COLUMN_ARTICLE_AUTHOR + " TEXT NOT NULL, "
            + COLUMN_ARTICLE_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_ARTICLE_URL_LINK + " TEXT NOT NULL, "
            + COLUMN_ARTICLE_CATEGORY_ID + " INTEGER NOT NULL"
            + ");";

    // Categories table create statement
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_CREATED_AT + " TEXT NOT NULL, "
            + COLUMN_CATEGORY_NAME + " TEXT NOT NULL"
            + ");";

    //Drop table statements
    private static final String DROP_TABLE_ARTICLES = "DROP TABLE IF EXISTS " + TABLE_ARTICLES;
    private static final String DROP_TABLE_CATEGORIES = "DROP TABLE IF EXISTS " + TABLE_CATEGORIES;

    private final String[] mCategories;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCategories = context.getResources().getStringArray(R.array.pref_default_feed_entries);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_ARTICLES);
        db.execSQL(CREATE_TABLE_CATEGORIES);

        for(String categoryName : mCategories){
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, categoryName);
            values.put(COLUMN_CREATED_AT, getDateTime());
            db.insert(TABLE_CATEGORIES, null, values);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL(DROP_TABLE_ARTICLES);
        db.execSQL(DROP_TABLE_CATEGORIES);
        // create new tables
        onCreate(db);
    }

    /*
     * Creating an article
     */
    public long createArticle(Article article) {

        String sql = "INSERT INTO " + TABLE_ARTICLES
                + " (" + COLUMN_ARTICLE_TITLE + ", " + COLUMN_ARTICLE_PUBLISH_DATE + ", " + COLUMN_ARTICLE_AUTHOR + ", "
                + COLUMN_ARTICLE_DESCRIPTION + ", " + COLUMN_ARTICLE_URL_LINK + ", " + COLUMN_ARTICLE_CATEGORY_ID + ", " + COLUMN_CREATED_AT
                + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, article.getTitle());
        statement.bindString(2, article.getPublishDate());
        statement.bindString(3, article.getAuthor());
        statement.bindString(4, article.getDescription());
        statement.bindString(5, article.getUrlLink());
        statement.bindLong(6, article.getCategoryID());
        statement.bindString(7, getDateTime());
        long articleID = statement.executeInsert();

        db.close();

        return articleID;
    }

    /*
     * Checking to see if article with specific title exists
     */
    public boolean articleWithTitleExists(String articleTitle) {

        boolean articleExists = false;
        String selectQuery = "SELECT * FROM " + TABLE_ARTICLES + " WHERE " + COLUMN_ARTICLE_TITLE + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {articleTitle});

        int count = cursor.getCount();

        cursor.close();
        db.close();

        if(count > 0) {
            articleExists = true;
        }
        // return count
        return articleExists;
    }

    /*
     * Getting all articles
     */
    public ArrayList<Article> getAllArticles() {

        ArrayList<Article> articles = new ArrayList<Article>();
        String selectQuery = "SELECT * FROM " + TABLE_ARTICLES;

        Log.d(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {

            do {

                Article article = new Article();
                article.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
                article.setCreatedAt(c.getString(c.getColumnIndex(COLUMN_CREATED_AT)));
                article.setTitle((c.getString(c.getColumnIndex(COLUMN_ARTICLE_TITLE))));
                article.setPublishDate((c.getString(c.getColumnIndex(COLUMN_ARTICLE_PUBLISH_DATE))));
                article.setAuthor((c.getString(c.getColumnIndex(COLUMN_ARTICLE_AUTHOR))));
                article.setDescription((c.getString(c.getColumnIndex(COLUMN_ARTICLE_DESCRIPTION))));
                article.setUrlLink((c.getString(c.getColumnIndex(COLUMN_ARTICLE_URL_LINK))));
                article.setCategoryID((c.getInt(c.getColumnIndex(COLUMN_ARTICLE_CATEGORY_ID))));

                // adding to article list
                articles.add(article);

            } while (c.moveToNext());

            c.close();
        }

        db.close();

        return articles;
    }

    /*
     * Getting article count
     */
    public int getArticleCount() {

        String countQuery = "SELECT  * FROM " + TABLE_ARTICLES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }

    /*
     * Get single category
     */
    public Category getCategoryByName(String categoryName) {

        SQLiteDatabase db = this.getReadableDatabase();
        Category category = new Category();

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_NAME + " = ?";

        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] {categoryName});

        if (c != null) {

            c.moveToFirst();

            category.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
            category.setCreatedAt(c.getString(c.getColumnIndex(COLUMN_CREATED_AT)));
            category.setName((c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME))));

            c.close();

        }

        db.close();

        return category;
    }

    //get the current data and time and return it as a string
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /*
     * Getting all categories
     */
    /*public List<Category> getAllCategories() {

        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES;

        Log.d(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {

            do {

                Category category = new Category();
                category.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
                category.setCreatedAt(c.getString((c.getColumnIndex(COLUMN_CREATED_AT))));
                category.setName(c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME)));

                // adding to tags list
                categories.add(category);

            } while (c.moveToNext());

            c.close();

        }

        db.close();

        return categories;
    }*/

    /*
     * Deleting an article
     */
    /*public void deleteArticle(long articleID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ARTICLES, COLUMN_ID + " = " + articleID,null);
        db.close();
    }*/

    /*
     * Get single article
     */
    /*public Article getArticle(long articleID) {

        SQLiteDatabase db = this.getReadableDatabase();
        Article article = new Article();

        String selectQuery = "SELECT * FROM " + TABLE_ARTICLES + " WHERE " + COLUMN_ID + " = " + articleID;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();

            article.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
            article.setCreatedAt(c.getString(c.getColumnIndex(COLUMN_CREATED_AT)));
            article.setTitle((c.getString(c.getColumnIndex(COLUMN_ARTICLE_TITLE))));
            article.setPublishDate((c.getString(c.getColumnIndex(COLUMN_ARTICLE_PUBLISH_DATE))));
            article.setAuthor((c.getString(c.getColumnIndex(COLUMN_ARTICLE_AUTHOR))));
            article.setDescription((c.getString(c.getColumnIndex(COLUMN_ARTICLE_DESCRIPTION))));
            article.setUrlLink((c.getString(c.getColumnIndex(COLUMN_ARTICLE_URL_LINK))));
            article.setCategoryID((c.getInt(c.getColumnIndex(COLUMN_ARTICLE_CATEGORY_ID))));

            c.close();

        }

        db.close();

        return article;
    }*/

    /*
     * Getting all articles under single category
     */
    /*public List<Article> getAllArticlesByCategory(String categoryName) {

        List<Article> articles = new ArrayList<Article>();
        //SELECT * FROM todos td, tags tg, todo_tags tt WHERE tg.tag_name = ‘Watchlist’ AND tg.id = tt.tag_id AND td.id = tt.todo_id;
        //SELECT * FROM articles, categories WHERE categories.name = categoryName AND categories.id = articles.category_id
        *//*String selectQuery = "SELECT  * FROM " + TABLE_TODO + " td, "
        + TABLE_TAG + " tg, " + TABLE_TODO_TAG + " tt WHERE tg."
                + KEY_TAG_NAME + " = '" + tag_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_TAG_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_TODO_ID;*//*

        String selectQuery = "SELECT *"
                + " FROM " + TABLE_ARTICLES + ", " + TABLE_CATEGORIES
                + " WHERE " + TABLE_CATEGORIES + "." + COLUMN_CATEGORY_NAME + " = '" + DatabaseUtils.sqlEscapeString(categoryName) + "'"
                + " AND " + TABLE_CATEGORIES + "." + COLUMN_ID + " = " + TABLE_ARTICLES + "." + COLUMN_ARTICLE_CATEGORY_ID;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {

            do {

                Article article = new Article();
                article.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
                article.setCreatedAt(c.getString(c.getColumnIndex(COLUMN_CREATED_AT)));
                article.setTitle((c.getString(c.getColumnIndex(COLUMN_ARTICLE_TITLE))));
                article.setPublishDate((c.getString(c.getColumnIndex(COLUMN_ARTICLE_PUBLISH_DATE))));
                article.setAuthor((c.getString(c.getColumnIndex(COLUMN_ARTICLE_AUTHOR))));
                article.setDescription((c.getString(c.getColumnIndex(COLUMN_ARTICLE_DESCRIPTION))));
                article.setUrlLink((c.getString(c.getColumnIndex(COLUMN_ARTICLE_URL_LINK))));
                article.setCategoryID((c.getInt(c.getColumnIndex(COLUMN_ARTICLE_CATEGORY_ID))));

                // adding to article list
                articles.add(article);

            } while (c.moveToNext());

            c.close();

        }

        db.close();

        return articles;
    }*/

    /*
     * Get single article
     */
    /*public Article getArticleByTitle(String articleTitle) {

        SQLiteDatabase db = this.getReadableDatabase();
        Article article = new Article();

        String selectQuery = "SELECT * FROM " + TABLE_ARTICLES + " WHERE " + COLUMN_ARTICLE_TITLE + " LIKE '%" + DatabaseUtils.sqlEscapeString(articleTitle) + "%';";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();

            article.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
            article.setCreatedAt(c.getString(c.getColumnIndex(COLUMN_CREATED_AT)));
            article.setTitle((c.getString(c.getColumnIndex(COLUMN_ARTICLE_TITLE))));
            article.setPublishDate((c.getString(c.getColumnIndex(COLUMN_ARTICLE_PUBLISH_DATE))));
            article.setAuthor((c.getString(c.getColumnIndex(COLUMN_ARTICLE_AUTHOR))));
            article.setDescription((c.getString(c.getColumnIndex(COLUMN_ARTICLE_DESCRIPTION))));
            article.setUrlLink((c.getString(c.getColumnIndex(COLUMN_ARTICLE_URL_LINK))));
            article.setCategoryID((c.getInt(c.getColumnIndex(COLUMN_ARTICLE_CATEGORY_ID))));

            c.close();

        }

        db.close();

        return article;
    }*/

    /*
     * Updating an article
     */
    /*public int updateArticle(Article article) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ARTICLE_TITLE, article.getTitle());
        values.put(COLUMN_ARTICLE_PUBLISH_DATE, article.getPublishDate());
        values.put(COLUMN_ARTICLE_AUTHOR, article.getAuthor());
        values.put(COLUMN_ARTICLE_DESCRIPTION, article.getDescription());
        values.put(COLUMN_ARTICLE_URL_LINK, article.getUrlLink());
        values.put(COLUMN_ARTICLE_CATEGORY_ID, article.getCategoryID());

        // updating row
        return db.update(TABLE_ARTICLES, values, COLUMN_ID + " = " + article.getId(),null);
    }*/
    /*
     * Creating category
     */
    /*public long createCategory(Category category) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.getName());
        values.put(COLUMN_CREATED_AT, getDateTime());

        // insert row
        long categoryID = db.insert(TABLE_CATEGORIES, null, values);

        return categoryID;
    }*/
    /*
     * Get single category
     */
    /*public Category getCategory(long categoryID) {

        SQLiteDatabase db = this.getReadableDatabase();
        Category category = new Category();

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_ID + " = " + categoryID;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();

            category.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
            category.setCreatedAt(c.getString(c.getColumnIndex(COLUMN_CREATED_AT)));
            category.setName((c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME))));

            c.close();
        }

        db.close();

        return category;
    }*/
    /*
     * Getting category count
     */
    /*public int getCategoryCount() {

        String countQuery = "SELECT  * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }*/
    /*
     * Updating a category
     */
    /*public int updateCategory(Category category) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.getName());

        // updating row
        return db.update(TABLE_CATEGORIES, values, COLUMN_ID + " = " + category.getId(),null);

    }*/

    /*
     * Deleting a category
     */
    /*public void deleteCategory(Category category, boolean should_delete_all_tag_articles) {

        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting category check if articles under this category should also be deleted
        if (should_delete_all_tag_articles) {
            // get all articles under this tag
            List<Article> allCategoryArticles = getAllArticlesByCategory(category.getName());

            // delete all articles
            for (Article categoryArticle : allCategoryArticles) {
                // delete article
                deleteArticle(categoryArticle.getId());
            }
        }

        // now delete the category
        db.delete(TABLE_CATEGORIES, COLUMN_ID + " = " + category.getId(),null);
        db.close();
    }*/

    /*// closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }*/
}
