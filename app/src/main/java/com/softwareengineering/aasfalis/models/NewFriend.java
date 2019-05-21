package com.softwareengineering.aasfalis.models;

import java.io.Serializable;

public class NewFriend implements Serializable {

    private static final long serialVersionUID = 1L;
    private String senderMail, reveiverMail;

    public NewFriend(String senderMail, String reveiverMail) {
        this.senderMail = senderMail;
        this.reveiverMail = reveiverMail;
    }

    public String getSenderMail() {
        return senderMail;
    }

    public String getReveiverMail() {
        return reveiverMail;
    }
}
