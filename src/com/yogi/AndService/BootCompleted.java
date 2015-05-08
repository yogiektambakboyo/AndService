package com.yogi.AndService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SOFT
 * Date: 5/28/14
 * Time: 10:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class BootCompleted extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        //we double check here for only boot complete event
        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
        {
            //here we start the service
            Intent serviceIntent = new Intent(context, AndroidStartServiceOnBoot.class);
            context.startService(serviceIntent);
        }

    }
}
