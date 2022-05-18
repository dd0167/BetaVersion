package com.example.betaversion;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        Task task=intent.getParcelableExtra("task");
        show_notification(context,task);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show_notification(Context context, Task task)
    {
        Random random=new Random();

        NotificationChannel channel = new NotificationChannel("channelID", "התראות לפי זמן", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.createNotificationChannel(channel);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        String contextText="";
        if (!task.getTaskAddress().isEmpty())
        {
            contextText="המטלה '"+task.getTaskName()+"' נמצאת בכתובת: "+task.getTaskAddress();
        }
        String finalContextText = contextText;

        Glide.with(context)
                .asBitmap()
                .load(task.getTaskPictureUid())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        NotificationCompat.Builder notificationCompat= new NotificationCompat.Builder(context, "channelID")
                                .setContentTitle("הגיע הזמן לבצע את המטלה '"+task.getTaskName()+"' ("+task.getTaskHour()+")")
                                .setContentText(finalContextText)
                                .setSmallIcon(R.drawable.notification_icon)
                                .setLargeIcon(resource)
                                .setColor(Color.parseColor(task.getTaskColor()))
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        mManager.notify(random.nextInt(),notificationCompat.build());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
}
