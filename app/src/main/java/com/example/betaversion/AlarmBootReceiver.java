package com.example.betaversion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;

/**
 * הפעלת האפליקציה ברקע על מנת ליצור את כל התזכורות הנדרשות כאשר הטלפון מופעל מחדש.
 */

public class AlarmBootReceiver extends BroadcastReceiver {

   @RequiresApi(api = Build.VERSION_CODES.O)
   @Override
   public void onReceive(Context context, Intent intent) {

      if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
         Intent i = new Intent(context, RestartAlarmsService.class);
         context.startService(i);
      }
   }
}