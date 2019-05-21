package com.softwareengineering.aasfalis.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

            Intent background = new Intent(context, ClientService.class);
            context.startService(background);



    }
}
