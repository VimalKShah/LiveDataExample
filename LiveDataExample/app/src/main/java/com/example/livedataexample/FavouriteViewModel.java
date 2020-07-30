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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class FavouriteViewModel extends AndroidViewModel {

    private FavouriteDbHelper mFavouriteDbHelper;
    private MutableLiveData<List<Favourites>> mFavourites;

    public FavouriteViewModel(@NonNull Application application) {
        super(application);
        mFavouriteDbHelper = new FavouriteDbHelper(application);
    }

    public LiveData<List<Favourites>> getFavourites() {
        if(mFavourites == null) {
            mFavourites = new MutableLiveData<>();
            loadFavourites();
        }
        return mFavourites;
    }

    private void createDummyList() {
        addFav("https://www.journaldev.com", (new Date()).getTime());
        addFav("https://www.medium.com", (new Date()).getTime());
        addFav("https://www.reddit.com", (new Date()).getTime());
        addFav("https://www.github.com", (new Date()).getTime());
        addFav("https://www.hackerrank.com", (new Date()).getTime());
        addFav("https://www.developers.android.com", (new Date()).getTime());
    }

    public void addFav(String url, long date) {
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(DbSettings.DbEntry.COLUMN_NAME_URL, url);
        contentValues.put(DbSettings.DbEntry.COLUMN_NAME_DATE, date);
        SQLiteDatabase database = mFavouriteDbHelper.getWritableDatabase();
        long id = database.insertWithOnConflict(DbSettings.DbEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        database.close();

        List<Favourites> favourites = mFavourites.getValue();
        if(favourites == null) {
            favourites = new ArrayList<>();
        }
        Favourites favourite = new Favourites(id, url, date);
        favourites.add(favourite);
        mFavourites.setValue(favourites);
    }

    private void loadFavourites() {
        List<Favourites> favourites = new ArrayList<>();
        SQLiteDatabase database = mFavouriteDbHelper.getReadableDatabase();
        Cursor cursor = database.query(DbSettings.DbEntry.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Favourites favourite = new Favourites(cursor.getLong(cursor.getColumnIndex(DbSettings.DbEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(DbSettings.DbEntry.COLUMN_NAME_URL)),
                    cursor.getLong(cursor.getColumnIndex(DbSettings.DbEntry.COLUMN_NAME_DATE)));
            favourites.add(favourite);
        }
        cursor.close();
        database.close();
        mFavourites.setValue(favourites);
    }

    public void removeFavourites(long id) {
        SQLiteDatabase database = mFavouriteDbHelper.getWritableDatabase();
        database.delete(DbSettings.DbEntry.TABLE_NAME, DbSettings.DbEntry._ID + " =? ", new String[]{Long.toString(id)});
        database.close();
        List<Favourites> favouritesList = mFavourites.getValue();
        ArrayList<Favourites> clonedFavouritesList = new ArrayList<>(favouritesList.size());
        for (int i = 0; i < favouritesList.size(); i++) {
            clonedFavouritesList.add(new Favourites(favouritesList.get(i)));
        }

        int index = -1;
        for (int i = 0; i < clonedFavouritesList.size(); i++) {
            Favourites favourites = clonedFavouritesList.get(i);
            if (favourites.mId == id) {
                index = i;
            }
        }
        if (index != -1) {
            clonedFavouritesList.remove(index);
        }
        mFavourites.setValue(clonedFavouritesList);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
