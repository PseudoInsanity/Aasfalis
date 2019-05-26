package com.softwareengineering.aasfalis.models;

public class Friend {

    private String firstName;
    private String lastName;
    private String eMail;

    public Friend(String firstName, String lastName, String eMail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
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

}
