package com.example.betaversion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;

public class AlarmBootReceiver extends BroadcastReceiver {

   @RequiresApi(api = Build.VERSION_CODES.O)
   @Override
   public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

         Intent i = new Intent(context, RestartAlarmsService.class);
         //ComponentName service = context.startService(i);
         context.startService(i);
      }
   }
}