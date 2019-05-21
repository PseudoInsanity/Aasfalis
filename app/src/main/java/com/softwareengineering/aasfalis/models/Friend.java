package com.softwareengineering.aasfalis.models;

public class Friend {

    private String firstName;
    private String lastName;
    private String userID;

    public Friend(String firstName, String lastName, String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserID() {
        return userID;
    }
}
