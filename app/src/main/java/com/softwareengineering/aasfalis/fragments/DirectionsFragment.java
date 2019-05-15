package com.softwareengineering.aasfalis.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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


public class DirectionsFragment extends Fragment implements
        GoogleMap.OnPolylineClickListener {


    private GeoApiContext geoApiContext;
    private EditText locationSearch;
    private MainActivity mainActivity;
    private Button searchButton;
    public ArrayList<PolylineData> mPolylineData = new ArrayList<>();

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

            } else {
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

        directions.alternatives(true);
        directions.origin(new com.google.maps.model.LatLng(mainActivity.lastLocation.getLatitude(), mainActivity.lastLocation.getLongitude()));

        Log.d("Edmir", "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("Edmir", "onResult: routes: " + result.routes[0].toString());
                Log.d("Edmir", "onResult: duration: " + result.routes[0].legs[0].duration);
                Log.d("Edmir", "onResult: distance: " + result.routes[0].legs[0].distance);
                Log.d("Edmir", "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("Edmir", "onFailure: " + e.getMessage());

            }
        });
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
                Polyline polyline = mainActivity.mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polyline.setClickable(true);
                mPolylineData.add(new PolylineData(polyline, route.legs[0]));

            }
        });
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        for (PolylineData polylineData : mPolylineData) {
            Log.d("Edmir", "onPolylineClick: toString: " + polylineData.toString());
            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.headings));
                polylineData.getPolyline().setZIndex(1);
            } else {
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

}
