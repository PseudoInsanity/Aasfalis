package com.softwareengineering.aasfalis.models;

import java.io.Serializable;

public class NewFriend implements Serializable {

    private static final long serialVersionUID = 1L;
    private String eMail;

    public NewFriend(String eMail) {
        this.eMail = eMail;
    }

    public String geteMail() {
        return eMail;
    }
}
