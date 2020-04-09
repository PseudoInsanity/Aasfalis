package com.softwareengineering.aasfalis.fragments;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.activities.MainActivity;
import com.softwareengineering.aasfalis.adapters.MessageAdapter;
import com.softwareengineering.aasfalis.adapters.MessageHandler;
import com.softwareengineering.aasfalis.client.Database;
import com.softwareengineering.aasfalis.models.Friend;
import com.softwareengineering.aasfalis.models.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static com.softwareengineering.aasfalis.client.ClientService.sendObject;


public class MessageFragment extends DialogFragment {

    private Button sendBtn;
    private EditText msgTxt;
    private RecyclerView msgBox;
    private TextView userTxt, userName, userTime;
    private View userMsg, friendMsg;
    private RecyclerView.LayoutManager layoutManager;
    private MessageAdapter adapter;
    private MessageHandler messageHandler;
    private Friend currentFriend;
    private Toolbar toolbar;
    private Database database;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);

        sendBtn = view.findViewById(R.id.msgSendBtn);
        msgTxt = view.findViewById(R.id.userMsgTxt);
        messageHandler = new MessageHandler();
        toolbar = view.findViewById(R.id.friendToolbar);

        if (currentFriend.getFirstName() != null) {
            toolbar.setTitle(currentFriend.getFirstName());
        }
        database = new Database();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!msgTxt.getText().toString().equals("")) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
                    String strDate = mdformat.format(calendar.getTime());

                    sendObject(new Message(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(), currentFriend.geteMail(), msgTxt.getText().toString(), strDate, database.getCurrentName()));
                    msgTxt.setText("");

                }

            }
        });


        return view;
    }

    @Override
    public void onResume () {
        super.onResume();
        buildRecyclerView(view);
    }

    private void buildRecyclerView(final View view) {

        msgBox = (RecyclerView) view.findViewById(R.id.conversationBox);
        msgBox.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(view.getContext());
        msgBox.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(messageHandler.getMessages(), currentFriend);
        msgBox.setAdapter(adapter);
    }

    public void setArguments(Friend friend) {

        this.currentFriend = friend;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

}
