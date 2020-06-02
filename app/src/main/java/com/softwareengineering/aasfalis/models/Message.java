package com.softwareengineering.aasfalis.models;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private String from, to;
    private String message;
    private Date timestamp;

    public Message(){}

    public Message(String from, String to, String message, Date time) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = time;
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

    public Date getTime() {
        return timestamp;
    }

}
