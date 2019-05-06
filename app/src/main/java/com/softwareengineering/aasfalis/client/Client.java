package com.softwareengineering.aasfalis.client;

import android.os.AsyncTask;

import com.softwareengineering.aasfalis.activities.MainActivity;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, Void> {

    private Socket socket;
    private ObjectOutputStream dataOutputStream;
    private ObjectInputStream dataInputStream;
    private volatile boolean running = true;
    private MainActivity mainActivity;

    public Client (MainActivity mainActivity) {

        this.mainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (connectToServer()) {

            /*Todo
            Send user to server
             */

            while (running) {

                /*Todo
                Handle input stream from server
                 */
            }
        }
        return null;
    }

    private boolean connectToServer() {

        try {

            this.socket = new Socket("192.168.0.199" +
                    "", 8080);

            this.dataInputStream = new ObjectInputStream(socket.getInputStream());
            this.dataOutputStream = new ObjectOutputStream(socket.getOutputStream());

            return true;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    private boolean sendToServer (Object object) {

        try {

            dataOutputStream.flush();
            dataOutputStream.writeObject(object);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }
}
