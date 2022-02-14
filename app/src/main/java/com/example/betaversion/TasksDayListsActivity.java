package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "My Tasks Days" + "</font>"));

        bottomSheetDialog_tasksDay=(BottomSheetDialog) new BottomSheetDialog(TasksDayListsActivity.this);

        tv_tasksDay_amount=(TextView) findViewById(R.id.tv_tasksDay_amount);

        tasksDay_listview=(ListView) findViewById(R.id.tasksDay_listview);
        tasksDay_listview.setOnItemClickListener(this);
        tasksDay_listview.setOnItemLongClickListener(this);
        tasksDay_listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        read_tasksDays();
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
            adb.setTitle("No Internet");
            adb.setMessage("Unable to read data");
            adb.setIcon(R.drawable.no_wifi);
            adb.setCancelable(false);
            adb.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    read_tasksDays();
                }
            });
            adb.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
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
            ValueEventListener tasksDay_array_listener = new ValueEventListener() {
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
//                    CustomListAdapter customadp = new CustomListAdapter(MainActivity.this,
//                            lists_array,lists_values);
//                    lists_listview.setAdapter(customadp);

                    ArrayAdapter<String> adp=new ArrayAdapter<String>(TasksDayListsActivity.this,R.layout.support_simple_spinner_dropdown_item,tasksDay_array);
                    tasksDay_listview.setAdapter(adp);
                    tv_tasksDay_amount.setText("You have "+ tasksDay_array.size()+ " tasks days");
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
        bottomSheetDialog_tasksDay.setContentView(R.layout.bottom_sheet_layout_tasks_day);
        bottomSheetDialog_tasksDay.setCanceledOnTouchOutside(true);
        bottomSheetDialog_tasksDay.show();
    }

    public void add_tasksDay (View view){

        EditText et_tasksDay_name=(EditText) bottomSheetDialog_tasksDay.findViewById(R.id.et_tasksDay_name);
        TextView tv_tasksDay_date=(TextView) bottomSheetDialog_tasksDay.findViewById(R.id.tv_tasksDay_date);
        String tasksDayName=et_tasksDay_name.getText().toString();

        //String date=set_tasksDay_date(view);
        String date="14-02-2022";
        tasksDay=new TasksDay(tasksDayName,date);

        if (tasksDayName.isEmpty())
        {
            et_tasksDay_name.setError("Tasks Day name is required!");
            et_tasksDay_name.requestFocus();
        }
        else if (tv_tasksDay_date.getText().toString().equals("Select Tasks Day Date") )
        {
            Toast.makeText(TasksDayListsActivity.this, "Select Date", Toast.LENGTH_SHORT).show();
        }
        else if (tasksDay_clicked!=null) {
            // copy and edit from main!!!!! ///////////////////////////////////////////////////////////////////////////////////
        }
        else if (tasksDay_array.contains(tasksDayName))
        {
            et_tasksDay_name.setError("There is a Tasks Day with this name!");
            et_tasksDay_name.requestFocus();
        }
        else
        {
            refTasksDays.child(currentUser.getUid()).child(tasksDayName).child("Tasks Day Data").setValue(tasksDay);
            Toast.makeText(this, "Add Tasks Day Successfully", Toast.LENGTH_SHORT).show();

            bottomSheetDialog_tasksDay.cancel();

            tasksDay_clicked=null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}