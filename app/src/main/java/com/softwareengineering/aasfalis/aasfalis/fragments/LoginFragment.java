package com.softwareengineering.aasfalis.aasfalis.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.aasfalis.activities.MainActivity;

import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    EditText username, password;
    Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_login, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        username = inflate.findViewById(R.id.editTextUsername);
        password = inflate.findViewById(R.id.editTextPassword);

        loginButton = inflate.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = username.getText().toString();
                String pass = password.getText().toString();

                if (isEmailValid(email) && isPasswordValid(pass)) {


                    loginUser(email, pass, view);
                }
            }
        });

        return inflate;
    }

    private void loginUser(String mail, String password, final View v) {
        firebaseAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Samin", "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user.isEmailVerified()) {
                                Intent intent = new Intent(v.getContext(), MainActivity.class);
                                startActivity(intent);

                            } else {

                                Toast.makeText(getContext(), "Please verify your email!",
                                        Toast.LENGTH_LONG).show();
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private boolean isEmailValid(String mail) {
        return mail.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.matches("[0-9a-zA-Z]{6,16}");
    }

}
