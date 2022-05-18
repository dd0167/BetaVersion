package com.example.betaversion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * מפסיק את פעולת האפליקציה ברקע בלחיצה על "עצור" בהתראה הראשית.
 */
public class NotificationService extends BroadcastReceiver {

   @Override
   public void onReceive(Context context, Intent intent) {

      Toast.makeText(context, "האפליקציה הפסיקה לעבוד ברקע", Toast.LENGTH_SHORT).show();
      BackgroundService.stop_handler();

      Intent serviceIntent = new Intent(context, BackgroundService.class);
      context.stopService(serviceIntent);
   }
}
