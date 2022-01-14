package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import petrov.kristiyan.colorpicker.ColorPicker;

public class TasksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, PopupMenu.OnMenuItemClickListener{

    BottomNavigationView bottomNavigationView;

    FirebaseUser currentUser;

    String list_clicked_name;
    String list_clicked_date;
    Intent gi;

    TextView tv_list_name;
    TextView tv_list_date;
    TextView tv_tasks_amount;

    ListView tasks_listview;

    ArrayList<String> tasks_array = new ArrayList<String>();
    ArrayList<Task> tasks_values = new ArrayList<Task>();

    BottomSheetDialog bottomSheetDialog_task;

    //Date and Time
    Calendar calendar=Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    String date,time,date_and_time="",task_color="";

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.add_image_icon);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////

        bottomSheetDialog_task=(BottomSheetDialog) new BottomSheetDialog(TasksActivity.this);

        currentUser = mAuth.getCurrentUser();

        tv_list_name=(TextView) findViewById(R.id.tv_list_name);
        tv_list_date=(TextView) findViewById(R.id.tv_list_date);
        tv_tasks_amount=(TextView) findViewById(R.id.tv_tasks_amount);

        tasks_listview=(ListView) findViewById(R.id.tasks_listview);
        tasks_listview.setOnItemClickListener(this);
        tasks_listview.setOnItemLongClickListener(this);
        tasks_listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        gi = getIntent();

        list_clicked_name = gi.getStringExtra("list_clicked_name");
        list_clicked_date = gi.getStringExtra("list_clicked_date");
        tv_list_name.setText(list_clicked_name);
        tv_list_date.setText(list_clicked_date);

        bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);

        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.empty);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id==R.id.my_lists)
                {
                    Intent ma = new Intent(TasksActivity.this,MainActivity.class);
                    startActivity(ma);
                    finish();
                }
                else if (id==R.id.about)
                {
                    Intent ca = new Intent(TasksActivity.this, CreditsActivity.class);
                    startActivity(ca);
                    finish();
                }
                else if (id==R.id.settings)
                {
                    Intent sa = new Intent(TasksActivity.this, SettingsActivity.class);
                    startActivity(sa);
                    finish();
                }
                return true;
            }
        });

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "My Tasks" + "</font>"));

        read_tasks();
    }

    public void read_tasks()
    {
        ValueEventListener tasks_array_listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                tasks_values.clear();
                tasks_array.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    Task stuTmp=data.child("Task Data").getValue(Task.class);
                    tasks_values.add(stuTmp);
                    String taskName = stuTmp.getTaskName();
                    tasks_array.add(taskName);
                }
                ArrayAdapter<String> adp = new ArrayAdapter<String>(TasksActivity.this, R.layout.support_simple_spinner_dropdown_item, tasks_array);
                tasks_listview.setAdapter(adp);
                tv_tasks_amount.setText("You have "+ tasks_array.size()+ " tasks");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TasksActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        refLists.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").addValueEventListener(tasks_array_listener);
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

    public void create_task(View view) {
        show_bottomSheetDialog();
    }

    public void show_bottomSheetDialog()
    {
        bottomSheetDialog_task.setContentView(R.layout.bottom_sheet_layout_task);
        bottomSheetDialog_task.setCanceledOnTouchOutside(true);
        bottomSheetDialog_task.show();
    }

    public void add_task(View view) {
        EditText et_task_name=(EditText) bottomSheetDialog_task.findViewById(R.id.et_task_name);
        EditText et_task_address=(EditText) bottomSheetDialog_task.findViewById(R.id.et_task_address);
        EditText et_task_notes=(EditText) bottomSheetDialog_task.findViewById(R.id.et_task_notes);

        String taskName=et_task_name.getText().toString();
        String taskAddress=et_task_address.getText().toString();
        String taskNotes=et_task_notes.getText().toString();
        String task_creationDate=get_current_date();

        if (task_color.isEmpty())
        {
            Toast.makeText(TasksActivity.this, "Select Color", Toast.LENGTH_SHORT).show();
        }
        else if (taskName.isEmpty())
        {
            et_task_name.setError("Task name is required!");
            et_task_name.requestFocus();
        }
        else if (taskAddress.isEmpty())
        {
            et_task_address.setError("Task address is required!");
            et_task_address.requestFocus();
        }
        else if (date_and_time.isEmpty())
        {
            Toast.makeText(TasksActivity.this, "Select Date And Time", Toast.LENGTH_SHORT).show();
        }
        else if (tasks_array.contains(taskName))
        {
            et_task_name.setError("There is a task with this name!");
            et_task_name.requestFocus();
        }
        else
        {
            Task task=new Task(taskName,taskAddress,date,time,task_creationDate,taskNotes,task_color,imageUri.toString());
            refLists.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
            Toast.makeText(this, "Add Task Successfully", Toast.LENGTH_SHORT).show();
            bottomSheetDialog_task.cancel();
            task_color="";
            date_and_time="";
        }
    }

    public String get_current_date()
    {
        return new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(new Date());
    }

    public void set_date_and_time(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(TasksActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                date=dayOfMonth+"-"+month+"-"+year;

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleTimeFormat=new SimpleDateFormat("HH:mm");

                        time=simpleTimeFormat.format(calendar.getTime());

                        date_and_time=date+" "+time;

                        TextView tv_task_date_and_time=(TextView) bottomSheetDialog_task.findViewById(R.id.tv_task_date_and_time);
                        tv_task_date_and_time.setText(date_and_time);
                    }
                };

                new TimePickerDialog(TasksActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        },year,month,day);
        datePickerDialog.show();
    }

    public void add_image(View view) {

    }

    public void task_color(View view) {
        ColorPicker colorPicker=new ColorPicker(this);
        ArrayList<String> colors=new ArrayList<>();
        colors.add("#000000"); //Black
        colors.add("#ff0000"); //Red
        colors.add("#00ff00"); //Green
        colors.add("#0000ff"); //Blue
        colors.add("#ffffff"); //White
        colorPicker.setColors(colors).setColumns(5).setColorButtonTickColor(Color.GRAY).setDefaultColorButton(Color.WHITE).setRoundColorButton(true).setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onChooseColor(int position, int color) {
                task_color=colors.get(position);
                CircleImageView btn_task_color=(CircleImageView) bottomSheetDialog_task.findViewById(R.id.btn_task_color);
                Drawable c = new ColorDrawable(color);
                btn_task_color.setImageDrawable(c);
            }

            @Override
            public void onCancel() {
            }
        }).show();
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