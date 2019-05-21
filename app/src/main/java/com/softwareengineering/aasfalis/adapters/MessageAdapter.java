package com.softwareengineering.aasfalis.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.models.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messages;
    private AdapterView.OnItemClickListener mListener;

    public MessageAdapter(ArrayList<Message> messageArrayList) {

        this.messages = messageArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View listView;

        if (messages.get(i).getFrom().equals("Matteo")) {

            listView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_sent, viewGroup, false);
            return new ViewHolderSend(listView);
        } else {

            listView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_received, viewGroup, false);
            return new ViewHolderReceiver(listView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (messages.get(i).getFrom().equals("Matteo")) {

            sendLayout((ViewHolderSend) viewHolder, i);

        } else {

            receiveLayout((ViewHolderReceiver) viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private void sendLayout (ViewHolderSend holder, int pos) {

        holder.userName.setText(messages.get(pos).getFrom());
        holder.userTxt.setText(messages.get(pos).getMessage());
        holder.time.setText(messages.get(pos).getTime());
    }

    private void receiveLayout (ViewHolderReceiver holder, int pos) {

        holder.friendName.setText(messages.get(pos).getFrom());
        holder.friendTxt.setText(messages.get(pos).getMessage());
        holder.friendTime.setText(messages.get(pos).getTime());
    }


    class ViewHolderSend extends RecyclerView.ViewHolder {

        TextView userName, userTxt, time;
        ViewHolderSend(View listView) {
            super(listView);

            userName = listView.findViewById(R.id.user_name);
            userTxt = listView.findViewById(R.id.user_msg);
            time = listView.findViewById(R.id.user_time);
        }
    }

    class ViewHolderReceiver extends RecyclerView.ViewHolder {

        TextView friendName, friendTxt, friendTime;
        ViewHolderReceiver(View listView) {
            super(listView);

            friendName = listView.findViewById(R.id.friend_name);
            friendTxt = listView.findViewById(R.id.friend_msg);
            friendTime = listView.findViewById(R.id.friend_time);
        }
    }
}
