package com.sp.gold.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "userlist.db";
    private static final int SCHEMA_VERSION = 1;

    public UserHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Will be called once when the database is not created
        db.execSQL("CREATE TABLE users_table ( _id INTEGER PRIMARY KEY AUTOINCREMENT, uinfin TEXT, name TEXT, phone TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Will not be called until SCHEMA_VERSION increases
        //Here we can upgrade the database e.g. add more tables
    }

    /* Read all records from bakeries_table */
    public Cursor getAll() {
        return (getReadableDatabase().rawQuery(
                "SELECT _id, uinfin, name, phone FROM users_table ORDER BY uinfin", null));
    }

    public Cursor getById(String id) {
        String[] args = {id};

        return (getReadableDatabase().rawQuery(
                "SELECT _id, uinfin, name, phone FROM users_table WHERE _ID = ?", args));
    }

    /* Write a record into bakeries_table */
    public void insert(String uinfin, String name, String phone) {
        ContentValues cv = new ContentValues();

        cv.put("uinfin", uinfin);
        cv.put("name", name);
        cv.put("phone", phone);

        getWritableDatabase().insert("users_table", "uinfin", cv);
    }

    public void delete(String id) {
        String[] args = {id};
        getWritableDatabase().delete("users_table",  " _ID = ?", args);
    }

    public void deleteAll()
    {
        getWritableDatabase().delete("users_table", null, null);
    }

    public String getID (Cursor c) {
        return (c.getString(0));
    }

    public String getUIN (Cursor c) {
        return (c.getString(1));
    }

    public String getName (Cursor c) {
        return (c.getString(2));
    }

    public String getPhone (Cursor c) {
        return (c.getString(3));
    }

}
