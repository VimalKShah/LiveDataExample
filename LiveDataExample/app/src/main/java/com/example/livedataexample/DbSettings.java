package com.example.livedataexample;

import android.provider.BaseColumns;

public class DbSettings {
    public static final String DB_NAME = "FavouritesLink.db";
    public static final int DB_VERSION = 1;

    public class DbEntry implements BaseColumns {
        public static final String TABLE_NAME = "favourites";
        public static final String _ID = "id";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
