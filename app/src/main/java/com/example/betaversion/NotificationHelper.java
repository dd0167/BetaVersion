package com.example.betaversion;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
   public static final String channelID = "channelID";
   public static final String channelName = "Channel Name";

   private NotificationManager mManager;

   public NotificationHelper(Context base) {
      super(base);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         createChannel();
      }
   }

   @TargetApi(Build.VERSION_CODES.O)
   private void createChannel() {
      NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

      getManager().createNotificationChannel(channel);
   }

   public NotificationManager getManager() {
      if (mManager == null) {
         mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      }

      return mManager;
   }

   public NotificationCompat.Builder getChannelNotification() {
      Intent intent=new Intent(this,CreditsActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

      return new NotificationCompat.Builder(getApplicationContext(), channelID)
              .setContentTitle("Alarm!")
              .setContentText("Your AlarmManager is working.")
              .setSmallIcon(R.drawable.notification_icon)
              .setContentIntent(pendingIntent)
              .setAutoCancel(true);
   }
}