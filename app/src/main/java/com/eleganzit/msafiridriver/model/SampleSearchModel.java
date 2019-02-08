package com.eleganzit.msafiridriver.model;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SampleSearchModel implements Searchable {
    private String address,lat,lng;

    public SampleSearchModel(String address,String lat,String lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String getTitle() {
        return address;
    }

    public SampleSearchModel setTitle(String address) {
        address = address;
        return this;
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