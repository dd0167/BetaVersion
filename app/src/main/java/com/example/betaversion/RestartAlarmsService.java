package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class RestartAlarmsService extends Service {

   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public void onCreate() {

      super.onCreate();
   }

   @RequiresApi(api = Build.VERSION_CODES.O)
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      // Set the alarm here.
      //Toast.makeText(getApplicationContext(), "reboot", Toast.LENGTH_SHORT).show();

      AlarmHelper.create_all_alarms(getApplicationContext());

      Intent serviceIntent = new Intent(getApplicationContext(), RestartAlarmsService.class);
      stopService(serviceIntent);

      return START_NOT_STICKY;
   }
}
