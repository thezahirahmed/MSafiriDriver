package com.eleganzit.msafiridriver.model;

public class RatingsData
{
    String trip_id,user_id,rating,trip_status,datetime,fname,lname,photo,review;

    public RatingsData(String trip_id, String user_id, String rating, String trip_status, String datetime, String fname, String lname, String photo, String review) {
        this.trip_id = trip_id;
        this.user_id = user_id;
        this.rating = rating;
        this.trip_status = trip_status;
        this.datetime = datetime;
        this.fname = fname;
        this.lname = lname;
        this.photo = photo;
        this.review = review;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
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

    public String getTrip_status() {
        return trip_status;
    }

    public void setTrip_status(String trip_status) {
        this.trip_status = trip_status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
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

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
