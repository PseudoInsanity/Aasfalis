package com.softwareengineering.aasfalis.models;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private String from, to, username;
    private String message;
    private String time;

    public Message(String from, String to, String message, String time, String username) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.time = time;
        this.username = username;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }
}
