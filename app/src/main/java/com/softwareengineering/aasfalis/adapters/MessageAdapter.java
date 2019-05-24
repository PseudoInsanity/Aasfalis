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
import com.softwareengineering.aasfalis.client.Database;
import com.softwareengineering.aasfalis.models.Friend;
import com.softwareengineering.aasfalis.models.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messages;
    private Friend currentFriend;
    private MessageHandler messageHandler;
    private AdapterView.OnItemClickListener mListener;

    public MessageAdapter(ArrayList<Message> messageArrayList, Friend currentFriend) {

        this.messageHandler = new MessageHandler();
        this.messages = messageArrayList;
        this.currentFriend = currentFriend;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View listView;

        if (messages.get(i).getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

            listView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_sent, viewGroup, false);
            return new ViewHolderSend(listView);
        } else {

            listView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_received, viewGroup, false);
            return new ViewHolderReceiver(listView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        if ((messages.get(i).getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()) && messages.get(i).getFrom().equals(currentFriend.geteMail())) ||
                (messages.get(i).getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()) && messages.get(i).getTo().equals(currentFriend.geteMail()))) {
            if (messages.get(i).getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

                sendLayout((ViewHolderSend) viewHolder, i);
                messageHandler.addMessage(me);

            } else {

                receiveLayout((ViewHolderReceiver) viewHolder, i);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private void sendLayout (ViewHolderSend holder, int pos) {

        holder.userName.setText(messages.get(pos).getUsername());
        holder.userTxt.setText(messages.get(pos).getMessage());
        holder.time.setText(messages.get(pos).getTime());
    }

    private void receiveLayout (ViewHolderReceiver holder, int pos) {

        holder.friendName.setText(messages.get(pos).getUsername());
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
