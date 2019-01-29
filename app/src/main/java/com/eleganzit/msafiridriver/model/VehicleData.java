package com.eleganzit.msafiridriver.model;

public class VehicleData
{
    String photoId,photo_type,photo;

    public VehicleData(String photoId, String photo_type, String photo) {
        this.photoId = photoId;
        this.photo_type = photo_type;
        this.photo = photo;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhoto_type() {
        return photo_type;
    }

    public void setPhoto_type(String photo_type) {
        this.photo_type = photo_type;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
