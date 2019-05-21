package com.softwareengineering.aasfalis.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.models.Friend;
import com.softwareengineering.aasfalis.models.User;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private ArrayList<Friend> userArrayList;
    private AdapterView.OnItemClickListener mListener;
    public static int currPos;

    public FriendListAdapter(ArrayList<Friend> users) {

        this.userArrayList = users;
    }

    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View listView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);

        return new ViewHolder(listView, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull  final ViewHolder holder, int i) {

        holder.userName.setText(userArrayList.get(i).getFirstName() +  " " + userArrayList.get(i).getLastName());
    }

    @Override
    public int getItemCount() {
        if (userArrayList.size() > 0) {
            return userArrayList.size();
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userName;

        public ViewHolder(@NonNull View itemView, AdapterView.OnItemClickListener mListener) {
            super(itemView);

            userName = itemView.findViewById(R.id.rowNameTxt);
        }
    }
}
