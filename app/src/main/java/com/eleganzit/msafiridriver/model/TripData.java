package com.eleganzit.msafiridriver.model;

public class TripData

{
    String trip_type,id,from_title,from_lat,from_lng,from_address,to_title,to_lat,to_lng,to_address,pickup_time,destination_time,statuss,trip_price;

    public TripData(String trip_type, String id, String from_title, String from_lat, String from_lng, String from_address, String to_title, String to_lat, String to_lng, String to_address, String pickup_time, String destination_time, String statuss, String trip_price) {
        this.trip_type = trip_type;
        this.id = id;
        this.from_title = from_title;
        this.from_lat = from_lat;
        this.from_lng = from_lng;
        this.from_address = from_address;
        this.to_title = to_title;
        this.to_lat = to_lat;
        this.to_lng = to_lng;
        this.to_address = to_address;
        this.pickup_time = pickup_time;
        this.destination_time = destination_time;
        this.statuss = statuss;
        this.trip_price = trip_price;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom_title() {
        return from_title;
    }

    public void setFrom_title(String from_title) {
        this.from_title = from_title;
    }

    public String getFrom_lat() {
        return from_lat;
    }

    public void setFrom_lat(String from_lat) {
        this.from_lat = from_lat;
    }

    public String getFrom_lng() {
        return from_lng;
    }

    public void setFrom_lng(String from_lng) {
        this.from_lng = from_lng;
    }

    public String getFrom_address() {
        return from_address;
    }

    public void setFrom_address(String from_address) {
        this.from_address = from_address;
    }

    public String getTo_title() {
        return to_title;
    }

    public void setTo_title(String to_title) {
        this.to_title = to_title;
    }

    public String getTo_lat() {
        return to_lat;
    }

    public void setTo_lat(String to_lat) {
        this.to_lat = to_lat;
    }

    public String getTo_lng() {
        return to_lng;
    }

    public void setTo_lng(String to_lng) {
        this.to_lng = to_lng;
    }

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
    }

    public String getPickup_time() {
        return pickup_time;
    }

    public void setPickup_time(String pickup_time) {
        this.pickup_time = pickup_time;
    }

    public String getDestination_time() {
        return destination_time;
    }

    public void setDestination_time(String destination_time) {
        this.destination_time = destination_time;
    }

    public String getStatuss() {
        return statuss;
    }

    public void setStatuss(String statuss) {
        this.statuss = statuss;
    }

    public String getTrip_price() {
        return trip_price;
    }

    public void setTrip_price(String trip_price) {
        this.trip_price = trip_price;
    }
}
