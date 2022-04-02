package com.example.betaversion;

import static com.example.betaversion.FB_Ref.FBDB;
import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;
import static com.example.betaversion.FB_Ref.refUsers;
import static com.example.betaversion.FB_Ref.referenceStorage;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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
import com.google.firebase.database.core.Path;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import petrov.kristiyan.colorpicker.ColorPicker;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class TasksActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener ,EasyPermissions.PermissionCallbacks{

    public static final int REQUEST_CODE_LOCATION_PERMISSION =0;

    DatabaseReference reference;

    BottomNavigationView bottomNavigationView;

    FirebaseUser currentUser;

    String list_clicked_name;
    String list_clicked_date;
    Intent gi;

    com.example.betaversion.List list_clicked;
    TasksDay tasksDay_clicked;

    TextView tv_list_name;
    TextView tv_list_date;
    TextView tv_tasks_amount;

    ListView tasks_listview;

    ArrayList<String> tasks_array = new ArrayList<String>();
    ArrayList<Task> tasks_values = new ArrayList<Task>();

    BottomSheetDialog bottomSheetDialog_task;

    //Date and Time
    Calendar calendar = Calendar.getInstance();
    int year;
    int month;
    int day;

    String date, time, date_and_time = "", task_color = "#808080"; //task_color=white

    //image
    Uri imageUri;
    int PICK_IMAGE = 2;

    Task task_clicked;

    Boolean is_image_changed;

    int default_color;

    ProgressDialog progressDialog;

    ImageView cancel_bottom_sheet_dialog_task;

    String SELECT_DATE_AND_TIME = "בחר תאריך ושעה";
    String SELECT_TIME = "בחר שעה";

    SimpleDateFormat dateFormat_before = new SimpleDateFormat("dd-MM-yyyy", new Locale("he"));
    SimpleDateFormat dateFormat_after = new SimpleDateFormat("yyyy-MM-dd", new Locale("he"));

    Chip chip_name, chip_color, chip_creation_date, chip_target_date, chip_distance;

    ValueEventListener tasks_array_listener;

    String task_creationDate;
    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-M-dd", new Locale("he"));

    FusedLocationProviderClient fusedLocationProviderClient;
    CancellationTokenSource cancellationTokenSource;
    double current_latitude=0,current_longitude=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.task_icon);

        bottomSheetDialog_task = (BottomSheetDialog) new BottomSheetDialog(TasksActivity.this);

        currentUser = mAuth.getCurrentUser();

        tv_list_name = (TextView) findViewById(R.id.tv_list_name);
        tv_list_date = (TextView) findViewById(R.id.tv_list_date);
        tv_tasks_amount = (TextView) findViewById(R.id.tv_tasks_amount);

        get_data_from_intent();

        tasks_listview = (ListView) findViewById(R.id.tasks_listview);
        tasks_listview.setOnItemClickListener(this);
        tasks_listview.setOnItemLongClickListener(this);
        tasks_listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);

        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.empty);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.my_lists) {
                    Intent ma = new Intent(TasksActivity.this, MainActivity.class);
                    startActivity(ma);
                    finish();
                } else if (id == R.id.about) {
                    Intent ca = new Intent(TasksActivity.this, CreditsActivity.class);
                    startActivity(ca);
                    finish();
                } else if (id == R.id.settings) {
                    Intent sa = new Intent(TasksActivity.this, SettingsActivity.class);
                    startActivity(sa);
                    finish();
                } else if (id == R.id.tasks_day) {
                    Intent td = new Intent(TasksActivity.this, TasksDayListsActivity.class);
                    startActivity(td);
                    finish();
                }
                return true;
            }
        });

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "המטלות שלי" + "</font>"));

        read_tasks();

        task_clicked = null;
        is_image_changed = false;

        chip_name = (Chip) findViewById(R.id.sort_by_name);
        chip_color = (Chip) findViewById(R.id.sort_by_color);
        chip_creation_date = (Chip) findViewById(R.id.sort_by_creation_date);
        chip_target_date = (Chip) findViewById(R.id.sort_by_target_date);
        chip_distance = (Chip) findViewById(R.id.sort_by_distance);
        chip_name.setClickable(false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        cancellationTokenSource = new CancellationTokenSource();

        if (hasLocationPermissions())
        {
            if (isLocationEnabled())
            {
                set_currentLocation();
            }
            else
            {
                turnGPSOn();
            }
        }
        else
        {
            requestPermissions();
        }
    }

    public void get_data_from_intent() {
        gi = getIntent();
        String ref = gi.getStringExtra("reference");

        if (ref.equals("Lists")) {
            list_clicked = gi.getParcelableExtra("list_clicked");
            list_clicked_name = list_clicked.getListName();
            list_clicked_date = list_clicked.getListCreationDate();
        } else if (ref.equals("Tasks Days")) {
            tasksDay_clicked = gi.getParcelableExtra("tasksDay_clicked");
            list_clicked_name = tasksDay_clicked.getTasksDayName();
            list_clicked_date = tasksDay_clicked.getTasksDayDate();
            date = list_clicked_date;
        }

        try {
            Date result = dateFormat_after.parse(list_clicked_date);
            String list_clickedDate = dateFormat_before.format(result);
            tv_list_date.setText(list_clickedDate);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        tv_list_name.setText(list_clicked_name);
        reference = FBDB.getReference(ref);
    }

    public void read_tasks() {
        tasks_array_listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                tasks_values.clear();
                tasks_array.clear();
                for (DataSnapshot data : dS.getChildren()) {
                    Task task = data.child("Task Data").getValue(Task.class);
                    tasks_values.add(task);
                    String taskName = task.getTaskName();
                    tasks_array.add(taskName);
                }
                CustomTaskAdapter customadp = new CustomTaskAdapter(TasksActivity.this,
                        tasks_array, tasks_values);
                tasks_listview.setAdapter(customadp);

                tv_tasks_amount.setText("קיימות " + tasks_array.size() + " מטלות");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TasksActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").addValueEventListener(tasks_array_listener);
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
            adb.setTitle("התנתקות");
            adb.setMessage("אתה בטוח שברצונך להתנתק מהאפליקציה?");
            adb.setIcon(R.drawable.log_out_icon);
            adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
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
            adb.setNeutralButton("לא", new DialogInterface.OnClickListener() {
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

    public void create_task(View view) {
        change_data_to_default();
        show_bottomSheetDialog();
    }

    public void show_bottomSheetDialog() {
        bottomSheetDialog_task = new BottomSheetDialog(this, R.style.BottomSheetTheme);

        bottomSheetDialog_task.setContentView(R.layout.bottom_sheet_layout_task);
        bottomSheetDialog_task.setCancelable(false);
        bottomSheetDialog_task.show();

        TextView tv_task_date_and_time = (TextView) bottomSheetDialog_task.findViewById(R.id.tv_task_date_and_time);
        if (reference.equals(refLists)) {
            tv_task_date_and_time.setText(SELECT_DATE_AND_TIME);
        } else if (reference.equals(refTasksDays)) {
            tv_task_date_and_time.setText(SELECT_TIME);
        }

        cancel_bottom_sheet_dialog_task = (ImageView) bottomSheetDialog_task.findViewById(R.id.cancel_bottom_sheet_dialog_task);
        cancel_bottom_sheet_dialog_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog_task.cancel();
            }
        });
    }

    public void add_task(View view) {
        EditText et_task_name = (EditText) bottomSheetDialog_task.findViewById(R.id.et_task_name);
        EditText et_task_address = (EditText) bottomSheetDialog_task.findViewById(R.id.et_task_address);
        EditText et_task_notes = (EditText) bottomSheetDialog_task.findViewById(R.id.et_task_notes);
        TextView tv_task_date_and_time = (TextView) bottomSheetDialog_task.findViewById(R.id.tv_task_date_and_time);

        String taskName = et_task_name.getText().toString();
        String taskAddress = et_task_address.getText().toString();
        String taskNotes = et_task_notes.getText().toString();
        task_creationDate = get_current_date();
        try {
            Date result = dateFormat_before.parse(task_creationDate);
            task_creationDate = dateFormat_after.format(result);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (taskName.isEmpty()) {
            et_task_name.setError("כתוב את שם המטלה!");
            et_task_name.requestFocus();
        } else if (!taskAddress.isEmpty() && correct_address(taskAddress).equals("")) {
            et_task_address.setError("כתובת שגויה!");
            et_task_address.requestFocus();
        } else if (tv_task_date_and_time.getText().toString().equals(SELECT_DATE_AND_TIME) || tv_task_date_and_time.getText().toString().equals(SELECT_TIME)) {
            if (reference.equals(refLists)) {
                Toast.makeText(TasksActivity.this, SELECT_DATE_AND_TIME, Toast.LENGTH_SHORT).show();
            } else if (reference.equals(refTasksDays)) {
                Toast.makeText(TasksActivity.this, SELECT_TIME, Toast.LENGTH_SHORT).show();
            }
        } else if (task_clicked != null) {
            progressDialog = ProgressDialog.show(this, "מעדכן את פרטי המטלה", "טוען...", true);

            String file_name = "Images";
            if (reference.equals(refTasksDays)) {
                file_name = currentUser.getUid() + "/" + "Tasks Days Images/" + tasksDay_clicked.getTasksDayName() + "/" + taskName + " image.png";
            } else if (reference.equals(refLists)) {
                file_name = currentUser.getUid() + "/" + "Tasks Lists Images/" + list_clicked.getListName() + "/" + taskName + " image.png";
            }

            if (is_image_changed) {
                StorageReference fileRef = referenceStorage.child(file_name);
                fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUri = uri;
                                reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task_clicked.getTaskName()).child("Task Data").removeValue();
                                Task task = new Task(taskName, correct_address(taskAddress), date, time, task_clicked.getTaskCreationDate(), taskNotes, task_color, imageUri.toString(),0);
                                reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
                                Toast.makeText(TasksActivity.this, "עדכון המטלה בוצע בהצלחה", Toast.LENGTH_SHORT).show();
                                change_data_to_default();

                                progressDialog.dismiss();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TasksActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                    }
                });
            } else {
                reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task_clicked.getTaskName()).child("Task Data").removeValue();
                Task task = new Task(taskName, correct_address(taskAddress), date, time, task_clicked.getTaskCreationDate(), taskNotes, task_color, imageUri.toString(),0);
                reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
                Toast.makeText(TasksActivity.this, "עדכון המטלה בוצע בהצלחה", Toast.LENGTH_SHORT).show();
                change_data_to_default();
                progressDialog.dismiss();
            }
        } else if (tasks_array.contains(taskName)) {
            et_task_name.setError("קיימת מטלה עם שם זה!");
            et_task_name.requestFocus();
        } else {
            progressDialog = ProgressDialog.show(this, "יוצר את המטלה", "טוען...", true);

            String file_name = "Images";
            if (reference.equals(refTasksDays)) {
                file_name = currentUser.getUid() + "/" + "Tasks Days Images/" + tasksDay_clicked.getTasksDayName() + "/" + taskName + " image.png";
            } else if (reference.equals(refLists)) {
                file_name = currentUser.getUid() + "/" + "Tasks Lists Images/" + list_clicked.getListName() + "/" + taskName + " image.png";
            }

            StorageReference fileRef = referenceStorage.child(file_name);
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUri = uri;
                            Task task = new Task(taskName, correct_address(taskAddress), date, time, task_creationDate, taskNotes, task_color, imageUri.toString(),0);
                            reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
                            Toast.makeText(TasksActivity.this, "יצירת המטלה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                            change_data_to_default();

                            progressDialog.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TasksActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                }
            });
        }
    }

    public void change_data_to_default() {
        task_clicked = null;
        bottomSheetDialog_task.cancel();
        task_color = "#808080";
        date_and_time = "";
        imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.task_icon);
        is_image_changed = false;

        chip_name.setChecked(true);
        chip_name.setClickable(false);
        chip_color.setClickable(true);
        chip_creation_date.setClickable(true);
        chip_target_date.setClickable(true);
        chip_distance.setClickable(true);
    }

    public String get_current_date() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    public void set_date_and_time(View view) {
        if (reference.equals(refLists)) {
            select_date();
        } else if (reference.equals(refTasksDays)) {
            select_time();
        }
    }

    public void select_date() {
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TasksActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                //date=dayOfMonth+"-"+month+"-"+year;

                date = year + "-" + month + "-" + dayOfMonth;
                try {
                    Date result_date = inputDateFormat.parse(date);
                    date = dateFormat_after.format(result_date);
                } catch (ParseException e) {
                    Toast.makeText(TasksActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                select_time();
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()); // Disable Previous or Future Dates in Datepicker
        datePickerDialog.show();
    }

    public void select_time() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

                time = simpleTimeFormat.format(calendar.getTime());

                date_and_time = date + " " + time;
                try {
                    Date result = dateFormat_after.parse(date);
                    String old_date = dateFormat_before.format(result);
                    date_and_time = old_date + " " + time;
                } catch (Exception e) {
                    Toast.makeText(TasksActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                TextView tv_task_date_and_time = (TextView) bottomSheetDialog_task.findViewById(R.id.tv_task_date_and_time);
                tv_task_date_and_time.setText(date_and_time);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(TasksActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public void add_image(View view) {
        ImageView task_image = (ImageView) bottomSheetDialog_task.findViewById(R.id.task_image);

        String[] items = {"בחר תמונה מהגלריה", "בחר את תמונת ברירת המחדל"};
        AlertDialog.Builder adb;
        adb = new AlertDialog.Builder(this);
        adb.setTitle("תמונת המטלה");
        adb.setIcon(R.drawable.add_image_icon);
        adb.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, PICK_IMAGE);

                    is_image_changed = true;
                } else if (which == 1) {
                    task_image.setImageResource(R.drawable.task_icon);
                    imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.task_icon);

                    is_image_changed = true;
                }
            }
        });
        adb.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    public String correct_address(String address) {
        Geocoder geocoder = new Geocoder(TasksActivity.this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 6);
            Address user_address = addressList.get(0);
            LatLng latLng = new LatLng(user_address.getLatitude(), user_address.getLongitude());
            //Toast.makeText(TasksActivity.this, "Address: "+user_address.getAddressLine(0), Toast.LENGTH_SHORT).show();
            //Toast.makeText(TasksActivity.this, "Lat: "+latLng.latitude+", "+"Lng: "+latLng.longitude, Toast.LENGTH_SHORT).show();
            return user_address.getAddressLine(0);
        } catch (Exception e) {
            //Toast.makeText(TasksActivity.this, "Error Address!", Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView task_image = (ImageView) bottomSheetDialog_task.findViewById(R.id.task_image);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setBorderCornerLength(1)
                    .setBorderLineColor(Color.WHITE)
                    .setAutoZoomEnabled(true)
                    .setActivityTitle("חתוך תמונה")
                    .setFixAspectRatio(true)
                    .setCropMenuCropButtonTitle("סיום")
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                task_image.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void task_color(View view) {
        default_color = Color.parseColor("#808080");
        if (task_clicked != null) {
            default_color = Color.parseColor(task_clicked.getTaskColor());
        }

        ColorPicker colorPicker = new ColorPicker(this);
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#000000"); //Black
        colors.add("#ff0000"); //Red
        colors.add("#00ff00"); //Green
        colors.add("#0000ff"); //Blue
        colors.add("#808080"); //gray
        colorPicker.setColors(colors).setColumns(5).setColorButtonTickColor(Color.WHITE).setDefaultColorButton(default_color).setRoundColorButton(true).setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onChooseColor(int position, int color) {
                task_color = colors.get(position);
                CircleImageView btn_task_color = (CircleImageView) bottomSheetDialog_task.findViewById(R.id.btn_task_color);
                if (color == 0) {
                    Drawable c = new ColorDrawable(default_color);
                    btn_task_color.setImageDrawable(c);
                } else {
                    Drawable c = new ColorDrawable(color);
                    btn_task_color.setImageDrawable(c);
                }
            }

            @Override
            public void onCancel() {
            }
        }).show();
    }

    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.task_options);
        popupMenu.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        show_bottomSheetDialog();

        task_clicked = tasks_values.get(position);

        EditText et_task_name = (EditText) bottomSheetDialog_task.findViewById(R.id.et_task_name);
        EditText et_task_address = (EditText) bottomSheetDialog_task.findViewById(R.id.et_task_address);
        EditText et_task_notes = (EditText) bottomSheetDialog_task.findViewById(R.id.et_task_notes);
        TextView tv_task_date_and_time = (TextView) bottomSheetDialog_task.findViewById(R.id.tv_task_date_and_time);
        CircleImageView btn_task_color = (CircleImageView) bottomSheetDialog_task.findViewById(R.id.btn_task_color);
        ImageView task_image = (ImageView) bottomSheetDialog_task.findViewById(R.id.task_image);
        Button add_task = (Button) bottomSheetDialog_task.findViewById(R.id.add_task);

        et_task_name.setText(tasks_values.get(position).getTaskName());
        et_task_address.setText(tasks_values.get(position).getTaskAddress());
        et_task_notes.setText(tasks_values.get(position).getTaskNotes());
        time = tasks_values.get(position).getTaskHour();
        date = tasks_values.get(position).getTaskDay();

        date_and_time = date + " " + time;
        try {
            Date result = dateFormat_after.parse(date);
            String old_date = dateFormat_before.format(result);
            date_and_time = old_date + " " + time;
        } catch (Exception e) {
            Toast.makeText(TasksActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        tv_task_date_and_time.setText(date_and_time);

        task_color = tasks_values.get(position).getTaskColor();
        Drawable c = new ColorDrawable(Color.parseColor(task_color));
        btn_task_color.setImageDrawable(c);
        add_task.setText("עדכון המטלה");

        imageUri = Uri.parse(tasks_values.get(position).getTaskPictureUid());
        Glide.with(task_image.getContext()).load(imageUri).into(task_image);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        task_clicked = tasks_values.get(position);

        if (task_clicked.getTaskAddress().isEmpty()) {
            delete_task();
        } else {
            showPopup(view);
        }

        return true;
    }

    public void delete_task() {
        AlertDialog.Builder adb;
        adb = new AlertDialog.Builder(this);
        adb.setTitle("מחיקת המטלה");
        adb.setMessage("אתה בטוח שברצונך למחוק את המטלה " + task_clicked.getTaskName() + "?");
        adb.setIcon(R.drawable.delete_list);
        adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task_clicked.getTaskName()).child("Task Data").removeValue();
                Toast.makeText(TasksActivity.this, "מחיקת המטלה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
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

    // click in popup menu
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.delete_task) {
            delete_task();
        } else if (item_id == R.id.show_task_in_map) {
            Intent stma = new Intent(this, ShowTaskMapActivity.class);
            stma.putExtra("task_clicked", task_clicked);
            startActivity(stma);
        }
        return false;
    }


    public void sort_items(View view) {
        if (chip_distance.isChecked()) {
            if (isLocationEnabled())
            {
                update_task_currentLocation();

                Query query = reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").orderByChild("Task Data/taskDistance").startAt(0.001);
                query.addListenerForSingleValueEvent(tasks_array_listener);

                chip_name.setClickable(true);
                chip_color.setClickable(true);
                chip_creation_date.setClickable(true);
                chip_target_date.setClickable(true);
                chip_distance.setClickable(false);
            }
            else
            {
                turnGPSOn();
                chip_name.setChecked(true);
                chip_name.setClickable(false);
                chip_color.setClickable(true);
                chip_creation_date.setClickable(true);
                chip_target_date.setClickable(true);
                chip_distance.setClickable(true);
            }
        }
        else if (chip_name.isChecked()) {
            Query query = reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").orderByKey();
            query.addListenerForSingleValueEvent(tasks_array_listener);

            chip_name.setClickable(false);
            chip_color.setClickable(true);
            chip_creation_date.setClickable(true);
            chip_target_date.setClickable(true);
            chip_distance.setClickable(true);
        }
        else if (chip_color.isChecked()) {
            Query query = reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").orderByChild("Task Data/taskColor");
            query.addListenerForSingleValueEvent(tasks_array_listener);

            chip_name.setClickable(true);
            chip_color.setClickable(false);
            chip_creation_date.setClickable(true);
            chip_target_date.setClickable(true);
            chip_distance.setClickable(true);
        }
        else if (chip_creation_date.isChecked()) {
            Query query = reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").orderByChild("Task Data/taskCreationDate");
            query.addListenerForSingleValueEvent(tasks_array_listener);

            chip_name.setClickable(true);
            chip_color.setClickable(true);
            chip_creation_date.setClickable(false);
            chip_target_date.setClickable(true);
            chip_distance.setClickable(true);
        }
        else if (chip_target_date.isChecked()) {
            Query query = reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").orderByChild("Task Data/taskDay");
            query.addListenerForSingleValueEvent(tasks_array_listener);

            chip_name.setClickable(true);
            chip_color.setClickable(true);
            chip_creation_date.setClickable(true);
            chip_target_date.setClickable(false);
            chip_distance.setClickable(true);
        }
    }

    public void update_task_currentLocation()
    {
        set_currentLocation();
        Geocoder geocoder=new Geocoder(TasksActivity.this);
        List<Address> addressList;
        Address task_address;
        LatLng task_location = null;

        progressDialog=ProgressDialog.show(this,"מחשב מרחקים בין מיקומך הנוכחי לבין המטלות","טוען...",true);

        for (int position=0;position<tasks_values.size();position++)
        {
            Task task=tasks_values.get(position);

            if (!task.getTaskAddress().isEmpty())
            {
                try {
                    addressList=geocoder.getFromLocationName(task.getTaskAddress(),6);
                    task_address=addressList.get(0);
                    task_location = new LatLng(task_address.getLatitude(), task_address.getLongitude());
                }
                catch (Exception e)
                {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                double d = distance(current_latitude, task_location.latitude , current_longitude, task_location.longitude);
                String distance = String.valueOf(d).substring(0, 5);
                double final_distance=Double.parseDouble(distance);

                task.setTaskDistance(final_distance);
                reference.child(currentUser.getUid()).child(list_clicked_name).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
            }
        }
        progressDialog.dismiss();
    }

    public void set_currentLocation()
    {
        progressDialog=ProgressDialog.show(this,"מוצא את המיקום הנוכחי","טוען...",true);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        com.google.android.gms.tasks.Task<Location> currentLocationTask = fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.getToken()
        );
        currentLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                current_latitude=location.getLatitude();
                current_longitude=location.getLongitude();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
    }

    public static double distance(double lat1, double lat2, double lon1, double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles 6371;
        double r = 3956;

        // calculate the result
        return(c * r);
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public void turnGPSOn() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        com.google.android.gms.tasks.Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {


            @Override
            public void onComplete(com.google.android.gms.tasks.Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        TasksActivity.this,
                                        101);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    public boolean hasLocationPermissions() {
        return EasyPermissions.hasPermissions(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        );
    }

    public void requestPermissions() {
        if (hasLocationPermissions())
        {
            return;
        }
        EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
        );

        EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        );
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (isLocationEnabled())
        {
            set_currentLocation();
        }
        else
        {
            turnGPSOn();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms))
        {
            AppSettingsDialog.Builder builder = new AppSettingsDialog.Builder(this);
            builder.build().show();
        }
        else
        {
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
}