package de.wenzel.paul.trelloextansion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("TrelloExtansion");
        i.setClass(context, MainService.class);
        context.startService(i);
    }
}

