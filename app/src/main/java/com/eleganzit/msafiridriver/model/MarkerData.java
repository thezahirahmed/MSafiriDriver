package com.eleganzit.msafiridriver.model;

import android.graphics.Bitmap;

public class MarkerData
{
    String title,snippet;
    Bitmap iconResID;
    double latitude,longitude;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Bitmap getIconResID() {
        return iconResID;
    }

    public void setIconResID(Bitmap iconResID) {
        this.iconResID = iconResID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public MarkerData(String title, String snippet, Bitmap iconResID, double latitude, double longitude) {
        this.title = title;
        this.snippet = snippet;
        this.iconResID = iconResID;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
