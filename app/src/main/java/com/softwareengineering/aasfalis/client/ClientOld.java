package com.softwareengineering.aasfalis.client;

import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.aasfalis.activities.MainActivity;
import com.softwareengineering.aasfalis.models.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientOld extends AsyncTask<Void, Void, Void> {

    private Socket socket;
    private ObjectOutputStream dataOutputStream;
    private ObjectInputStream dataInputStream;
    private boolean running;

    public ClientOld() {

        Class<MainActivity> mainActivity1 = MainActivity.class;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (connectToServer()) {

            running = true;

            while (running) {

                if (isCancelled()) {
                    break;
                }

                /*Todo
                Handle input stream from server
                 */
            }
        }
        return null;
    }

    public boolean connectToServer() {

        try {

            socket = new Socket("192.168.0.199", 6453);

            dataInputStream = new ObjectInputStream(socket.getInputStream());
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());

            dataOutputStream.writeObject(new User(FirebaseAuth.getInstance().getUid()));

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    public boolean sendToServer (Object object) {

        try {

            dataOutputStream.flush();
            dataOutputStream.writeObject(object);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    public void removeThread () {

        try {
            cancel(true);
            socket.close();
            running = false;
            socket = new Socket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
