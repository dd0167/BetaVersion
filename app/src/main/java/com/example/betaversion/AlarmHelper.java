package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * מחלקת עזר לתזכורות.
 */

public class AlarmHelper {

    public static FirebaseUser currentUser;

    /**
     * שם הפעולה הדרושה (מחיקת/יצירת התראות).
     */
    public static String action;



    /**
     * הסרת כל התזכורות.
     *
     * @param context the context
     */
    public static void cancel_all_alarms(Context context)
    {
        currentUser = mAuth.getCurrentUser();
        action="cancel";
        read_lists(context);
        read_tasksDays(context);
    }

    /**
     * יצירת כל התזכורות הנדרשות.
     *
     * @param context the context
     */
    public static void create_all_alarms(Context context)
    {
        currentUser = mAuth.getCurrentUser();
        action="create";
        read_lists(context);
        read_tasksDays(context);
    }

    /**
     * הסרת תזכורת.
     *
     * @param task    the task
     * @param context the context
     */
    public static void cancel_alarm(Task task,Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getTaskAlarmId(), intent,  PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
    }

    /**
     * יצירת תזכורת.
     *
     * @param task    the task
     * @param context the context
     * @return the id of alarm
     */
    public static int create_task_alarm(Task task, Context context)
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

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("task",task);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm_id, intent,  PendingIntent.FLAG_IMMUTABLE );

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

        return alarm_id;
    }

    /**
     * קריאת הרשימות מ-Firebase Realtime Database.
     *
     * @param context the context
     */
    public static void read_lists(Context context)
    {
        refLists.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    List list = data.child("List Data").getValue(List.class);
                    read_tasks(refLists,list.getListName(),context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * קריאת הימים המרוכזים מ-Firebase Realtime Database.
     *
     * @param context the context
     */
    public static void read_tasksDays(Context context)
    {
        refTasksDays.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    TasksDay tasksDay=data.child("Tasks Day Data").getValue(TasksDay.class);
                    read_tasks(refTasksDays,tasksDay.getTasksDayName(),context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * קריאת המטלות מ-Firebase Realtime Database.
     *
     * @param reference the reference
     * @param name      the name of List/DayList
     * @param context   the context
     */
    public static void read_tasks(DatabaseReference reference, String name, Context context)
    {
        reference.child(currentUser.getUid()).child(name).child("Tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Task task = data.child("Task Data").getValue(Task.class);

                    if (action.equals("cancel"))
                    {
                        cancel_alarm(task,context);
                    }
                    else if (action.equals("create"))
                    {
                        if (isHourOfTaskHasOk(task.getTaskDay(),task.getTaskHour(),context))
                        {
                            task.setTaskAlarmId(create_task_alarm(task,context));
                            reference.child(currentUser.getUid()).child(name).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * בדיקה האם השעה שנבחרה לביצוע המטלה עוד לא עברה (תקינות קלט).
     *
     * @param task_date the date of task
     * @param task_time the time of task
     * @param context   the context
     * @return the boolean
     */
    public static boolean isHourOfTaskHasOk(String task_date, String task_time, Context context)
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
                    if (Integer.parseInt(hour[0])-Integer.parseInt(String.valueOf(new Date().getHours()))>1)
                    {
                        return true;
                    }
                    else if (Integer.parseInt(hour[0])-Integer.parseInt(String.valueOf(new Date().getHours()))==1)
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
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
