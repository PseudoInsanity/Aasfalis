package com.softwareengineering.aasfalis.client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.aasfalis.models.Test;
import com.softwareengineering.aasfalis.models.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class ClientService extends Service {

    private Client client = new Client();

    @Override
    public void onCreate (){
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        client.execute();

        return START_STICKY;
    }

    @Override
    public void onDestroy () {


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            client.execute();
        } else  {
            client.cancelLoop();
        }
    }



    private static class Client extends AsyncTask<Void, Void, Void> {

        private Socket socket;
        private ObjectOutputStream dataOutputStream;
        private ObjectInputStream dataInputStream;

        private Client() {}

        @Override
        protected Void doInBackground(Void... voids) {

            if (connectToServer()) {


                int i = 0;

                while (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    try {

                        Thread.sleep(1000);
                        dataOutputStream.writeObject(new Test("Hej: " + i));
                        i++;

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                /*Todo
                Handle input stream from server
                 */
                }

                try {
                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private boolean connectToServer() {

            try {

                this.socket = new Socket("192.168.0.199", 6453);

                this.dataInputStream = new ObjectInputStream(socket.getInputStream());
                this.dataOutputStream = new ObjectOutputStream(socket.getOutputStream());

                dataOutputStream.writeObject(new User(FirebaseAuth.getInstance().getUid()));

                return true;

            } catch (Exception e) {

                return false;
            }
        }

        private void cancelLoop () {
            cancel(true);
        }
    }
}
