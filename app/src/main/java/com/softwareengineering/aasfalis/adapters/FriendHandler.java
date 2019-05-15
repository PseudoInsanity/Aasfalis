package com.softwareengineering.aasfalis.models;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Friend {

    private ArrayList<Friend> friendList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Friend () {

        friendList = new ArrayList<>();

        db.collection("friendList")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot querySnapshot: queryDocumentSnapshots) {
                            friendList.add(new Fr)
                        }
                    }
                });
    }
}
