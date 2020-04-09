package com.softwareengineering.aasfalis.adapters;

import android.os.Looper;

import com.softwareengineering.aasfalis.activities.MainActivity;
import com.softwareengineering.aasfalis.fragments.MessageFragment;
import com.softwareengineering.aasfalis.models.Message;

import java.util.ArrayList;

import static com.softwareengineering.aasfalis.activities.MainActivity.messages;

public class MessageHandler {

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
