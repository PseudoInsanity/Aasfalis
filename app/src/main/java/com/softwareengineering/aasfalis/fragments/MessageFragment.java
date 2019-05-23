package com.softwareengineering.aasfalis.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.adapters.FriendListAdapter;
import com.softwareengineering.aasfalis.adapters.MessageAdapter;
import com.softwareengineering.aasfalis.adapters.MessageHandler;
import com.softwareengineering.aasfalis.client.ClientService;
import com.softwareengineering.aasfalis.models.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MessageFragment extends Fragment {

    private Button sendBtn;
    private EditText msgTxt;
    private RecyclerView msgBox;
    private TextView userTxt, userName, userTime;
    private View userMsg, friendMsg;
    private RecyclerView.LayoutManager layoutManager;
    private MessageAdapter adapter;
    private MessageHandler messageHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message, container, false);

        sendBtn = view.findViewById(R.id.msgSendBtn);
        msgTxt = view.findViewById(R.id.userMsgTxt);
        messageHandler = new MessageHandler();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!msgTxt.getText().toString().equals("")) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
                    String strDate = mdformat.format(calendar.getTime());

                    //sendObject(new Message("Matteo", "Erik", msgTxt.getText().toString(), strDate));
                    messageHandler.addMessage(new Message("Matteo", "Erik", msgTxt.getText().toString(), strDate));
                    adapter.notifyItemInserted(messageHandler.lastIndex());

                    msgTxt.setText("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            msgBox.smoothScrollToPosition(messageHandler.lastIndex());
                        }
                    }, 1);
                }

            }
        });

        buildRecyclerView(view);

        msgBox.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    layoutManager.smoothScrollToPosition(msgBox, null, adapter.getItemCount());
                }
            }
        });
        return view;
    }

    private void buildRecyclerView(final View view) {

        msgBox = (RecyclerView) view.findViewById(R.id.conversationBox);
        msgBox.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(view.getContext());
        msgBox.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(messageHandler.getMessages());
        msgBox.setAdapter(adapter);
    }

}
