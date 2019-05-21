package com.softwareengineering.aasfalis.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    public static final long serialVersionUID = 1L;
    private String userName;

    public User(String name) {
        this.userName = name;
    }

    public String getName() {
        return userName;
    }
}

