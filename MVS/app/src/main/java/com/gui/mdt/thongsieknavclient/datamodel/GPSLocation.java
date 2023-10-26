package com.gui.mdt.thongsieknavclient.datamodel;

public class GPSLocation {
    int id;
    double latitude;
    double longitude;

    String date;
    String Status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
