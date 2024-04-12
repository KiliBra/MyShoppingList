package com.example.myshoppinglist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//
public class ListGroupBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals("android.intent.action.OpenList")) {
                return;
            }
        }
    }
}
