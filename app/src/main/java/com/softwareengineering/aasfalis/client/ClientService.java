package com.softwareengineering.aasfalis.client;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.aasfalis.adapters.MessageAdapter;
import com.softwareengineering.aasfalis.adapters.MessageHandler;
import com.softwareengineering.aasfalis.models.Message;
import com.softwareengineering.aasfalis.models.NewFriend;
import com.softwareengineering.aasfalis.models.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientService extends Service {

    private static Client client = new Client();

    @Override
    public void onCreate () {
            client = new Client();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return START_STICKY;
    }

    @Override
    public void onDestroy () {


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            breakLoop();
            forceOut();
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    public static void sendObject (Object object) {

        client.sendToServer(object);
    }

    public static void forceOut () {
        client.closeSocket();
    }

    public static void breakLoop () {
        client.cancelLoop();
    }



    private static class Client extends AsyncTask<Void, Void, Void> {

        private Socket socket;
        private ObjectOutputStream dataOutputStream;
        private ObjectInputStream dataInputStream;
        private Object object;
        private Database database;
        private MessageHandler messageHandler;
        private MessageAdapter adapter;

        private Client() {}

        @Override
        protected Void doInBackground(Void... voids) {

            if (connectToServer()) {

                database = new Database();
                messageHandler = new MessageHandler();
                adapter = new MessageAdapter(messageHandler.getMessages(), null);

                while (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    try {

                        if (isCancelled()) {
                            break;
                        }
                        object = dataInputStream.readObject();

                        if (object instanceof Message) {
                            messageHandler.addMessage((Message) object);
                            adapter.notifyItemChanged(messageHandler.lastIndex());
                        }



                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                /*Todo
                Handle input stream from server
                 */
                }



            }
            return null;
        }

        private boolean connectToServer() {

            try {

                this.socket = new Socket("192.168.0.199", 6453);

                this.dataInputStream = new ObjectInputStream(socket.getInputStream());
                this.dataOutputStream = new ObjectOutputStream(socket.getOutputStream());

                dataOutputStream.writeObject(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail()));

                return true;

            } catch (Exception e) {

                return false;
            }
        }

        private void sendToServer (Object object) {

            try {
                dataOutputStream.writeObject(object);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void cancelLoop () {
            cancel(true);
        }

        private void closeSocket () {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
