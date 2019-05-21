package com.softwareengineering.aasfalis.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.activities.Database;

import static android.support.constraint.Constraints.TAG;

public class ProfileFragment extends Fragment {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    //Database database = new Database();
    private String name;
    private String currentUserID;
    private TextView userFirstName, userLastName, userEmail, address, currentUsername;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userFirstName = view.findViewById(R.id.firstName);
        userLastName = view.findViewById(R.id.lastName);
        userEmail = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        currentUsername = view.findViewById(R.id.user_name);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getFirstName(currentUserID);
        getLastName(currentUserID);
        getEmail(currentUserID);
        //getAddress(currentUserID);

        return view;
    }

    public void getFirstName(String currentUserID){
        DocumentReference docRef = firestore.collection("users").document(currentUserID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            DocumentSnapshot documentSnapshot;
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        Log.d(TAG, "DocumentSnapshot data: " + firstName);
                        userFirstName.setText(firstName);
                    } else {
                        Log.d(TAG, "No such document");
                    }
            }
        });
    }

    public void getLastName(String currentUserID){
        DocumentReference docRef = firestore.collection("users").document(currentUserID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            DocumentSnapshot documentSnapshot;
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String lastName = documentSnapshot.getString("lastName");
                    Log.d(TAG, "DocumentSnapshot data: " + lastName);
                    userLastName.setText(lastName);
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }

    public void getEmail(String currentUserID){
        DocumentReference docRef = firestore.collection("users").document(currentUserID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            DocumentSnapshot documentSnapshot;
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String email = documentSnapshot.getString("email");
                    Log.d(TAG, "DocumentSnapshot data: " + email);
                    userEmail.setText(email);
                    currentUsername.setText(email);
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }

    public void getAddress(String currentUserID){
        DocumentReference docRef = firestore.collection("users").document(currentUserID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            DocumentSnapshot documentSnapshot;
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String firstName = documentSnapshot.getString("firstName");
                    Log.d(TAG, "DocumentSnapshot data: " + firstName);
                    userFirstName.setText(firstName);
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }

    public String getUserFirstName(){


        return name;
    }

}
