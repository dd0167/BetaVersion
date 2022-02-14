package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import petrov.kristiyan.colorpicker.ColorPicker;

public class TasksActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    BottomNavigationView bottomNavigationView;

    FirebaseUser currentUser;

    String list_clicked_name;
    String list_clicked_date;
    Intent gi;
    com.example.betaversion.List list_clicked;

    TextView tv_list_name;
    TextView tv_list_date;
    TextView tv_tasks_amount;

    ListView tasks_listview;

    ArrayList<String> tasks_array = new ArrayList<String>();
    ArrayList<Task> tasks_values = new ArrayList<Task>();

    BottomSheetDialog bottomSheetDialog_task;

    //Date and Time
    Calendar calendar=Calendar.getInstance();
    int year;
    int month;
    int day;

    String date,time,date_and_time="",task_color="#808080"; //task_color=white

    //image
    Uri imageUri;
    int PICK_IMAGE=2;

    Task task_clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.task_icon);

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

        list_clicked=gi.getParcelableExtra("list_clicked");
        list_clicked_name = list_clicked.getListName();
        list_clicked_date = list_clicked.getListCreationDate();
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
                else if (id==R.id.tasks_day)
                {
                    Intent td=new Intent(TasksActivity.this,TasksDayListsActivity.class);
                    startActivity(td);
                    finish();
                }
                return true;
            }
        });

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "My Tasks" + "</font>"));

        read_tasks();

        task_clicked=null;
    }

    public void read_tasks()
    {
        ValueEventListener tasks_array_listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                tasks_values.clear();
                tasks_array.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    Task task=data.child("Task Data").getValue(Task.class);
                    tasks_values.add(task);
                    String taskName = task.getTaskName();
                    tasks_array.add(taskName);
                }
                CustomTaskAdapter customadp = new CustomTaskAdapter(TasksActivity.this,
                        tasks_array,tasks_values);
                tasks_listview.setAdapter(customadp);

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
        change_data_to_default();
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
        TextView tv_task_date_and_time=(TextView) bottomSheetDialog_task.findViewById(R.id.tv_task_date_and_time);

        String taskName=et_task_name.getText().toString();
        String taskAddress=et_task_address.getText().toString();
        String taskNotes=et_task_notes.getText().toString();
        String task_creationDate=get_current_date();

        if (taskName.isEmpty())
        {
            et_task_name.setError("Task name is required!");
            et_task_name.requestFocus();
        }
        else if (!taskAddress.isEmpty() && correct_address(taskAddress).equals(""))
        {
            et_task_address.setError("Error address!");
            et_task_address.requestFocus();
        }
        else if (tv_task_date_and_time.getText().toString().equals("Select Date And Time") )
        {
            Toast.makeText(TasksActivity.this, "Select Date And Time", Toast.LENGTH_SHORT).show();
        }
        else if(task_clicked!=null)
        {
            refLists.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task_clicked.getTaskName()).child("Task Data").removeValue();
            Task task=new Task(taskName,correct_address(taskAddress),date,time,task_clicked.getTaskCreationDate(),taskNotes,task_color,imageUri.toString());
            refLists.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
            Toast.makeText(this, "Update Task Successfully", Toast.LENGTH_SHORT).show();
            change_data_to_default();
        }
        else if (tasks_array.contains(taskName))
        {
            et_task_name.setError("There is a task with this name!");
            et_task_name.requestFocus();
        }
        else
        {
            Task task=new Task(taskName,correct_address(taskAddress),date,time,task_creationDate,taskNotes,task_color,imageUri.toString());
            refLists.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
            Toast.makeText(this, "Add Task Successfully", Toast.LENGTH_SHORT).show();
            change_data_to_default();
        }
    }

    public void change_data_to_default()
    {
        task_clicked=null;
        bottomSheetDialog_task.cancel();
        task_color="#808080";
        date_and_time="";
        imageUri= Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.task_icon);
    }

    public String get_current_date()
    {
        return new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(new Date());
    }

    public void set_date_and_time(View view) {
        select_date();
    }

    public void select_date()
    {
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TasksActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                date=dayOfMonth+"-"+month+"-"+year;
                select_time();
            }
        },year,month,day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()); // Disable Previous or Future Dates in Datepicker
        datePickerDialog.show();
    }

    public void select_time()
    {
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

        TimePickerDialog timePickerDialog=new TimePickerDialog(TasksActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
        timePickerDialog.show();
    }

    public void add_image(View view) {
        ImageView task_image=(ImageView) bottomSheetDialog_task.findViewById(R.id.task_image);

        String[] items={"Select Image From Gallery","Select The Default Image"};
        AlertDialog.Builder adb;
        adb=new AlertDialog.Builder(this);
        adb.setTitle("Change Profile Image");
        adb.setIcon(R.drawable.add_image_icon);
        adb.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0)
                {
                    Intent galleryIntent=new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent,PICK_IMAGE);
                }
                else if (which==1)
                {
                    task_image.setImageResource(R.drawable.task_icon);
                    imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.task_icon);
                }
            }
        });
        adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog ad= adb.create();
        ad.show();
    }

    public String correct_address(String address)
    {
        Geocoder geocoder=new Geocoder(TasksActivity.this);
        try {
            List<Address> addressList=geocoder.getFromLocationName(address,6);
            Address user_address=addressList.get(0);
            LatLng latLng = new LatLng(user_address.getLatitude(), user_address.getLongitude());
            //Toast.makeText(TasksActivity.this, "Address: "+user_address.getAddressLine(0), Toast.LENGTH_SHORT).show();
            //Toast.makeText(TasksActivity.this, "Lat: "+latLng.latitude+", "+"Lng: "+latLng.longitude, Toast.LENGTH_SHORT).show();
            return user_address.getAddressLine(0);
        }
        catch (Exception e) {
            //Toast.makeText(TasksActivity.this, "Error Address!", Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView task_image=(ImageView) bottomSheetDialog_task.findViewById(R.id.task_image);

        if (requestCode==PICK_IMAGE && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setBorderCornerLength(1)
                    .setBorderLineColor(Color.WHITE)
                    .setAutoZoomEnabled(true)
                    .setActivityTitle("Crop Image")
                    .setFixAspectRatio(true)
                    .setCropMenuCropButtonTitle("Done")
                    .start(this);
        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK)
            {
                imageUri=result.getUri();
                task_image.setImageURI(imageUri);
            }
            else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error=result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void task_color(View view) {
        ColorPicker colorPicker=new ColorPicker(this);
        ArrayList<String> colors=new ArrayList<>();
        colors.add("#000000"); //Black
        colors.add("#ff0000"); //Red
        colors.add("#00ff00"); //Green
        colors.add("#0000ff"); //Blue
        colors.add("#808080"); //gray
        colorPicker.setColors(colors).setColumns(5).setColorButtonTickColor(Color.WHITE).setDefaultColorButton(Color.WHITE).setRoundColorButton(true).setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
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

    public void showPopup(View v)
    {
        PopupMenu popupMenu=new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.task_options);
        popupMenu.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        show_bottomSheetDialog();

        task_clicked=tasks_values.get(position);

        EditText et_task_name=(EditText) bottomSheetDialog_task.findViewById(R.id.et_task_name);
        EditText et_task_address=(EditText) bottomSheetDialog_task.findViewById(R.id.et_task_address);
        EditText et_task_notes=(EditText) bottomSheetDialog_task.findViewById(R.id.et_task_notes);
        TextView tv_task_date_and_time=(TextView) bottomSheetDialog_task.findViewById(R.id.tv_task_date_and_time);
        CircleImageView btn_task_color=(CircleImageView) bottomSheetDialog_task.findViewById(R.id.btn_task_color);
        ImageView task_image=(ImageView) bottomSheetDialog_task.findViewById(R.id.task_image);
        Button add_task=(Button) bottomSheetDialog_task.findViewById(R.id.add_task);

        et_task_name.setText(tasks_values.get(position).getTaskName());
        et_task_address.setText(tasks_values.get(position).getTaskAddress());
        et_task_notes.setText(tasks_values.get(position).getTaskNotes());
        time=tasks_values.get(position).getTaskHour();
        date=tasks_values.get(position).getTaskDay();
        date_and_time=date+" "+time;
        tv_task_date_and_time.setText(date_and_time);
        task_color=tasks_values.get(position).getTaskColor();
        Drawable c = new ColorDrawable(Color.parseColor(task_color));
        btn_task_color.setImageDrawable(c);
        imageUri=Uri.parse(tasks_values.get(position).getTaskPictureUid());
        task_image.setImageURI(imageUri);
        add_task.setText("Update Task");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        task_clicked=tasks_values.get(position);

        if (task_clicked.getTaskAddress().isEmpty())
        {
            delete_task();
        }
        else
        {
            showPopup(view);
        }

        return true;
    }

    public void delete_task()
    {
        AlertDialog.Builder adb;
        adb=new AlertDialog.Builder(this);
        adb.setTitle("Delete List");
        adb.setMessage("Are you sure you want delete "+task_clicked.getTaskName()+"?");
        adb.setIcon(R.drawable.delete_list);
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refLists.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task_clicked.getTaskName()).child("Task Data").removeValue();
                Toast.makeText(TasksActivity.this, "Delete List Successfully", Toast.LENGTH_SHORT).show();
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

    // click in popup menu
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int item_id=item.getItemId();
        if (item_id == R.id.delete_task)
        {
            delete_task();
        }
        else if (item_id == R.id.show_task_in_map)
        {
            Intent stma = new Intent(this, ShowTaskMapActivity.class);
            stma.putExtra("task_clicked", task_clicked);
            startActivity(stma);
        }
        return false;
    }
}