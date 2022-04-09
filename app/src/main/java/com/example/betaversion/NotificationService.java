package com.example.betaversion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationService extends BroadcastReceiver {

   @Override
   public void onReceive(Context context, Intent intent) {
      Toast.makeText(context, "האפליקציה הפסיקה לעבוד ברקע", Toast.LENGTH_SHORT).show();
      BackgroundService.stop_hi();

      Intent serviceIntent = new Intent(context, BackgroundService.class);
      context.stopService(serviceIntent);
   }
}
