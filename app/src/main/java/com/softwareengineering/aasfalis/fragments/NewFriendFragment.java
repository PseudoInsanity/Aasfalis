package com.softwareengineering.aasfalis.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.client.Database;
import com.softwareengineering.aasfalis.models.NewFriend;

public class NewFriendFragment extends DialogFragment {

    private EditText friendEmail;
    private Button sendReq;
    private Database database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_new_friend, container, false);

        friendEmail = view.findViewById(R.id.friendEmailTxt);
        sendReq = view.findViewById(R.id.sendFriendReqBtn);
        database = new Database();

        sendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fEmail = friendEmail.getText().toString();
                if (isEmailValid(fEmail)) {
                    database.addRequest(new NewFriend(FirebaseAuth.getInstance().getCurrentUser().getEmail(), fEmail));
                }
            }
        });
        return view;
    }

    private boolean isEmailValid(String mail) {
        return mail.contains("@");
    }
}
