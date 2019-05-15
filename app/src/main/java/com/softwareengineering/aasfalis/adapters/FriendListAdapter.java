package com.softwareengineering.aasfalis.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.models.User;

import java.util.ArrayList;

public class FriendList extends RecyclerView.Adapter<FriendList.ViewHolder> {

    private ArrayList<User> userArrayList;
    private AdapterView.OnItemClickListener mListener;
    public static int currPos;

    public FriendList(ArrayList<User> users) {

        this.userArrayList = users;
    }

    @NonNull
    @Override
    public FriendList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View listView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);

        return new ViewHolder(listView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull  final ViewHolder holder, int i) {

        holder.userName.setText(userArrayList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;

        public ViewHolder(@NonNull View itemView, AdapterView.OnItemClickListener mListener) {
            super(itemView);

            userName = itemView.findViewById(R.id.rowNameTxt);
        }
    }
}
