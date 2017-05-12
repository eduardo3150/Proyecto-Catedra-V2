package com.chavez.eduardo.udbtour;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WebServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, WebService.class);
        context.startService(myIntent);
    }
}
