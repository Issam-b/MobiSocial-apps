package com.derassom.issam.mobisociallab4;

/**
 * Created by assam on 2/19/2018.
 */

public class MessageDataHandler {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private double latitude;
    private double longitude;
    private String title;
    private String message;
    private String status;

    public MessageDataHandler(int id, double latitude, double longitude, String title, String message) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.message = message;
        this.status = "ok";
    }

    public MessageDataHandler(double latitude, double longitude, String title, String message) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.message = message;
        this.status = "ok";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MessageDataHandler(String status) {
        if(!status.equals( "ok" ))
            this.status = "bad";
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
