package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,PopupMenu.OnMenuItemClickListener {

    FirebaseUser currentUser;

    List list;
    List list_clicked;

    BottomNavigationView bottomNavigationView;

    ArrayList<String> lists_array = new ArrayList<String>();
    ArrayList<List> lists_values = new ArrayList<List>();
    String list_name;
    ListView lists_listview;
    TextView tv_lists_amount;

    BottomSheetDialog bottomSheetDialog_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomSheetDialog_list=(BottomSheetDialog) new BottomSheetDialog(MainActivity.this);

        tv_lists_amount=(TextView) findViewById(R.id.tv_lists_amount);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "My Lists" + "</font>"));

        currentUser = mAuth.getCurrentUser();

        lists_listview=(ListView) findViewById(R.id.lists_listview);
        lists_listview.setOnItemClickListener(this);
        lists_listview.setOnItemLongClickListener(this);
        lists_listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        read_lists();


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
                else if (id==R.id.tasks_day)
                {
                    Intent td=new Intent(MainActivity.this,TasksDayListsActivity.class);
                    startActivity(td);
                    finish();
                }
                return true;
            }
        });
        // overridePendingTransition(0,0);
    }

    public void move_login()
    {
        Intent la = new Intent(this, LoginActivity.class);
        startActivity(la);
        finish();
    }


//
//    public void create_tasks_day(View view) {
//        TasksDay tasksDay=new TasksDay("Example tasksday name","30.11.2021");
//        refTasksDays.child(namechild).child(tasksDay.getTasksDayName()).child("Tasks Day Data").setValue(tasksDay);
//        Task task=new Task("Example Task","address","30.11.2021","15:00","30.11.2021","encienciencwoincoenc","white","exampleuid");
//        refTasksDays.child(namechild).child(tasksDay.getTasksDayName()).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
//    }

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

    public void create_list(View view) {

        list_clicked=null;
        show_bottomSheetDialog();
    }

    public void show_bottomSheetDialog()
    {
        bottomSheetDialog_list=new BottomSheetDialog(this,R.style.BottomSheetTheme);

        bottomSheetDialog_list.setContentView(R.layout.bottom_sheet_layout_list);
        bottomSheetDialog_list.setCanceledOnTouchOutside(true);
        bottomSheetDialog_list.show();
    }

    public void add_list (View view){

        EditText et_list_name=(EditText) bottomSheetDialog_list.findViewById(R.id.et_list_name);
        String listName=et_list_name.getText().toString();

        String date=get_current_date();
        list=new List(listName,date);

        if (listName.isEmpty())
        {
            et_list_name.setError("List name is required!");
            et_list_name.requestFocus();
        }
        else if (list_clicked!=null) {
            list.setListCreationDate(list_clicked.getListCreationDate());

            DatabaseReference ref = refLists.child(currentUser.getUid()).child(list_clicked.getListName()).child("Tasks");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot data : dataSnapshot.getChildren()) {
                        Task task=data.child("Task Data").getValue(Task.class);
                        refLists.child(currentUser.getUid()).child(listName).child("Tasks").child(task.getTaskName()).child("Task Data").setValue(task);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            refLists.child(currentUser.getUid()).child(list_clicked.getListName()).removeValue();
            refLists.child(currentUser.getUid()).child(listName).child("List Data").setValue(list);

            Toast.makeText(this, "Update List Successfully", Toast.LENGTH_SHORT).show();
            bottomSheetDialog_list.cancel();
        }
        else if (lists_array.contains(listName))
        {
            et_list_name.setError("There is a List with this name!");
            et_list_name.requestFocus();
        }
        else
        {
            refLists.child(currentUser.getUid()).child(listName).child("List Data").setValue(list);
            Toast.makeText(this, "Add List Successfully", Toast.LENGTH_SHORT).show();

            bottomSheetDialog_list.cancel();

            list_clicked=null;
        }
    }

    public String get_current_date()
    {
        return new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(new Date());
    }

    public void read_lists()
    {
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
                    read_lists();
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
            ValueEventListener lists_array_listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dS) {
                    lists_values.clear();
                    lists_array.clear();
                    for(DataSnapshot data : dS.getChildren()) {
                        List stuTmp=data.child("List Data").getValue(List.class);
                        lists_values.add(stuTmp);
                        list_name = stuTmp.getListName();
                        lists_array.add(list_name);
                    }
                    CustomListAdapter customadp = new CustomListAdapter(MainActivity.this,
                            lists_array,lists_values);
                    lists_listview.setAdapter(customadp);
                    tv_lists_amount.setText("You have "+ lists_array.size()+ " lists");
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            };
            refLists.child(currentUser.getUid()).addValueEventListener(lists_array_listener);
        }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent ta = new Intent(this,TasksActivity.class);

        ta.putExtra("list_clicked",lists_values.get(position));
        ta.putExtra("reference","Lists");

        startActivity(ta);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        list_clicked=lists_values.get(position);
        showPopup(view);
        return true;
    }

    public void showPopup(View v)
    {
        PopupMenu popupMenu=new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.list_options);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int item_id=item.getItemId();
        if (item_id == R.id.update_list)
        {
            show_bottomSheetDialog();
            EditText et_list_name=(EditText) bottomSheetDialog_list.findViewById(R.id.et_list_name);
            Button add_list=(Button) bottomSheetDialog_list.findViewById(R.id.add_list);
            ImageView iv_list_layout=(ImageView) bottomSheetDialog_list.findViewById(R.id.iv_list_layout);
            et_list_name.setText(list_clicked.getListName());
            add_list.setText("Update List");
            iv_list_layout.setImageResource(R.drawable.update_list);
        }
        else if (item_id == R.id.delete_list)
        {
            AlertDialog.Builder adb;
            adb=new AlertDialog.Builder(this);
            adb.setTitle("Delete List");
            adb.setMessage("Are you sure you want delete "+list_clicked.getListName()+"?");
            adb.setIcon(R.drawable.delete_list);
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    refLists.child(currentUser.getUid()).child(list_clicked.getListName()).removeValue();
                    Toast.makeText(MainActivity.this, "Delete List Successfully", Toast.LENGTH_SHORT).show();
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
}