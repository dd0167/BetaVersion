package com.example.betaversion;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

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
      Intent intent = new Intent(this, CreditsActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

      return new NotificationCompat.Builder(getApplicationContext(), channelID)
              .setContentTitle("Alarm!")
              .setContentText("Your AlarmManager is working. ")
              .setSmallIcon(R.drawable.notification_icon)
              .setContentIntent(pendingIntent)
              .setAutoCancel(true);
   }
}