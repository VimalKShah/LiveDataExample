package com.example.livedataexample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FavouriteDbHelper extends SQLiteOpenHelper {

    public FavouriteDbHelper(@Nullable Context context) {
        super(context, DbSettings.DB_NAME, null, DbSettings.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + DbSettings.DbEntry.TABLE_NAME + " ( " + DbSettings.DbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DbSettings.DbEntry.COLUMN_NAME_URL + " TEXT NOT NULL, " + DbSettings.DbEntry.COLUMN_NAME_DATE + " INTEGER NOT NULL);";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbSettings.DbEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
