package com.softwareengineering.aasfalis.adapters;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.softwareengineering.aasfalis.models.Friend;

import java.util.ArrayList;

public class FriendHandler {

    private static ArrayList<Friend> friendList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void fillList(String eMail) {

        friendList = new ArrayList<>();

        db.collection("users").document(eMail).collection("friends")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot q: queryDocumentSnapshots) {
                            friendList.add(new Friend(q.getString("firstName"), q.getString("lastName"), q.getString("userID")));
                        }
                    }
                });

    }

    public ArrayList<Friend> getFriendList() {
        return friendList;
    }

    public void clearList () {

        friendList = new ArrayList<>();
    }
}
