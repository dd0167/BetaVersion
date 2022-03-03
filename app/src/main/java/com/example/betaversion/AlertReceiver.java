package com.example.betaversion;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

public class AlertReceiver extends BroadcastReceiver {

   public static Runnable runnable;
   public static Handler handler=new Handler(Looper.getMainLooper());

   public static FusedLocationProviderClient fusedLocationProviderClient;
   public static CancellationTokenSource cancellationTokenSource;
   public static String latitude="", longitude="";

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















      int milliseconds=7000;
      handler = new Handler(Looper.getMainLooper());
      runnable = new Runnable() {
         @Override
         public void run() {
            // Do the task...
            //Toast.makeText(CreditsActivity.this, "hi", Toast.LENGTH_SHORT).show();

            show_notification(context);

            handler.postDelayed(this, milliseconds); // Optional, to repeat the task.
         }
      };
      handler.postDelayed(runnable, milliseconds);
   }

   public static void show_notification(Context context)
   {
      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
      cancellationTokenSource = new CancellationTokenSource();

      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         // TODO: Consider calling
         //    ActivityCompat#requestPermissions
         // here to request the missing permissions, and then overriding
         //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
         //                                          int[] grantResults)
         // to handle the case where the user grants the permission. See the documentation
         // for ActivityCompat#requestPermissions for more details.
         return;
      }
      com.google.android.gms.tasks.Task<Location> currentLocationTask = fusedLocationProviderClient.getCurrentLocation(
              PRIORITY_HIGH_ACCURACY,
              cancellationTokenSource.getToken()
      );
      currentLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
         @Override
         public void onSuccess(Location location) {
            latitude=String.valueOf(location.getLatitude());
            longitude=String.valueOf(location.getLongitude());
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
         }
      });






      NotificationHelper notificationHelper = new NotificationHelper(context);
      NotificationCompat.Builder nb = notificationHelper.getChannelNotification(context,latitude,longitude);
      notificationHelper.getManager().notify(1, nb.build());
   }

   public static void stop_hi()
   {
      handler.removeCallbacks(runnable);
   }
}