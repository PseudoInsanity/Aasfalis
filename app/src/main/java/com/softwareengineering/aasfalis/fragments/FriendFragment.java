package com.softwareengineering.aasfalis.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.adapters.TabAdapter;


public class FriendFragment extends Fragment {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friend, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getFragmentManager());
        adapter.addFragment(new CurrentFriendFragment(), "Friends");
        adapter.addFragment(new FriendRequestFragment(), "Requests");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


}
