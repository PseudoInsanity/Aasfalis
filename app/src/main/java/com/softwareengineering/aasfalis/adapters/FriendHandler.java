package com.softwareengineering.aasfalis.adapters;

import android.os.AsyncTask;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.aasfalis.client.Database;
import com.softwareengineering.aasfalis.models.Friend;

import java.util.ArrayList;

public class FriendHandler extends AsyncTask<Void, Void, Void> {

    private static ArrayList<Friend> friendList;
    private static ArrayList<Friend> requestList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Database database;

    public void fillFriendList() {

        friendList = new ArrayList<>();

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("friends")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                            friendList.add(new Friend(q.getString("firstName"), q.getString("lastName"), q.getString("eMail")));
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
                            requestList.add(new Friend(q.getString("firstName"), q.getString("lastName"), q.getString("eMail")));
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


    @Override
    protected Void doInBackground(Void... voids) {

        while (FirebaseAuth.getInstance().getCurrentUser() != null) {
            try {
                fillFriendList();
                fillRequestList();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
