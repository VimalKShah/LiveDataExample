package com.example.livedataexample;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class FavouriteViewModel extends AndroidViewModel {

    private FavouriteDbHelper mFavouriteDbHelper;
    private ArrayList<Favourites> mFavourites;

    public FavouriteViewModel(@NonNull Application application) {
        super(application);
        mFavouriteDbHelper = new FavouriteDbHelper(application);
    }

    public List<Favourites> getFavourites() {
        if(mFavourites == null) {
            mFavourites = new ArrayList<>();
            createDummyList();
            loadFavourites();
        }
        ArrayList<Favourites> clonedFavs = new ArrayList<>(mFavourites.size());
        for (int i = 0; i < mFavourites.size(); i++) {
            clonedFavs.add(new Favourites(mFavourites.get(i)));
        }
        return clonedFavs;
    }

    private void createDummyList() {
        addFav("https://www.journaldev.com", (new Date()).getTime());
        addFav("https://www.medium.com", (new Date()).getTime());
        addFav("https://www.reddit.com", (new Date()).getTime());
        addFav("https://www.github.com", (new Date()).getTime());
        addFav("https://www.hackerrank.com", (new Date()).getTime());
        addFav("https://www.developers.android.com", (new Date()).getTime());
    }

    public Favourites addFav(String url, long date) {
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(DbSettings.DbEntry.COLUMN_NAME_URL, url);
        contentValues.put(DbSettings.DbEntry.COLUMN_NAME_DATE, date);
        SQLiteDatabase database = mFavouriteDbHelper.getWritableDatabase();
        long id = database.insertWithOnConflict(DbSettings.DbEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        database.close();
        Favourites favourites = new Favourites(id, url, date);
        mFavourites.add(favourites);
        return favourites;
    }

    private void loadFavourites() {
        mFavourites.clear();
        SQLiteDatabase database = mFavouriteDbHelper.getReadableDatabase();
        Cursor cursor = database.query(DbSettings.DbEntry.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Favourites favourites = new Favourites(cursor.getLong(cursor.getColumnIndex(DbSettings.DbEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(DbSettings.DbEntry.COLUMN_NAME_URL)),
                    cursor.getLong(cursor.getColumnIndex(DbSettings.DbEntry.COLUMN_NAME_DATE)));
            mFavourites.add(favourites);
        }
        cursor.close();
    }

    public void removeFavourites(long id) {
        SQLiteDatabase database = mFavouriteDbHelper.getWritableDatabase();
        database.delete(DbSettings.DbEntry.TABLE_NAME, DbSettings.DbEntry._ID + " =? ", new String[] {Long.toString(id)});
        database.close();
        int index = -1;
        for(Favourites favourite : mFavourites) {
            index++;
            if(favourite.mId == id) {
                mFavourites.remove(index);
                break;
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mFavourites.clear();
        mFavourites = null;
    }
}
