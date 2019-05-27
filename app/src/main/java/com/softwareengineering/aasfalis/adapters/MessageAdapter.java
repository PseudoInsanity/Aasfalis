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
import com.softwareengineering.aasfalis.models.Friend;
import com.softwareengineering.aasfalis.models.Message;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messages;
    private Friend currentFriend;
    private MessageHandler messageHandler;
    private AdapterView.OnItemClickListener mListener;

    public MessageAdapter(ArrayList<Message> messageArrayList, Friend currFr) {

        messageHandler = new MessageHandler();
        messages = messageArrayList;
        currentFriend = currFr;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View listView;

        if (messages.get(i).getFrom().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())) {

            listView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_sent, viewGroup, false);
            return new ViewHolderSend(listView);
        } else {

            listView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_received, viewGroup, false);
            return new ViewHolderReceiver(listView);
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int i) {

            if (messages.get(i).getFrom().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())) {

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

            userName = listView.findViewById(R.id.user_name_reg);
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
