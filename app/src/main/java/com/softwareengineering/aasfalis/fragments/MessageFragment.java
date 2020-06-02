package com.softwareengineering.aasfalis.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.adapters.ChatAdapter;
import com.softwareengineering.aasfalis.models.Friend;
import com.softwareengineering.aasfalis.models.Message;

import java.util.Date;


public class MessageFragment extends DialogFragment {

    private Button sendBtn;
    private EditText msgTxt;
    private RecyclerView msgBox;
    private RecyclerView.LayoutManager layoutManager;
    private ChatAdapter adapter;
    private Friend currentFriend;
    private Toolbar toolbar;
    private CollectionReference chatref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        sendBtn = view.findViewById(R.id.msgSendBtn);
        msgTxt = view.findViewById(R.id.userMsgTxt);
        toolbar = view.findViewById(R.id.friendToolbar);

        if (currentFriend.getFirstName() != null) {
            toolbar.setTitle(currentFriend.getFirstName());
        }
        chatref = FirebaseFirestore.getInstance().collection(currentFriend.getRoomID());

        msgBox = view.findViewById(R.id.conversationBox);
        msgBox.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        msgBox.setLayoutManager(layoutManager);

        Query query = chatref;
        query.orderBy("time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new ChatAdapter(options);
        adapter.registerAdapterDataObserver( new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                msgBox.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
        adapter.startListening();
        msgBox.setAdapter(adapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!msgTxt.getText().toString().equals("")) {

                    Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(), currentFriend.geteMail(), msgTxt.getText().toString(), new Date());
                    chatref.document(new Date().toString()).set(message);
                    msgTxt.setText("");
                }
            }
        });


        return view;
    }

    @Override
    public void onResume () {
        super.onResume();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void setArguments(Friend friend) {

        this.currentFriend = friend;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        adapter.startListening();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);

        }
    }

}
