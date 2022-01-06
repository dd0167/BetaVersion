package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;
import static com.example.betaversion.FB_Ref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    User user;
    String namechild;
    List list;
    BottomNavigationView bottomNavigationView;

    TextView taskDate;

    ArrayList<String> user_list = new ArrayList<String>();
    ArrayList<List> user_values = new ArrayList<List>();
    ArrayAdapter<String> adp;
    String list_name;
    ListView user_listview;

    DatePickerDialog.OnDateSetListener mDateSetListener;
    Calendar calendar=Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        taskDate=(TextView) findViewById(R.id.taskDate);
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                taskDate.setText(date);
            }
        };





        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "My Lists" + "</font>"));

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            user=new User(currentUser.getUid()+"","Dean","David","17","Home",currentUser.getEmail()+"","0544953999", "Uidpicture");
        }

        namechild=user.getUserFirstName()+" "+user.getUserLastName();

        list=new List("Example List","30.11.2021");






        //refLists.child(currentUser.getUid()).child(list.getListName()).child("List Data").setValue(list);

        user_listview=(ListView) findViewById(R.id.user_listview);

        user_listview.setOnItemClickListener(this);
        user_listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ValueEventListener user_list_listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                user_values.clear();
                user_list.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    List stuTmp=data.getValue(List.class);
                    user_values.add(stuTmp);
                    list_name = stuTmp.getListName();
                    user_list.add(list_name);
                }
                CustomListAdapter customadp = new CustomListAdapter(getApplicationContext(),
                        user_list);
                user_listview.setAdapter(customadp);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        refLists.child(currentUser.getUid()).child(list.getListName()).addValueEventListener(user_list_listener);








        bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);

        bottomNavigationView.getMenu().findItem(R.id.my_lists).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.my_lists);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id==R.id.my_lists)
                {
                    Intent ma = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(ma);
                    finish();
                }
                else if (id==R.id.about)
                {
                    Intent ca = new Intent(MainActivity.this, CreditsActivity.class);
                    startActivity(ca);
                    finish();
                }
                else if (id==R.id.settings)
                {
                    Intent sa = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(sa);
                    finish();
                }
                return true;
            }
        });
        // overridePendingTransition(0,0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void log_out(View view) {
        mAuth.signOut();
        SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("stayConnect",false);
        editor.commit();
        move_login();
    }

    public void move_login()
    {
        Intent la = new Intent(this, LoginActivity.class);
        startActivity(la);
        finish();
    }

    public void move_main()
    {
        Intent ma = new Intent(this, MainActivity.class);
        startActivity(ma);
        finish();
    }

    public void create_user(View view) {
        refUsers.child(namechild).child("User Data").setValue(user);
    }

    public void create_list(View view) {

        refLists.child(namechild).child(list.getListName()).child("List Data").setValue(list);
    }

    public void create_task(View view) {
        Task task=new Task("Example Task","address","30.11.2021","15:00","30.11.2021","encienciencwoincoenc","white","exampleuid");
        refLists.child(namechild).child(list.getListName()).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
    }

    public void create_tasks_day(View view) {
        TasksDay tasksDay=new TasksDay("Example tasksday name","30.11.2021");
        refTasksDays.child(namechild).child(tasksDay.getTasksDayName()).child("Tasks Day Data").setValue(tasksDay);
        Task task=new Task("Example Task","address","30.11.2021","15:00","30.11.2021","encienciencwoincoenc","white","exampleuid");
        refTasksDays.child(namechild).child(tasksDay.getTasksDayName()).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        String title=item.getTitle().toString();
        if (title.equals("Log Out"))
        {
            AlertDialog.Builder adb;
            adb=new AlertDialog.Builder(this);
            adb.setTitle("Log Out");
            adb.setMessage("Are you sure you want log out?");
            adb.setIcon(R.drawable.log_out_icon);
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.signOut();
                    SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("stayConnect",false);
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
            AlertDialog ad= adb.create();
            ad.show();
        }
        return true;
    }

    public void click(View view) {
        Toast.makeText(this, "try" , Toast.LENGTH_SHORT).show();
    }

    public void get_date(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                date=dayOfMonth+"-"+month+"-"+year;



                SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);

                try {
                    Date result_date=inputDateFormat.parse(date);
                    String outputDateString = dateFormat.format(result_date);
                    String[] items1 = outputDateString.split(" ");
                    String day = items1[0];
                    String dd = items1[1];
                    String mon = items1[2];
                    taskDate.setText(day+" "+dd+" "+mon);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },year,month,day);
        datePickerDialog.show();



    }
}