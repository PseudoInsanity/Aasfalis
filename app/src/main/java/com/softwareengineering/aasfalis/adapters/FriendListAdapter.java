package com.softwareengineering.aasfalis.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.fragments.MessageFragment;
import com.softwareengineering.aasfalis.models.Friend;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private ArrayList<Friend> userArrayList;
    private AdapterView.OnItemClickListener mListener;
    public static int currPos;
    private Context context;

    public FriendListAdapter(ArrayList<Friend> users, Context c) {

        this.context = c;
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
        holder.userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                MessageFragment messageFragment = new MessageFragment();
                messageFragment.setArguments(userArrayList.get(i));
                messageFragment.show(fragmentManager, "MessageFragment");
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
        private CardView userCard;

        public ViewHolder(@NonNull View itemView, AdapterView.OnItemClickListener mListener) {
            super(itemView);

            userName = itemView.findViewById(R.id.rowNameTxt);
            userCard = itemView.findViewById(R.id.userCard);
        }
    }
}
