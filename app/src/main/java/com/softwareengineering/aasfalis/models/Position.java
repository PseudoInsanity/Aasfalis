package com.softwareengineering.aasfalis.models;

import java.util.Date;

public class Position {

    private double lat, lon;
    private Date time;

    public Position(double lat, double lon, Date time) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public Date getTime() {
        return time;
    }
}
