package com.eleganzit.msafiridriver.model;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class PassengerData extends ExpandableGroup<SubPassengersData>
{
    String id,user_id,rating,rstatus,fname,lname,photo;

    public PassengerData(String id, List<SubPassengersData> passengers, String user_id, String rating, String rstatus, String fname, String lname, String photo) {
        super(fname, passengers);
        this.id = id;
        this.user_id = user_id;
        this.rating = rating;
        this.rstatus = rstatus;
        this.fname = fname;
        this.lname = lname;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRstatus() {
        return rstatus;
    }

    public void setRstatus(String rstatus) {
        this.rstatus = rstatus;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
