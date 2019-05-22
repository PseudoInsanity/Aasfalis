package com.softwareengineering.aasfalis.client;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.softwareengineering.aasfalis.models.NewFriend;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class Database {

    // Write a message to the database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addUser(String id, String fName, String lName, String eMail){
        Map<String, Object> user = new HashMap<>();
        user.put("userid", id);
        user.put("firstName", fName);
        user.put("lastName", lName);
        user.put("email", eMail);

        db.collection("users").document(eMail)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    public void readData(){
        DocumentReference docRef = db.collection("cities").document("LA");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    //method for updating the database, could be used to update telephone or username
    public void updateData(){
        // Update one field, creating the document if it does not already exist.
        Map<String, Object> data = new HashMap<>();
        data.put("phone", 12345678);

        db.collection("users").document("test123")
                .set(data, SetOptions.merge());
    }

    public void addFriend (NewFriend newFriend) {

        Map<String, Object> friend = new HashMap<>();
        friend.put("eMail", newFriend.getReveiverMail());

        db.collection("users").document(newFriend.getSenderMail()).collection("friends").document(newFriend.getReveiverMail())
                .set(friend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        friend = new HashMap<>();
        friend.put("eMail", newFriend.getSenderMail());

        db.collection("users").document(newFriend.getReveiverMail()).collection("friends").document(newFriend.getSenderMail())
                .set(friend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}