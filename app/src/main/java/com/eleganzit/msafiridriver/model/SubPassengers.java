package com.eleganzit.msafiridriver.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class SubPassengers extends ExpandableGroup<SubPassengersData> {

    public SubPassengers(String user_id, List<SubPassengersData> passengers) {
        super(user_id, passengers);
    }
}