package com.softwareengineering.aasfalis.models;

public class FriendPosition {

    private double lat, lon;
    private String name;

    public FriendPosition(double lat, double lon, String name) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FriendPosition{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", name='" + name + '\'' +
                '}';
    }
}
