package com.softwareengineering.aasfalis.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment {

    private static final int REQUEST_USER_LOCATION_CODE = 99;
    private FirebaseAuth firebaseAuth;

    CallbackManager callbackManager;

    private EditText username, password;
    private Button loginButton;
    private LoginButton fbLoginButton;
    private TextView txtName, txtEmail;
    AppCompatCheckBox checkBox;

    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

    private static final String EMAIL = "email";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_login, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        username = inflate.findViewById(R.id.editTextUsername);
        password = inflate.findViewById(R.id.editTextPassword);
        checkBox = inflate.findViewById(R.id.checkbox);

        callbackManager = CallbackManager.Factory.create();

        checkUserInternetPermission();
        fbLoginButton = (LoginButton) inflate.findViewById(R.id.login_button);
        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL));
        fbLoginButton.setFragment(this);
        checkLoginStatus();

        Collection<String> readPermissions = new ArrayList<>();
        readPermissions.add("public_profile");
        readPermissions.add("email");
        readPermissions.add("user_birthday");
        Collection<String> publishPermissions = new ArrayList<>();
        publishPermissions.add("publish_actions");

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                if(value){
                    //show password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    //hide password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });



        // Callback registration for fb
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Edmir", "Success");
                LoginManager.getInstance().logInWithPublishPermissions(LoginFragment.this, Arrays.asList("public_profile", "user_friends", "email"));
                Toast.makeText(getContext(), "Facebook login success!",
                        Toast.LENGTH_LONG).show();
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.map, fragment);
                transaction.addToBackStack(null);
                transaction.commit();



            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "Cancel",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getContext(), "Error",
                        Toast.LENGTH_LONG).show();
                Log.d("Samin", exception.getMessage());
                Log.d("Edmir", exception.getMessage());
            }
        });


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


    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken == null) {

            }
        }
    };

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkUserInternetPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.INTERNET)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET}, REQUEST_USER_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET}, REQUEST_USER_LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_USER_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                        checkUserInternetPermission();
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }

        }
    }

    private void loadUserProfile(AccessToken newAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    // txtEmail.setText(email);
                    // txtName.setText(first_name + " " + last_name);
                    // RequestOptions requestOptions = new RequestOptions();
                    // requestOptions.dontAnimate();

                    //Glide.with(LoginFragment.this).load(image_url).into();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();


    }

    private void checkLoginStatus() {
        if (isLoggedIn) {
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }
}
