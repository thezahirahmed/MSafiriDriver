package com.eleganzit.msafiridriver.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SubPassengersData implements Parcelable {

    String id,user_id,rating,rstatus,fname,lname,photo;

    public SubPassengersData(String id, String user_id, String rating, String rstatus, String fname, String lname, String photo) {
        this.id = id;
        this.user_id = user_id;
        this.rating = rating;
        this.rstatus = rstatus;
        this.fname = fname;
        this.lname = lname;
        this.photo = photo;
    }

    protected SubPassengersData(Parcel in) {
        id = in.readString();
        user_id = in.readString();
        rating = in.readString();
        rstatus = in.readString();
        fname = in.readString();
        lname = in.readString();
        photo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user_id);
        dest.writeString(rating);
        dest.writeString(rstatus);
        dest.writeString(fname);
        dest.writeString(lname);
        dest.writeString(photo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubPassengersData> CREATOR = new Creator<SubPassengersData>() {
        @Override
        public SubPassengersData createFromParcel(Parcel in) {
            return new SubPassengersData(in);
        }

        @Override
        public SubPassengersData[] newArray(int size) {
            return new SubPassengersData[size];
        }
    };

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
