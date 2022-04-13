package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class BackgroundService extends Service {

   FirebaseUser currentUser;

   public static Runnable runnable;
   public static Handler handler;

   public static FusedLocationProviderClient fusedLocationProviderClient;
   public static CancellationTokenSource cancellationTokenSource;
   public static String latitude, longitude;

   public static ArrayList<Task> tasks=new ArrayList<Task>();

   @Override
   public void onCreate() {

      currentUser = mAuth.getCurrentUser();

      createNotificationChannel();

      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
      cancellationTokenSource = new CancellationTokenSource();
      handler=new Handler(Looper.getMainLooper());

      set_current_location();

      tasks.clear();
      read_lists();
      read_tasksDays();

      super.onCreate();
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {

      Intent notification_intent = new Intent(this, MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notification_intent, PendingIntent.FLAG_IMMUTABLE);

      // adding action for broadcast
      Intent broadcastIntent = new Intent(this, NotificationService.class);
      PendingIntent broadcastPendingIntent=PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_IMMUTABLE);

      Notification notification = new NotificationCompat.Builder(this, "CHANNEL_ID")
              .setContentTitle("Remind")
              .setContentText("האפליקציה פועלת ברקע")
              .setSmallIcon(R.drawable.notification_icon)
              .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                      R.drawable.gps_icon))
              .setContentIntent(pendingIntent)
              .addAction(R.drawable.notification_icon,"עצור",broadcastPendingIntent)
              .build();

      startForeground(1, notification);

      //do heavy work on a background thread
      run();
      //stopSelf();

      return START_NOT_STICKY;
   }

   public void run()
   {
      int milliseconds=10000; // 1000 millisecond = 1 second
      runnable = new Runnable() {
         @RequiresApi(api = Build.VERSION_CODES.O)
         @Override
         public void run() {
            // Do the task...

            //Toast.makeText(getApplicationContext(), "the size of tasks is: "+ tasks.size(), Toast.LENGTH_SHORT).show();

            set_current_location();

            for (Task task: tasks)
            {
               if (getDistance(task) <=1 && !isDateOfTaskHasPassed(task))
               {
                  show_notification(task);
//                  try
//                  {
//
//                     Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/notification_sound");
//                     Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
//                     r.play();
//                  }
//                  catch (Exception e)
//                  {
//                     e.printStackTrace();
//                  }
               }
            }
            // לבדוק כאן את מיקום המשתמש ובהתאם לקילומטר אחד להוציא התראות

            handler.postDelayed(this, milliseconds); // Optional, to repeat the task.
         }
      };
      handler.postDelayed(runnable, milliseconds);
   }

   @RequiresApi(api = Build.VERSION_CODES.O)
   public void show_notification(Task task)
   {
      Random random=new Random();
      int Unique_Integer_Number= random.nextInt();

      Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/notification_sound");
      Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);

      NotificationChannel channel = new NotificationChannel("channelID", "Alert Notification", NotificationManager.IMPORTANCE_HIGH);
      channel.setSound(alarmSound,r.getAudioAttributes());
      NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      mManager.createNotificationChannel(channel);

      Intent intent = new Intent(getApplicationContext(), MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//      Bitmap icon = BitmapFactory.decodeResource(getResources(),
//              R.drawable.task_icon);

//      Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//      // Vibrate for 500 milliseconds
//      v.vibrate(500);

      Glide.with(getApplicationContext())
              .asBitmap()
              .load(task.getTaskPictureUid())
              .into(new CustomTarget<Bitmap>() {
                 @Override
                 public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    NotificationCompat.Builder notificationCompat= new NotificationCompat.Builder(getApplicationContext(), "channelID")
                            .setContentTitle(task.getTaskName()+" ("+task.getTaskAddress()+")")
                            .setContentText("המטלה '"+task.getTaskName()+"' נמצאת במרחק "+getDistance(task)+" ק\"מ ממיקומך")
                            .setSmallIcon(R.drawable.notification_icon)
                            .setLargeIcon(resource)
                            .setColor(Color.parseColor(task.getTaskColor()))
                            .setContentIntent(pendingIntent)
                            //.setDefaults(Notification.DEFAULT_VIBRATE)
                            //.setVibrate(new long[]{1000,1000,1000})
                            .setAutoCancel(true);

                    mManager.notify(Unique_Integer_Number,notificationCompat.build());
                 }

                 @Override
                 public void onLoadCleared(@Nullable Drawable placeholder) {
                 }
              });
   }

   public static void stop_hi()
   {
      handler.removeCallbacks(runnable);
   }

   @Override
   public void onDestroy() {
      stop_hi();
      super.onDestroy();
   }

   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   public void createNotificationChannel() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel serviceChannel = new NotificationChannel(
                 "CHANNEL_ID",
                 "Background Service Channel",
                 NotificationManager.IMPORTANCE_DEFAULT
         );

         NotificationManager manager = getSystemService(NotificationManager.class);
         manager.createNotificationChannel(serviceChannel);
      }
   }

   public void set_current_location()
   {
      if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
         }
      });
   }

   public void read_lists()
   {
      refLists.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot data : snapshot.getChildren()) {
               List list = data.child("List Data").getValue(List.class);
               read_tasks(refLists,list.getListName());
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
         }
      });
   }

   public void read_tasksDays()
   {
      refTasksDays.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot data : snapshot.getChildren()) {
               TasksDay tasksDay=data.child("Tasks Day Data").getValue(TasksDay.class);
               read_tasks(refTasksDays,tasksDay.getTasksDayName());
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
         }
      });
   }

   public void read_tasks(DatabaseReference reference, String name)
   {
      reference.child(currentUser.getUid()).child(name).child("Tasks").addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot data : snapshot.getChildren()) {
               Task task = data.child("Task Data").getValue(Task.class);
               if (!task.getTaskAddress().isEmpty())
               {
                  tasks.add(task);
               }
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
         }
      });
   }

   public static double distance(double lat1, double lat2, double lon1, double lon2)
   {

      // The math module contains a function
      // named toRadians which converts from
      // degrees to radians.
      lon1 = Math.toRadians(lon1);
      lon2 = Math.toRadians(lon2);
      lat1 = Math.toRadians(lat1);
      lat2 = Math.toRadians(lat2);

      // Haversine formula
      double dlon = lon2 - lon1;
      double dlat = lat2 - lat1;
      double a = Math.pow(Math.sin(dlat / 2), 2)
              + Math.cos(lat1) * Math.cos(lat2)
              * Math.pow(Math.sin(dlon / 2),2);

      double c = 2 * Math.asin(Math.sqrt(a));

      // Radius of earth in kilometers. Use 3956
      // for miles 6371;
      double r = 3956;

      // calculate the result
      return(c * r);
   }

   public double getDistance(Task task)
   {
      Geocoder geocoder=new Geocoder(getApplicationContext());
      java.util.List<Address> addressList;
      Address task_address;
      LatLng task_location = null;
      try {
         addressList=geocoder.getFromLocationName(task.getTaskAddress(),6);
         task_address=addressList.get(0);
         task_location = new LatLng(task_address.getLatitude(), task_address.getLongitude());
      }
      catch (Exception e)
      {
         Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
      }
      double d = distance(Double.parseDouble(latitude), task_location.latitude , Double.parseDouble(longitude), task_location.longitude);
      String distance = String.valueOf(d).substring(0, 5);
      double final_distance=Double.parseDouble(distance);

      return final_distance;
   }

   public boolean isDateOfTaskHasPassed(Task task)
   {
      String task_date=task.getTaskDay();
      String task_time=task.getTaskHour();
      String[] date=task_date.split("-");
      String task_date_time=date[2]+"-"+date[1]+"-"+date[0]+" "+task_time;

      try {
         if (new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("he")).parse(task_date_time).before(new Date())) {
            return true;
         }
      }
      catch (Exception e)
      {
         Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
      }

      return false;
   }
}