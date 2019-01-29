package com.eleganzit.msafiridriver.model;

public class StateData
{
    String state_id,country_id,state;

    public StateData(String state_id, String country_id, String state) {
        this.state_id = state_id;
        this.country_id = country_id;
        this.state = state;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
