package com.example.livedataexample;

public class Favourites {

    public long mId;
    public String mLink;
    public long mDate;

    public Favourites(long mId, String mLink, long mDate) {
        this.mId = mId;
        this.mLink = mLink;
        this.mDate = mDate;
    }

    public Favourites(Favourites favourites) {
        mId = favourites.mId;
        mLink = favourites.mLink;
        mDate = favourites.mDate;
    }
}
