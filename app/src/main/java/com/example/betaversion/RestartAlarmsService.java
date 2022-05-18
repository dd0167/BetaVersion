package com.example.betaversion;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

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

      AlarmHelper.create_all_alarms(getApplicationContext());

      Intent serviceIntent = new Intent(getApplicationContext(), RestartAlarmsService.class);
      stopService(serviceIntent);

      return START_NOT_STICKY;
   }
}
