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

   FirebaseUser currentUser;

   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public void onCreate() {
      currentUser = mAuth.getCurrentUser();

      super.onCreate();
   }

   @RequiresApi(api = Build.VERSION_CODES.O)
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      // Set the alarm here.
      Toast.makeText(getApplicationContext(), "reboot", Toast.LENGTH_SHORT).show();

      read_lists();
      read_tasksDays();

      Intent serviceIntent = new Intent(getApplicationContext(), BackgroundService.class);
      stopService(serviceIntent);

      return START_NOT_STICKY;
   }

   public int create_task_alarm(Task task)
   {
      String[] task_date=task.getTaskDay().split("-");
      String[] task_time=task.getTaskHour().split(":");

      Calendar c = Calendar.getInstance();
      c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(task_time[0])-1);
      c.set(Calendar.MINUTE, Integer.parseInt(task_time[1]));
      c.set(Calendar.SECOND, 0);

      c.set(Calendar.YEAR, Integer.parseInt(task_date[0]));
      c.set(Calendar.MONTH,Integer.parseInt(task_date[1])-1);
      c.set(Calendar.DAY_OF_MONTH,Integer.parseInt(task_date[2]));

      Random random=new Random();
      int alarm_id=random.nextInt();

      AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
      Intent intent = new Intent(this, AlarmReceiver.class);
      intent.putExtra("task",task);
      PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm_id, intent,  PendingIntent.FLAG_IMMUTABLE );

      if (c.before(Calendar.getInstance())) {
         c.add(Calendar.DATE, 1);
      }

      alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

      return alarm_id;
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

               if (isHourOfTaskHasOk(task.getTaskDay(),task.getTaskHour()))
               {
                  task.setTaskAlarmId(create_task_alarm(task));
                  reference.child(currentUser.getUid()).child(name).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
               }
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
         }
      });
   }

   public boolean isHourOfTaskHasOk(String task_date, String task_time)
   {
      String[] hour=task_time.split(":");
      String[] date=task_date.split("-");
      String task_date_time=date[2]+"-"+date[1]+"-"+date[0]+" "+task_time;

      String currentDate =new SimpleDateFormat("dd-MM-yyyy").format(new Date());
      String[] current_date= currentDate.split("-");

      try {
         if (!new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("he")).parse(task_date_time).before(new Date())) {
            if (date[2].equals(current_date[0]) && date[1].equals(current_date[1]) && date[0].equals(current_date[2]))
            {
               if (Integer.parseInt(hour[0])-Integer.parseInt(String.valueOf(new Date().getHours()))>=1)
               {
                  if (Integer.parseInt(hour[1])>Integer.parseInt(String.valueOf(new Date().getMinutes())))
                  {
                     return true;
                  }
               }
            }
            else
            {
               return true;
            }
         }
      }
      catch (Exception e)
      {
         Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
      }
      return false;
   }
}
