package com.softwareengineering.aasfalis.client;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.softwareengineering.aasfalis.models.Friend;
import com.softwareengineering.aasfalis.models.NewFriend;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class Database {

    // Write a message to the database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentName;

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    public String getCurrentName() {
        return currentName;
    }

    public void addUser(String id, String fName, String lName, String eMail, String username, String phone){
        Map<String, Object> user = new HashMap<>();
        user.put("userid", id);
        user.put("firstName", fName);
        user.put("lastName", lName);
        user.put("email", eMail);
        user.put("username", username);
        user.put("phone", phone);

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

    //method for updating the database, could be used to update telephone or username
    public void updateData(){
        // Update one field, creating the document if it does not already exist.
        Map<String, Object> data = new HashMap<>();
        data.put("phone", 12345678);

        db.collection("users").document("test123")
                .set(data, SetOptions.merge());
    }

    public void addFriend (Friend newFriend) {


        DocumentReference docRef = db.collection("users").document(newFriend.geteMail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> friend = new HashMap<>();
                        friend.put("userID", Objects.requireNonNull(document.get("userid")));
                        friend.put("firstName", Objects.requireNonNull(document.get("firstName")));
                        friend.put("lastName", Objects.requireNonNull(document.get("lastName")));
                        friend.put("eMail", Objects.requireNonNull(document.get("email")));

                        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("friends").document(newFriend.geteMail())
                                .set(friend)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Matteo", "Successful add");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Matteo", "Not successful add");
                                    }
                                });

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        DocumentReference docRefe = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        docRefe.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> user = new HashMap<>();
                        user.put("userID", Objects.requireNonNull(document.get("userid")));
                        user.put("firstName", Objects.requireNonNull(document.get("firstName")));
                        user.put("lastName", Objects.requireNonNull(document.get("lastName")));
                        user.put("eMail", Objects.requireNonNull(document.get("email")));

                        db.collection("users").document(newFriend.geteMail()).collection("friends").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Matteo", "Successful add");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Matteo", "Not successful add");
                                    }
                                });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void addRequest (NewFriend firendRequest) {

        DocumentReference docRef = db.collection("users").document(firendRequest.getSenderMail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> request = new HashMap<>();
                        request.put("userID", document.get("userid"));
                        request.put("firstName", document.get("firstName"));
                        request.put("lastName", document.get("lastName"));
                        request.put("eMail", document.get("email"));

                        db.collection("users").document(firendRequest.getRecieverMail()).collection("request").document(firendRequest.getSenderMail())
                                .set(request)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Matteo", "Successful add");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Matteo", "Not successful add");
                                    }
                                });

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void removeRequest (Friend friend) {

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("request").document(friend.geteMail())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}