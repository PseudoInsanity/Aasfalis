package com.softwareengineering.aasfalis.adapters;

import com.softwareengineering.aasfalis.models.Message;

import java.util.ArrayList;

import static com.softwareengineering.aasfalis.activities.MainActivity.messages;

public class MessageHandler {




    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage (Message message) {

        System.out.println("HEJEHEJEHJ2222222: " + message.getMessage());
        messages.add(message);
    }

    public int lastIndex () {

        return messages.size() - 1;
    }
}
