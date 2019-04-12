package com.eleganzit.msafiridriver.model;

import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by eleganz on 7/2/19.
 */

public class ContactModel  implements Searchable {
    private String address,lat,lng;

    public ContactModel(String address, String lat, String lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String getTitle() {
        return address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
