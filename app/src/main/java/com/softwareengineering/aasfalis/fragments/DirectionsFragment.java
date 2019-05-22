package com.softwareengineering.aasfalis.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.activities.MainActivity;
import com.softwareengineering.aasfalis.models.PolylineData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DirectionsFragment extends Fragment {


    private GeoApiContext geoApiContext;
    private EditText locationSearch;
    private MainActivity mainActivity;
    private Button searchButton;
    private Marker currentUserLocationMarker;
    private Polyline polyline;

    public static ArrayList<PolylineData> mPolylineData = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_directions, container, false);
        locationSearch = (EditText) inflate.findViewById(R.id.to_location_text);
        searchButton = (Button) inflate.findViewById(R.id.search_button);
        mainActivity = (MainActivity) getActivity();

        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }

        searchButton.setOnClickListener(view -> calculateDirections());
        return inflate;
    }


    public com.google.maps.model.LatLng searchLocation() {

        String location = locationSearch.getText().toString();
        List<Address> addressList = null;
        com.google.maps.model.LatLng latLng = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                addressList = geocoder.getFromLocationName(location, 1);


            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList == null) {

            } else if (addressList.size() > 0) {
                Address address = addressList.get(0);
                latLng = new com.google.maps.model.LatLng(address.getLatitude(), address.getLongitude());
            }
        }

        return latLng;
    }


    private void calculateDirections() {
        Log.d("Edmir", "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = searchLocation();
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        if (directions != null && destination != null) {
            directions.alternatives(true);
            com.google.maps.model.LatLng start = new com.google.maps.model.LatLng(mainActivity.lastLocation.getLatitude(), mainActivity.lastLocation.getLongitude());
            directions.origin(start);

            Log.d("Edmir", "calculateDirections: destination: " + destination.toString());
            directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    Log.d("Edmir", "onResult: routes: " + result.routes[0].toString());
                    Log.d("Edmir", "onResult: duration: " + result.routes[0].legs[0].duration);
                    Log.d("Edmir", "onResult: distance: " + result.routes[0].legs[0].distance);
                    Log.d("Edmir", "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                    addPolylinesToMap(result);

                    getActivity().runOnUiThread(() -> zoomRoute(polyline.getPoints()));
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
                polyline = mainActivity.mMap.addPolyline(new PolylineOptions()
                        .addAll(newDecodedPath)
                        .width(10)
                        .color(Color.GRAY)
                        .geodesic(true)
                        .clickable(true));
                mPolylineData.add(new PolylineData(polyline, route.legs[0]));

                // highlight the fastest route and adjust camera
                double tempDuration = route.legs[0].duration.inSeconds;
                if(tempDuration < duration){
                    duration = tempDuration;
                    mainActivity.onPolylineClick(polyline);
                    zoomRoute(polyline.getPoints());
                }

                if (getActivity() != null && mainActivity.getSupportActionBar() != null) {
                    hideSoftKeyboard(getActivity());
                    getActivity().onBackPressed();
                    mainActivity.hideActionBar();
                }
            }
        });
    }


    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mainActivity.mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mainActivity.mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
