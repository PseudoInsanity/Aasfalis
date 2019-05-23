package com.softwareengineering.aasfalis.models;

import java.io.Serializable;

public class NewFriend implements Serializable {

    private static final long serialVersionUID = 1L;
    private String senderMail, receiverMail;

    public NewFriend(String senderMail, String receiverMail) {
        this.senderMail = senderMail;
        this.receiverMail = receiverMail;
    }

    public String getSenderMail() {
        return senderMail;
    }

    public String getRecieverMail() {
        return receiverMail;
    }
}
