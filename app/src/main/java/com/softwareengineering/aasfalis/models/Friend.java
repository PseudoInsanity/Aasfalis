package com.softwareengineering.aasfalis.models;

import java.io.Serializable;

public class Friend implements Serializable {

    private static final long serialVersionUID = 1L;
    private String firstName;
    private String lastName;
    private String eMail;
    private String roomID;

    public Friend(String firstName, String lastName, String eMail, String roomID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
        this.roomID = roomID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String geteMail() {
        return eMail;
    }

    public String getRoomID() {
        return roomID;
    }

}
