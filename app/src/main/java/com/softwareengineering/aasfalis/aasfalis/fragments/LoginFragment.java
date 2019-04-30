package com.softwareengineering.aasfalis.aasfalis.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.aasfalis.R;

public class LoginFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    EditText username, password;
    Button signinButton, registerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState){

        View inflate = inflater.inflate(R.layout.fragment_login,container,false);
        firebaseAuth = FirebaseAuth.getInstance();
        username = inflate.findViewById(R.id.username);
        password = inflate.findViewById(R.id.password);

        signinButton = inflate.findViewById(R.id.signinButon);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
}
