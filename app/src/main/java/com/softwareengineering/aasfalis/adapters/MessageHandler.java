package com.softwareengineering.aasfalis.adapters;

import com.softwareengineering.aasfalis.models.Message;

import java.util.ArrayList;

public class MessageHandler {

    private ArrayList<Message> messages;

    public MessageHandler () {

        messages = new ArrayList<>();
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage (Message message) {

        messages.add(message);
    }

    public int lastIndex () {

        return messages.size() - 1;
    }
}
