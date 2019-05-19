package com.softwareengineering.aasfalis.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softwareengineering.aasfalis.R;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class RegisterFragment extends Fragment {
    private Button registerBtn, backFab;
    private EditText emailTxt, passwordTxt, confirmTxt, firstNameTxt, lastNameTxt;
    private FirebaseAuth authUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register, container, false);
        registerBtn = view.findViewById(R.id.registerBtn);
        emailTxt = view.findViewById(R.id.emailTxt);
        passwordTxt = view.findViewById(R.id.passwordTxt);
        confirmTxt = view.findViewById(R.id.confirmTxt);
        firstNameTxt = view.findViewById(R.id.firstNameTxt);
        lastNameTxt = view.findViewById(R.id.lastNameTxt);

        authUser = FirebaseAuth.getInstance();

        passwordTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());


        registerBtn.setOnClickListener(v -> {

            String eMail = emailTxt.getText().toString();
            String passw = passwordTxt.getText().toString();
            String confirmPass = confirmTxt.getText().toString();
            String firstName = firstNameTxt.getText().toString();
            String lastName = lastNameTxt.getText().toString();

            if (isEmailValid(eMail) && isPasswordValid(passw) && passw.equals(confirmPass)
                    && !firstName.isEmpty() && !lastName.isEmpty()) {
                createAccount(eMail, passw);
            } else if (!passw.equals(confirmPass)) {

                new AlertDialog.Builder(getContext(), R.style.com_facebook_auth_dialog)
                        .setTitle("passwords are not matching!")
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else if (eMail.isEmpty() || passw.isEmpty() || confirmPass.isEmpty()
                    || firstName.isEmpty() || lastName.isEmpty()) {

                new AlertDialog.Builder(getContext(), R.style.com_facebook_auth_dialog)
                        .setTitle("Make sure to fill in all the tabs!")
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else if (!isPasswordValid(passw)){

                new AlertDialog.Builder(getContext(), R.style.com_facebook_auth_dialog)
                        .setTitle("Password needs to be at least 6 character long!")
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        return view;
    }

    private void createAccount(String mail, String password) {

        authUser.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = authUser.getCurrentUser();
                        user.sendEmailVerification();

                        Toast.makeText(getContext(), "Verification mail sent!",
                                Toast.LENGTH_LONG).show();

                        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                        navigationView.getMenu().getItem(0).setChecked(false);

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isEmailValid(String mail) {
        return mail.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.matches("[0-9a-zA-Z]{6,16}");
    }


    @Override
    public void onStart() {
        super.onStart();
    }

}
