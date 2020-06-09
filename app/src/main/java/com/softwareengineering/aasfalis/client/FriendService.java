package com.softwareengineering.aasfalis.client;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.aasfalis.models.Friend;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FriendService extends Service {

    public static final int notify = 1000;
    int count = 0;
    private Handler handler = new Handler();
    private Timer timer = null;
    private static ArrayList<Friend> friendList;
    private static ArrayList<Friend> requestList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Database database;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (timer != null)
            timer.cancel();
        else
            timer = new Timer();
        timer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();    //For Cancel Timer
    }

    class TimeDisplay extends TimerTask {
        @Override
        public void run() {

            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        fillFriendList();
                        fillRequestList();
                    }
                }
            });

        }

    }



    public void fillFriendList() {

        friendList = new ArrayList<>();

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("friends")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                            friendList.add(new Friend(q.getString("firstName"), q.getString("lastName"), q.getString("eMail"), q.getString("roomID")));
                        }
                    }
                });
    }

    public void fillRequestList() {

        requestList = new ArrayList<>();

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("request")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                            requestList.add(new Friend(q.getString("firstName"), q.getString("lastName"), q.getString("eMail"), ""));
                        }
                    }
                });
    }

    public ArrayList<Friend> getFriendList() {
        return friendList;
    }

    public ArrayList<Friend> getRequestList() {
        return requestList;
    }

    public boolean acceptedRequest(Friend friend) {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            database = new Database();
            database.addFriend(friend);
            database.removeRequest(friend);
            requestList.remove(friend);
            return true;
        }
        return false;
    }

    public boolean declineRequest(Friend friend) {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            database = new Database();
            database.removeRequest(friend);
            requestList.remove(friend);
            return true;
        }
        return false;
    }
}
