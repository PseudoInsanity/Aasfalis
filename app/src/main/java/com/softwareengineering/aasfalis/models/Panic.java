package com.softwareengineering.aasfalis.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Panic implements Serializable {

    private static final long serialVersionUID = 1L;

    private String panicFrom;
    private ArrayList<Friend> friends;

    public Panic(String panicFrom, ArrayList<Friend> myFriends) {
        this.panicFrom = panicFrom;
        this.friends = myFriends;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public String getPanicFrom() {
        return panicFrom;
    }
}
