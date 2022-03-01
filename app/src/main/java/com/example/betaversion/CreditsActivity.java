package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;
import java.util.Date;

public class CreditsActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "Notifications";
    BottomNavigationView bottomNavigationView;

    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);

        bottomNavigationView.getMenu().findItem(R.id.about).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.about);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.my_lists) {
                    Intent ma = new Intent(CreditsActivity.this, MainActivity.class);
                    startActivity(ma);
                    finish();
                } else if (id == R.id.about) {
                    Intent ca = new Intent(CreditsActivity.this, CreditsActivity.class);
                    startActivity(ca);
                    finish();
                } else if (id == R.id.settings) {
                    Intent sa = new Intent(CreditsActivity.this, SettingsActivity.class);
                    startActivity(sa);
                    finish();
                } else if (id == R.id.tasks_day) {
                    Intent td = new Intent(CreditsActivity.this, TasksDayListsActivity.class);
                    startActivity(td);
                    finish();
                }
                return true;
            }
        });

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "About" + "</font>"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if (title.equals("Log Out")) {
            AlertDialog.Builder adb;
            adb = new AlertDialog.Builder(this);
            adb.setTitle("Log Out");
            adb.setMessage("Are you sure you want log out?");
            adb.setIcon(R.drawable.log_out_icon);
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.signOut();
                    SharedPreferences settings = getSharedPreferences("Stay_Connect", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("stayConnect", false);
                    editor.commit();
                    move_login();
                }
            });
            adb.setNeutralButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        return true;
    }

    public void move_login() {
        Intent la = new Intent(this, LoginActivity.class);
        startActivity(la);
        finish();
    }

    public void move_main() {
        Intent ma = new Intent(this, MainActivity.class);
        startActivity(ma);
        finish();
    }

    public void notification(View view) {
        Toast.makeText(this, "notification", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        for (int i = 0; i < 1; i++) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.drawable.task_icon))
                    .setContentTitle("Notification Title" + i)
                    .setContentText("textContent" + i)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            int notification_amount = i;//(0=1 notification)

            createNotificationChannel();

// notificationId is a unique int for each notification that you must define
            notificationManager.notify(notification_amount, builder.build());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void start(View view) {
        Toast.makeText(this, "start button", Toast.LENGTH_SHORT).show();



//        int milliseconds=10000;
//        handler = new Handler(Looper.getMainLooper());
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                // Do the task...
//                //Toast.makeText(CreditsActivity.this, "hi", Toast.LENGTH_SHORT).show();
//
//                Calendar c = Calendar.getInstance();
//                c.set(Calendar.HOUR_OF_DAY, c.getTime().getHours());
//                c.set(Calendar.MINUTE, c.getTime().getMinutes());
//                c.set(Calendar.SECOND, c.getTime().getSeconds()+5);
//                startAlarm(c);
//
//                Log.e("Alarm",c.getTime().toString());
//
//                handler.postDelayed(this, milliseconds); // Optional, to repeat the task.
//            }
//        };
//        handler.postDelayed(runnable, milliseconds);


        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, c.getTime().getHours());
        c.set(Calendar.MINUTE, c.getTime().getMinutes());
        c.set(Calendar.SECOND, c.getTime().getSeconds()+5);
        startAlarm(c);
    }

    public void stop(View view)
    {
        Toast.makeText(this, "stop button", Toast.LENGTH_SHORT).show();

        cancelAlarm();
    }




    public void startAlarm(Calendar c) {
//        int milliseconds=5000;
//
//        handler = new Handler(Looper.getMainLooper());
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                // Do the task...
//                Toast.makeText(CreditsActivity.this, "hi", Toast.LENGTH_SHORT).show();
//                handler.postDelayed(this, milliseconds); // Optional, to repeat the task.
//            }
//        };
//        handler.postDelayed(runnable, milliseconds);




        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    public void cancelAlarm() {
        // Stop a repeating task like this.
        //handler.removeCallbacks(runnable);






        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);


        AlertReceiver.stop_hi();
    }
}