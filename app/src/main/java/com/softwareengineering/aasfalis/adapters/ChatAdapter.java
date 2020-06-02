package com.softwareengineering.aasfalis.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.models.Message;

import org.ocpsoft.prettytime.PrettyTime;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private static final String TAG = "MyActivity";

    PrettyTime p = new PrettyTime();
    FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message model) {
        holder.message.setText(model.getMessage());
        holder.sender.setText(model.getFrom());
        holder.timestamp.setText(p.format(model.getTime()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right, parent, false);;

        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left, parent, false);
                return new ViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right, parent, false);
                return new ViewHolder(view);

        }

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).getFrom().equals(user.getEmail())) {
            Log.d("View", getItem(position).getFrom());
            return 1;
        }


        return 0;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sender, message, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sender = itemView.findViewById(R.id.sendername);
            message = itemView.findViewById(R.id.textmessage);
            timestamp = itemView.findViewById(R.id.timestamp);

        }
    }

}
