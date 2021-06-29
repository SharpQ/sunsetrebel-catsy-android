package com.sunsetrebel.catsy.models;


public class AddEventModel {

    private String name;
    private String date;
    private String location;
    private String event_description;
    private String event_creator_name;
    private int event_creator_photo;
    private int event_image;

    public AddEventModel() {
    }

    public AddEventModel(String name, String date, String location, String event_description, String event_creator_name, int event_image, int event_creator_photo) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.event_description = event_description;
        this.event_creator_name = event_creator_name;
        this.event_creator_photo = event_creator_photo;
        this.event_image = event_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String get_event_description() {
        return event_description;
    }

    public void set_event_description(String event_description) {
        this.event_description = event_description;
    }

    public String get_event_creator_name() {
        return event_creator_name;
    }

    public void set_event_creator_name(String event_creator_name) {
        this.location = event_creator_name;
    }

    public int getEvent_image() {
        return event_image;
    }

    public void setEvent_image(int event_image) {
        this.event_image = event_image;
    }

    public int get_event_creator_photo() {
        return event_creator_photo;
    }

    public void set_event_creator_photo(int event_creator_photo) {
        this.event_creator_photo = event_creator_photo;
    }
}
