package com.softwareengineering.aasfalis.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.adapters.FriendHandler;
import com.softwareengineering.aasfalis.adapters.FriendListAdapter;
import com.softwareengineering.aasfalis.models.Friend;

import java.util.ArrayList;


public class FriendFragment extends Fragment {

    private FriendHandler friendHandler;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FriendListAdapter adapter;
    private FloatingActionButton newFriendFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_friend, container, false);

        friendHandler = new FriendHandler();
        friendHandler.fillList(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            buildRecyclerView(view);
        }

        newFriendFab = (FloatingActionButton) view.findViewById(R.id.newFriendBtn);
        newFriendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                NewFriendFragment newFriendFragment = new NewFriendFragment();
                newFriendFragment.show(fragmentManager, "NewFriendFragment");
            }
        });
        return view;
    }

    private void buildRecyclerView(final View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.userRecycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FriendListAdapter(friendHandler.getFriendList());
        recyclerView.setAdapter(adapter);
    }
}
