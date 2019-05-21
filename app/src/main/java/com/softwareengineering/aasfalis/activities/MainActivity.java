package com.softwareengineering.aasfalis.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;
import android.os.Build;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.adapters.FriendHandler;
import com.softwareengineering.aasfalis.client.ClientService;
import com.softwareengineering.aasfalis.client.Database;
import com.softwareengineering.aasfalis.client.ServerReceiver;
import com.softwareengineering.aasfalis.fragments.DirectionsFragment;
import com.softwareengineering.aasfalis.fragments.FriendFragment;
import com.softwareengineering.aasfalis.fragments.LoginFragment;
import com.softwareengineering.aasfalis.fragments.ProfileFragment;
import com.softwareengineering.aasfalis.fragments.RegisterFragment;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ValueAnimator mVaActionBar;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private GeoApiContext geoApiContext;

    // holds the original Toolbar height.
    // this can also be obtained via (an)other method(s)
    private int mToolbarHeight, mAnimDuration = 600/* milliseconds */;

    private static final int REQUEST_USER_LOCATION_CODE = 99;

    //Fragments
    private LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("LoginFragment");
    private ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");
    private DirectionsFragment directionsFragment = (DirectionsFragment) getSupportFragmentManager().findFragmentByTag("DirectionsFragment");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> openDirectionsFragment());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        checkUserLocationPermission();

        stopService(new Intent(this, ClientService.class));
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startService(new Intent(this, ClientService.class));
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onResume () {
        super.onResume();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        //View fragmentRootView = loginFragment.getView();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
       /* if (fab.isOrWillBeHidden() && loginFragment != null && !loginFragment.isVisible()) {
            fab.show();
            showActionBar();
        } else if (profileFragment != null && profileFragment.isVisible()) {
            showActionBar();
            fab.show();
        } else if (directionsFragment != null && directionsFragment.isVisible()) {
            showActionBar();
        } */
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
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
                }
                break;
            case R.id.nav_friends:
                fragmentClass = FriendFragment.class;
                tag = "FriendFragment";
                hideActionBar();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
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

        mMap.setOnMapClickListener(null);
        navigationView.getMenu().getItem(0).setChecked(true);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (directionsFragment != null) {
            mMap.setOnPolylineClickListener(polyline -> directionsFragment.onPolylineClick(polyline));
        }
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


    private void showActionBar() {
        if (mVaActionBar != null && mVaActionBar.isRunning()) {
            // we are already animating a transition - block here
            return;
        }

        // restore `Toolbar's` height
        mVaActionBar = ValueAnimator.ofInt(0, mToolbarHeight);
        mVaActionBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // update LayoutParams
                ((AppBarLayout.LayoutParams) toolbar.getLayoutParams()).height
                        = (Integer) animation.getAnimatedValue();
                toolbar.requestLayout();
            }
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


    private void hideActionBar() {
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
        mVaActionBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // update LayoutParams
                ((AppBarLayout.LayoutParams) toolbar.getLayoutParams()).height
                        = (Integer) animation.getAnimatedValue();
                toolbar.requestLayout();
            }
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
        stopService(new Intent(this, ClientService.class));

        directions.alternatives(true);
        directions.origin(new com.google.maps.model.LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));

        Log.d("Edmir", "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("Edmir", "onResult: routes: " + result.routes[0].toString());
                Log.d("Edmir", "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("Edmir", "onFailure: " + e.getMessage());

            }
        });
    }
}
