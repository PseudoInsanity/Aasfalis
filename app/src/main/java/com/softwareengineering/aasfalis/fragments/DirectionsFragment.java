package com.softwareengineering.aasfalis.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.maps.GeoApiContext;
import com.softwareengineering.aasfalis.R;




public class DirectionsFragment extends Fragment {

    private GeoApiContext geoApiContext;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_directions, container, false);

        return inflate;
    }






}
