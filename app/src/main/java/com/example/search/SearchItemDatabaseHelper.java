package com.example.search;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class SearchItemDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "search_items.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "items";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";

    public SearchItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TITLE + " TEXT," +
                COL_DESCRIPTION + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertItem(SearchItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, item.getTitle());
        values.put(COL_DESCRIPTION, item.getDescription());
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<SearchItem> getAllSavedItems() {
        List<SearchItem> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
                itemList.add(new SearchItem(id, title, description));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    public static List<SearchItem> getAllSavedItems(Context context) {
        SearchItemDatabaseHelper dbHelper = new SearchItemDatabaseHelper(context);
        return dbHelper.getAllSavedItems();
    }

    public SearchItem getItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            SearchItem item = new SearchItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION))
            );
            cursor.close();
            db.close();
            return item;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    public int updateItem(SearchItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, item.getTitle());
        values.put(COL_DESCRIPTION, item.getDescription());
        int rows = db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    public int deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    // ✅ Static helper for DetailActivity
    public static boolean saveItem(Context context, SearchItem item) {
        SearchItemDatabaseHelper dbHelper = new SearchItemDatabaseHelper(context);
        long id = dbHelper.insertItem(item);
        return id != -1;
    }

    public static SearchItem loadItem(Context context, int id) {
        SearchItemDatabaseHelper dbHelper = new SearchItemDatabaseHelper(context);
        return dbHelper.getItemById(id);
    }

    public static boolean deleteItem(Context context, int id) {
        SearchItemDatabaseHelper dbHelper = new SearchItemDatabaseHelper(context);
        int rows = dbHelper.deleteItem(id);
        return rows > 0;
    }
}
