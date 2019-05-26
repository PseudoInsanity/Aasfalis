package com.softwareengineering.aasfalis.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.adapters.FriendHandler;
import com.softwareengineering.aasfalis.adapters.FriendListAdapter;
import com.softwareengineering.aasfalis.adapters.FriendRequestAdapter;

import java.util.Objects;

public class FriendRequestFragment extends Fragment {

    private FriendHandler friendHandler;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FriendRequestAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        friendHandler = new FriendHandler();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            buildRecyclerView(view);
        }

        adapter.notifyDataSetChanged();
        return view;
    }

    private void buildRecyclerView(final View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.requestRecycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FriendRequestAdapter(friendHandler.getRequestList());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Objects.requireNonNull(getFragmentManager()).beginTransaction().detach(this).attach(this).commit();
        }
    }
}
