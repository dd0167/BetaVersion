package com.example.betaversion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class AlertReceiver extends BroadcastReceiver {

   public static Runnable runnable;
   public static Handler handler=new Handler(Looper.getMainLooper());

   @Override
   public void onReceive(Context context, Intent intent) {

//      int milliseconds=5000;
//      runnable = new Runnable() {
//         @Override
//         public void run() {
//            // Do the task...
//            Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show();
//            Log.e("Alarm","...............................................................");
//            handler.postDelayed(this, milliseconds); // Optional, to repeat the task.
//         }
//      };
//      handler.postDelayed(runnable, milliseconds);


      //show_notification(context);












      int milliseconds=10000;
      handler = new Handler(Looper.getMainLooper());
      runnable = new Runnable() {
         @Override
         public void run() {
            // Do the task...
            //Toast.makeText(CreditsActivity.this, "hi", Toast.LENGTH_SHORT).show();

            show_notification(context);

            Log.e("Alarm","...................................................".toString());

            handler.postDelayed(this, milliseconds); // Optional, to repeat the task.
         }
      };
      handler.postDelayed(runnable, milliseconds);
   }

   public static void show_notification(Context context)
   {
      NotificationHelper notificationHelper = new NotificationHelper(context);
      NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
      notificationHelper.getManager().notify(1, nb.build());
   }

   public static void stop_hi()
   {
      handler.removeCallbacks(runnable);
   }
}