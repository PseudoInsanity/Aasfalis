package com.softwareengineering.aasfalis.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.adapters.FriendHandler;
import com.softwareengineering.aasfalis.client.ClientService;
import com.softwareengineering.aasfalis.client.Database;
import com.softwareengineering.aasfalis.fragments.DirectionsFragment;
import com.softwareengineering.aasfalis.fragments.FriendFragment;
import com.softwareengineering.aasfalis.fragments.LoginFragment;
import com.softwareengineering.aasfalis.fragments.MessageFragment;
import com.softwareengineering.aasfalis.fragments.ProfileFragment;
import com.softwareengineering.aasfalis.models.Friend;
import com.softwareengineering.aasfalis.models.Message;
import com.softwareengineering.aasfalis.models.PolylineData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.softwareengineering.aasfalis.client.ClientService.breakLoop;
import static com.softwareengineering.aasfalis.client.ClientService.forceOut;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static ArrayList<Message> messages = new ArrayList<>();
    private static final int REQUEST_USER_LOCATION_CODE = 99;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private int mToolbarHeight, mAnimDuration = 600;

    public GoogleMap mMap;
    public Location lastLocation;
    public Marker mSelectedMarker = null;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Marker currentUserLocationMarker;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ValueAnimator mVaActionBar;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private GeoApiContext geoApiContext;
    private ArrayList<PolylineData> mPolylineData = new ArrayList<>();
    private FriendHandler friendHandler;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private String address;
    private Polyline polyline;
    private TextView usernameNav, emailNav;
    private String currentUserEmail;

    private ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyDs7FtleHttwUBwTngT1PG8sOZeoc5O_vQ");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> findPlace());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

       // usernameNav = (TextView) headerView.findViewById(R.id.username_nav_header);
       // emailNav = findViewById(R.id.email_nav_header);

        checkUserLocationPermission();

        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                Database database = new Database();
                database.setCurrentName(currentUserEmail);
                friendHandler = new FriendHandler();
                friendHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                breakLoop();
                forceOut();
                startService(new Intent(this, ClientService.class));


                //usernameNav.setText(profileFragment.getPublicUserName(currentUserEmail));
            }
        } catch (NullPointerException nx) {
            Log.d("Edmir", "NullPointer nx: " + nx.getMessage());
        }

        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        getCallingActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        //View fragmentRootView = loginFragment.getView();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (backStackEntryCount == 1) {
            showActionBar();
            fab.show();
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
        navigationView.getMenu().getItem(0).setChecked(false);
        addListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectDrawerItem(item);
        return true;
    }

    private void selectDrawerItem(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        String tag = null;

        switch (id) {
            case R.id.nav_profile:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    fragmentClass = ProfileFragment.class;
                    tag = "ProfileFragment";
                    hideActionBar();
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mMap.setOnMapClickListener(null);
                } else {
                    fragmentClass = LoginFragment.class;
                    tag = "LoginFragment";
                    hideActionBar();
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mMap.setOnMapClickListener(null);
                }
                break;
            case R.id.nav_friends:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    fragmentClass = FriendFragment.class;
                    tag = "FriendFragment";
                    hideActionBar();
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else {
                    fragmentClass = LoginFragment.class;
                    tag = "LoginFragment";
                    hideActionBar();
                    Toast.makeText(this, "You must log in to see the friend list", Toast.LENGTH_SHORT).show();
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                break;
          /*  case R.id.nav_send:
                fragmentClass = MessageFragment.class;
                tag = "MessageFrag";
                hideActionBar();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                break;*/
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fab.hide();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.map, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();

        //  mMap.setOnMapClickListener(null);
        navigationView.getMenu().getItem(0).setChecked(true);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnPolylineClickListener(this::onPolylineClick);
        // mMap.setOnMapClickListener((GoogleMap.OnMapClickListener) this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            new Thread(() -> buildGoogleApiClient()).start();

            mMap.setMyLocationEnabled(true);

        }
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        addListener();

    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)             // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_USER_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void showActionBar() {
        if (mVaActionBar != null && mVaActionBar.isRunning()) {
            // we are already animating a transition - block here
            return;
        }

        // restore `Toolbar's` height
        mVaActionBar = ValueAnimator.ofInt(0, mToolbarHeight);
        mVaActionBar.addUpdateListener(animation -> {
            // update LayoutParams
            ((AppBarLayout.LayoutParams) toolbar.getLayoutParams()).height
                    = (Integer) animation.getAnimatedValue();
            toolbar.requestLayout();
        });

        mVaActionBar.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                if (getSupportActionBar() != null) { // sanity check
                    getSupportActionBar().show();
                }
            }
        });

        mVaActionBar.setDuration(mAnimDuration);
        mVaActionBar.start();

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    public void hideActionBar() {
        // initialize `mToolbarHeight`
        if (mToolbarHeight == 0) {
            mToolbarHeight = toolbar.getHeight();
        }

        if (mVaActionBar != null && mVaActionBar.isRunning()) {
            // we are already animating a transition - block here
            return;
        }

        // animate `Toolbar's` height to zero.
        mVaActionBar = ValueAnimator.ofInt(mToolbarHeight, 0);
        mVaActionBar.addUpdateListener(animation -> {
            // update LayoutParams
            ((AppBarLayout.LayoutParams) toolbar.getLayoutParams()).height
                    = (Integer) animation.getAnimatedValue();
            toolbar.requestLayout();
        });

        mVaActionBar.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (getSupportActionBar() != null) { // sanity check
                    getSupportActionBar().hide();
                }
            }
        });

        mVaActionBar.setDuration(mAnimDuration);
        mVaActionBar.start();

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    private void addListener() {
        mMap.setOnMapClickListener(latLng -> {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar.isShowing()) {
                hideActionBar();
            } else {
                showActionBar();
            }
        });

        mMap.setOnInfoWindowClickListener(marker -> {
            if (marker.getTitle().contains(address)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Open Google Maps?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            String latitude = String.valueOf(marker.getPosition().latitude);
                            String longitude = String.valueOf(marker.getPosition().longitude);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            try {
                                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                            } catch (NullPointerException e) {
                                Log.e("Edmir", "onClick: NullPointerException: Couldn't open map." + e.getMessage());
                                Toast.makeText(getApplicationContext(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                            }

                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                if (marker.getSnippet().equals("This is you")) {
                    marker.hideInfoWindow();
                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(marker.getSnippet())
                            .setCancelable(true)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                resetSelectedMarker();
                                mSelectedMarker = marker;
                                dialog.dismiss();
                            })
                            .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    private void openDirectionsFragment() {
        Fragment fragment = new DirectionsFragment();
        String tag = "DirectionsFragment";

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.map, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
        mMap.setOnMapClickListener(null);
        hideActionBar();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        System.out.println("USER: " + FirebaseAuth.getInstance().getCurrentUser());
        stopService(new Intent(this, ClientService.class));
    }

    public void onPolylineClick(Polyline polyline) {
        for (PolylineData polylineData : mPolylineData) {
            Log.d("Edmir", "onPolylineClick: toString: " + polylineData.toString());
            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(ContextCompat.getColor(this, R.color.headings));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(endLocation.latitude, endLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    address = addresses.get(0).getAddressLine(0);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(endLocation)
                            .title(address)
                            .snippet("Duration: " + polylineData.getLeg().duration)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    mTripMarkers.add(marker);

                    marker.showInfoWindow();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                polylineData.getPolyline().setColor(ContextCompat.getColor(this, R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }

        }
        hideActionBar();
    }

    public void resetSelectedMarker() {
        if (mSelectedMarker != null) {
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }

    public void removeTripMarkers() {
        for (Marker marker : mTripMarkers) {
            marker.remove();
        }
    }

    public void showMsgFrag(Friend friend) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(friend);
        messageFragment.show(fragmentManager, "MessageFragment");
    }

    private void findPlace() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        // Start the autocomplete intent.
        hideActionBar();
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Place place = Autocomplete.getPlaceFromIntent(data);
                calculateDirections(place.getAddress());

                Log.d("Edmir", "Place: " + place.getName() + ", " + place.getLatLng() + ", " + place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d("Edmir", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void calculateDirections(String address) {
        Log.d("Edmir", "calculateDirections: calculating directions.");
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        if (address != null && lastLocation != null) {
            directions.alternatives(true);
            com.google.maps.model.LatLng start = new com.google.maps.model.LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

            directions.origin(start);
            //Log.d("Edmir", "calculateDirections: destination: " + address);
            directions.destination(address);
            directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    //   Log.d("Edmir", "onResult: routes: " + result.routes[0].toString());
                    //   Log.d("Edmir", "onResult: duration: " + result.routes[0].legs[0].duration);
                    //   Log.d("Edmir", "onResult: distance: " + result.routes[0].legs[0].distance);
                    //   Log.d("Edmir", "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                    addPolylinesToMap(result);

                    //runOnUiThread(() -> zoomRoute(polyline.getPoints()));
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e("Edmir", "onFailure: " + e.getMessage());
                }
            });
        }
    }

    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d("Edmir", "run: result routes: " + result.routes.length);
            if (mPolylineData.size() > 0) {
                for (PolylineData polylineData : mPolylineData) {
                    polylineData.getPolyline().remove();
                }
                mPolylineData.clear();
                mPolylineData = new ArrayList<>();
            }
            double duration = 999999999;
            for (DirectionsRoute route : result.routes) {
                Log.d("Edmir", "run: leg: " + route.legs[0].toString());
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                List<LatLng> newDecodedPath = new ArrayList<>();

                // This loops through all the LatLng coordinates of ONE polyline.

                for (com.google.maps.model.LatLng latLng : decodedPath) {
                    newDecodedPath.add(new LatLng(
                            latLng.lat,
                            latLng.lng
                    ));
                }
                polyline = mMap.addPolyline(new PolylineOptions()
                        .addAll(newDecodedPath)
                        .width(10)
                        .color(Color.GRAY)
                        .geodesic(true)
                        .clickable(true));
                mPolylineData.add(new PolylineData(polyline, route.legs[0]));

                // highlight the fastest route and adjust camera
                double tempDuration = route.legs[0].duration.inSeconds;
                if (tempDuration < duration) {
                    duration = tempDuration;
                    onPolylineClick(polyline);
                    zoomRoute(polyline.getPoints());
                }

            }

        });
    }


    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 400;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
