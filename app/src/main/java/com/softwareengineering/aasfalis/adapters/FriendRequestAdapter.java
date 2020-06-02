package com.softwareengineering.aasfalis.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.client.Database;
import com.softwareengineering.aasfalis.client.FriendService;
import com.softwareengineering.aasfalis.models.Friend;

import java.util.ArrayList;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {
    private ArrayList<Friend> userArrayList;
    private FriendService friendHandler;
    private Database database;
    private AdapterView.OnItemClickListener mListener;
    public static int currPos;


    public FriendRequestAdapter(ArrayList<Friend> users) {

        this.userArrayList = users;
        friendHandler = new FriendService();
        database = new Database();
    }

    @NonNull
    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View listView = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row, parent, false);

        return new FriendRequestAdapter.ViewHolder(listView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull  final FriendRequestAdapter.ViewHolder holder, int i) {

        holder.userName.setText(userArrayList.get(i).getFirstName() +  " " + userArrayList.get(i).getLastName());

        holder.yBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendHandler.acceptedRequest(userArrayList.get(i));
                friendHandler.fillFriendList();
                friendHandler.fillRequestList();
                notifyDataSetChanged();
            }
        });

        holder.nBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendHandler.declineRequest(userArrayList.get(i));
                friendHandler.fillRequestList();
                notifyDataSetChanged();
            }
        });


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
        private Button yBtn, nBtn;

        public ViewHolder(@NonNull View itemView, AdapterView.OnItemClickListener mListener) {
            super(itemView);

            userName = itemView.findViewById(R.id.rowNameTxt);
            yBtn = itemView.findViewById(R.id.acceptBtn);
            nBtn = itemView.findViewById(R.id.denyBtn);

        }

    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListener = listener;
    }
}
