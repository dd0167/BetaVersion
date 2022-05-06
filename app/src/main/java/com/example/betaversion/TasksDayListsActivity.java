package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;
import static com.example.betaversion.FB_Ref.refTasksDays;
import static com.example.betaversion.FB_Ref.refUsers;
import static com.example.betaversion.FB_Ref.referenceStorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TasksDayListsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, PopupMenu.OnMenuItemClickListener{

    BottomNavigationView bottomNavigationView;

    FirebaseUser currentUser;

    TasksDay tasksDay;
    TasksDay tasksDay_clicked;

    ArrayList<String> tasksDay_array = new ArrayList<String>();
    ArrayList<TasksDay> tasksDay_values = new ArrayList<TasksDay>();
    String tasksDay_name;
    ListView tasksDay_listview;
    TextView tv_tasksDay_amount;

    BottomSheetDialog bottomSheetDialog_tasksDay;

    //Date
    Calendar calendar=Calendar.getInstance();
    int year;
    int month;
    int day;
    String date="בחר תאריך יעד";

    ImageView cancel_bottom_sheet_dialog_tasksDays;

    Chip chip_name;
    Chip chip_date;

    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-M-dd", new Locale("he"));
    SimpleDateFormat dateFormat_before = new SimpleDateFormat("dd-MM-yyyy", new Locale("he"));
    SimpleDateFormat dateFormat_after = new SimpleDateFormat("yyyy-MM-dd", new Locale("he"));

    ValueEventListener tasksDay_array_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_day_lists);

        currentUser = mAuth.getCurrentUser();

        bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);

        bottomNavigationView.getMenu().findItem(R.id.tasks_day).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.tasks_day);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id==R.id.my_lists)
                {
                    Intent ma = new Intent(TasksDayListsActivity.this,MainActivity.class);
                    startActivity(ma);
                    finish();
                }
                else if (id==R.id.about)
                {
                    Intent ca = new Intent(TasksDayListsActivity.this, CreditsActivity.class);
                    startActivity(ca);
                    finish();
                }
                else if (id==R.id.settings)
                {
                    Intent sa = new Intent(TasksDayListsActivity.this, SettingsActivity.class);
                    startActivity(sa);
                    finish();
                }
                else if (id==R.id.tasks_day)
                {
                    Intent td=new Intent(TasksDayListsActivity.this,TasksDayListsActivity.class);
                    startActivity(td);
                    finish();
                }
                return true;
            }
        });

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "הימים המרוכזים שלי" + "</font>"));

        bottomSheetDialog_tasksDay=(BottomSheetDialog) new BottomSheetDialog(TasksDayListsActivity.this);

        tv_tasksDay_amount=(TextView) findViewById(R.id.tv_tasksDay_amount);

        tasksDay_listview=(ListView) findViewById(R.id.tasksDay_listview);
        tasksDay_listview.setOnItemClickListener(this);
        tasksDay_listview.setOnItemLongClickListener(this);
        tasksDay_listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        read_tasksDays();

        showDaysHasTheyDatePassed();

        chip_name=(Chip) findViewById(R.id.sort_by_name);
        chip_date=(Chip) findViewById(R.id.sort_by_date);
        chip_name.setClickable(false);

        check_permissions();
    }

    public boolean is_Internet_Connected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public void read_tasksDays() {
        if (!is_Internet_Connected())
        {
            AlertDialog.Builder adb;
            adb=new AlertDialog.Builder(this);
            adb.setTitle("אין חיבור אינטרנט");
            adb.setMessage("אין אפשרות לקרוא את הנתונים הנדרשים, אנא התחבר לאינטרנט");
            adb.setIcon(R.drawable.no_wifi);
            adb.setCancelable(false);
            adb.setPositiveButton("נסה שוב", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    read_tasksDays();
                }
            });
            adb.setNeutralButton("יציאה מהאפליקציה", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
            AlertDialog ad= adb.create();
            ad.show();
        }
        else
        {
             tasksDay_array_listener= new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dS) {
                    tasksDay_values.clear();
                    tasksDay_array.clear();
                    for(DataSnapshot data : dS.getChildren()) {
                        TasksDay stuTmp=data.child("Tasks Day Data").getValue(TasksDay.class);
                        tasksDay_values.add(stuTmp);
                        tasksDay_name = stuTmp.getTasksDayName();
                        tasksDay_array.add(tasksDay_name);
                    }
                    CustomTasksDayListAdapter customadp = new CustomTasksDayListAdapter(TasksDayListsActivity.this,
                            tasksDay_array,tasksDay_values);
                    tasksDay_listview.setAdapter(customadp);
                    tv_tasksDay_amount.setText("קיימים "+ tasksDay_array.size()+ " ימים מרוכזים");

                    //ArrayAdapter<String> adp=new ArrayAdapter<String>(TasksDayListsActivity.this,R.layout.support_simple_spinner_dropdown_item,tasksDay_array);
                    //tasksDay_listview.setAdapter(adp);
                    //tv_tasksDay_amount.setText("You have "+ tasksDay_array.size()+ " tasks days");
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(TasksDayListsActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            };
            refTasksDays.child(currentUser.getUid()).addValueEventListener(tasksDay_array_listener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.logOut_menu) {
            AlertDialog.Builder adb;
            adb = new AlertDialog.Builder(this);
            adb.setTitle("התנתקות");
            adb.setMessage("אתה בטוח שברצונך להתנתק מהאפליקציה?");
            adb.setIcon(R.drawable.log_out_icon);
            adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    AlarmHelper.cancel_all_alarms(getApplicationContext());

                    mAuth.signOut();
                    SharedPreferences settings = getSharedPreferences("STAY_CONNECT", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("stayConnect", false);
                    editor.commit();
                    move_login();
                }
            });
            adb.setNeutralButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        else if (item.getItemId()==R.id.deleteUser_menu) {
            AlertDialog.Builder adb;
            adb = new AlertDialog.Builder(this);
            adb.setTitle("מחיקת חשבון");
            adb.setMessage("אתה בטוח שברצונך למחוק את חשבונך לצמיתות? ביצוע פעולה זו תגרום לאובדן כל הנתונים הנמצאים באפליקציה");
            adb.setIcon(R.drawable.delete_user);
            adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                            AlarmHelper.cancel_all_alarms(getApplicationContext());

                            SharedPreferences settings = getSharedPreferences("STAY_CONNECT", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("stayConnect", false);
                            editor.commit();

                            // Delete the folder
                            String deleteFileName1 = currentUser.getUid()+"/";
                            StorageReference desertRef = referenceStorage.child(deleteFileName1);
                            desertRef.listAll()
                                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult) {
                                            for (StorageReference item : listResult.getItems()) {
                                                // All the items under listRef.
                                                item.delete();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Uh-oh, an error occurred!
                                            Toast.makeText(TasksDayListsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // Delete the file
                            String deleteFileName2 = "User Images/" + currentUser.getUid() + " image.png";
                            StorageReference desRef = referenceStorage.child(deleteFileName2);
                            desRef.delete().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(TasksDayListsActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            refUsers.child(currentUser.getUid()).removeValue();
                            refTasksDays.child(currentUser.getUid()).removeValue();
                            refLists.child(currentUser.getUid()).removeValue();

                            Toast.makeText(TasksDayListsActivity.this, "מחיקת החשבון בוצעה בהצלחה", Toast.LENGTH_SHORT).show();

                            move_login();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TasksDayListsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).setNeutralButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        else if (item.getItemId()==R.id.runBackground_menu)
        {
            Toast.makeText(this, "האפליקציה פועלת ברקע", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(this, BackgroundService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
        else if (item.getTitle().equals("עזרה"))
        {
            Intent ha = new Intent(this, HelpActivity.class);
            startActivity(ha);
            finish();
        }
        return true;
    }

    public void move_login()
    {
        Intent la = new Intent(this, LoginActivity.class);
        startActivity(la);
        finish();
    }

    public void create_tasks_day(View view) {
        tasksDay_clicked=null;
        show_bottomSheetDialog();
    }

    public void show_bottomSheetDialog()
    {
        bottomSheetDialog_tasksDay=new BottomSheetDialog(this,R.style.BottomSheetTheme);

        bottomSheetDialog_tasksDay.setContentView(R.layout.bottom_sheet_layout_tasks_day);
        bottomSheetDialog_tasksDay.setCancelable(false);
        bottomSheetDialog_tasksDay.show();

        cancel_bottom_sheet_dialog_tasksDays=(ImageView) bottomSheetDialog_tasksDay.findViewById(R.id.cancel_bottom_sheet_dialog_tasksDays);
        cancel_bottom_sheet_dialog_tasksDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog_tasksDay.cancel();
            }
        });
    }

    public void add_tasksDay (View view){

        EditText et_tasksDay_name=(EditText) bottomSheetDialog_tasksDay.findViewById(R.id.et_tasksDay_name);
        TextView tv_tasksDay_date=(TextView) bottomSheetDialog_tasksDay.findViewById(R.id.tv_tasksDay_date);
        String tasksDayName=et_tasksDay_name.getText().toString();

        try {
            date=tv_tasksDay_date.getText().toString();
            Date result = dateFormat_before.parse(date);
            date = dateFormat_after.format(result);
            tv_tasksDay_date.setText(date);
        }
        catch (Exception e)
        {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        tasksDay=new TasksDay(tasksDayName,date);

        if (tasksDayName.isEmpty())
        {
            et_tasksDay_name.setError("כתוב את מטרת היום המרוכז!");
            et_tasksDay_name.requestFocus();
        }
        else if (tasksDay_clicked!=null) {
            DatabaseReference ref = refTasksDays.child(currentUser.getUid()).child(tasksDay_clicked.getTasksDayName()).child("Tasks");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot data : dataSnapshot.getChildren()) {
                        Task task=data.child("Task Data").getValue(Task.class);
                        refTasksDays.child(currentUser.getUid()).child(tasksDayName).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(TasksDayListsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            refTasksDays.child(currentUser.getUid()).child(tasksDay_clicked.getTasksDayName()).removeValue();
            refTasksDays.child(currentUser.getUid()).child(tasksDayName).child("Tasks Day Data").setValue(tasksDay);

            Toast.makeText(this, "עדכון היום המרוכז בוצע בהצלחה", Toast.LENGTH_SHORT).show();
            bottomSheetDialog_tasksDay.cancel();
        }
        else if (tasksDay_array.contains(tasksDayName))
        {
            et_tasksDay_name.setError("קיים יום מרוכז עם אותו השם");
            et_tasksDay_name.requestFocus();
        }
        else if (date.equals("בחר תאריך יעד") )
        {
            Toast.makeText(TasksDayListsActivity.this, "בחר תאריך", Toast.LENGTH_SHORT).show();
        }
        else
        {
            refTasksDays.child(currentUser.getUid()).child(tasksDayName).child("Tasks Day Data").setValue(tasksDay);
            Toast.makeText(this, "יצירת היום המרוכז בוצע בהצלחה", Toast.LENGTH_SHORT).show();
            bottomSheetDialog_tasksDay.cancel();
        }
        tasksDay_clicked=null;
        date="בחר תאריך יעד";
        chip_name.setChecked(true);
        chip_name.setClickable(false);
        chip_date.setClickable(true);
    }

    public void set_tasksDay_date(View view)
    {
        TextView tv_tasksDay_date=(TextView) bottomSheetDialog_tasksDay.findViewById(R.id.tv_tasksDay_date);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TasksDayListsActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                date=dayOfMonth+"-"+month+"-"+year;
                tv_tasksDay_date.setText(date);
                date=year+"-"+month+"-"+dayOfMonth;
                try {
                    Date result_date = inputDateFormat.parse(date);
                    date = dateFormat_after.format(result_date);
                } catch (ParseException e) {
                    Toast.makeText(TasksDayListsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        },year,month,day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()); // Disable Previous or Future Dates in Datepicker
        datePickerDialog.show();
    }

    public void showPopup(View v)
    {
        PopupMenu popupMenu=new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.list_options);
        popupMenu.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent ta = new Intent(this,TasksActivity.class);

        ta.putExtra("tasksDay_clicked",tasksDay_values.get(position));
        ta.putExtra("reference","Tasks Days");

        startActivity(ta);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        tasksDay_clicked=tasksDay_values.get(position);
        showPopup(view);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int item_id=item.getItemId();
        if (item_id == R.id.update_list)
        {
            show_bottomSheetDialog();
            EditText et_tasksDay_name=(EditText) bottomSheetDialog_tasksDay.findViewById(R.id.et_tasksDay_name);
            TextView tv_tasksDay_date=(TextView) bottomSheetDialog_tasksDay.findViewById(R.id.tv_tasksDay_date);
            Button add_tasksDay=(Button) bottomSheetDialog_tasksDay.findViewById(R.id.add_tasksDay);
            ImageView iv_TasksDay_layout=(ImageView) bottomSheetDialog_tasksDay.findViewById(R.id.iv_TasksDay_layout);
            et_tasksDay_name.setText(tasksDay_clicked.getTasksDayName());
            add_tasksDay.setText("עדכן את היום המרוכז");

            try {
                Date result = dateFormat_after.parse(tasksDay_clicked.getTasksDayDate());
                String old_date = dateFormat_before.format(result);
                tv_tasksDay_date.setText(old_date);
            } catch (ParseException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            iv_TasksDay_layout.setImageResource(R.drawable.update_list);
        }
        else if (item_id == R.id.delete_list)
        {
            AlertDialog.Builder adb;
            adb=new AlertDialog.Builder(this);
            adb.setTitle("מחיקת היום המרוכז");
            adb.setMessage("אתה בטוח שברצונך למחוק את היום המרוכז '"+tasksDay_clicked.getTasksDayName()+"'?");
            adb.setIcon(R.drawable.delete_list);
            adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    refTasksDays.child(currentUser.getUid()).child(tasksDay_clicked.getTasksDayName()).child("Tasks").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Task task = data.child("Task Data").getValue(Task.class);
                                cancel_alarm(task);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(TasksDayListsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    refTasksDays.child(currentUser.getUid()).child(tasksDay_clicked.getTasksDayName()).removeValue();
                    Toast.makeText(TasksDayListsActivity.this, "מחיקת היום המרוכז בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                }
            });
            adb.setNeutralButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad= adb.create();
            ad.show();
        }
        return true;
    }

    public void sort_items(View view) {

        if (chip_name.isChecked())
        {
            //Toast.makeText(this, "Name Is Checked", Toast.LENGTH_SHORT).show();

            Query query=refTasksDays.child(currentUser.getUid()).orderByKey();
            query.addListenerForSingleValueEvent(tasksDay_array_listener);

            chip_name.setClickable(false);
            chip_date.setClickable(true);
        }
        else if (chip_date.isChecked())
        {
            //Toast.makeText(this, "Date Is Checked", Toast.LENGTH_SHORT).show();

            Query query=refTasksDays.child(currentUser.getUid()).orderByChild("Tasks Day Data/tasksDayDate");
            query.addListenerForSingleValueEvent(tasksDay_array_listener);

            chip_date.setClickable(false);
            chip_name.setClickable(true);
        }
    }

    public void check_permissions() {
        if (!PermissionsActivity.checkAllPermissions(this) || !LocationHelper.isGPSOn(this))
        {
            Intent pa = new Intent(this, PermissionsActivity.class);
            startActivity(pa);
            finish();
        }
    }

    public void showDaysHasTheyDatePassed()
    {
        refTasksDays.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    TasksDay tasksDay=data.child("Tasks Day Data").getValue(TasksDay.class);

                    if (isDateOfDayHasPassed(tasksDay)) {
                        AlertDialog.Builder adb;
                        adb = new AlertDialog.Builder(TasksDayListsActivity.this);
                        adb.setTitle("תאריך היעד של היום המרוכז '" + tasksDay.getTasksDayName() + "' עבר");
                        adb.setMessage("האם למחוק את היום המרוכז?");
                        adb.setIcon(R.drawable.time_icon);
                        adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refTasksDays.child(currentUser.getUid()).child(tasksDay.getTasksDayName()).removeValue();
                                Toast.makeText(TasksDayListsActivity.this, "מחיקת היום המרוכז בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                            }
                        });
                        adb.setNeutralButton("לא", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog ad = adb.create();
                        ad.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TasksDayListsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isDateOfDayHasPassed(TasksDay tasksDay)
    {
        String taskDay_date=tasksDay.getTasksDayDate();
        String[] date=taskDay_date.split("-");
        String task_date_time=Integer.parseInt(date[2])+1+"-"+date[1]+"-"+date[0];

        try {
            if (new SimpleDateFormat("dd-MM-yyyy", new Locale("he")).parse(task_date_time).before(new Date())) {
                return true;
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public void cancel_alarm(Task task)
    {
        //cancel alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getTaskAlarmId(), intent,  PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
    }
}