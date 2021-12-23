package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;
import static com.example.betaversion.FB_Ref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    User user;
    String namechild;
    List list;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id==R.id.home)
                {
                    //Toast.makeText(MainActivity.this, "hi", Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.Search)
                {
//                    Intent ca = new Intent(MainActivity.this, CreditsActivity.class);
//                    startActivity(ca);
                    //overridePendingTransition(0,0);
                }
                return true;
            }
        });
        //bottomNavigationView.setSelectedItemId(R.id.settings);




        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "My Lists" + "</font>"));

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            user=new User(currentUser.getUid()+"","Dean","David","17","Home",currentUser.getEmail()+"","0544953999", "Uidpicture");
        }

        namechild=user.getUserFirstName()+" "+user.getUserLastName();

        list=new List("Example List","30.11.2021");
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
        if (title.equals("My Lists"))
        {
            move_main();
        }
        else if (title.equals("About"))
        {
            Intent in = new Intent(this, CreditsActivity.class);
            startActivity(in);
        }
        else if (title.equals("Settings"))
        {
            Intent in=new Intent(this,SettingsActivity.class);
            startActivity(in);
        }
        else if (title.equals("Log Out"))
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
        else if (title.equals("Back"))
        {
            super.onBackPressed();
        }
        return true;
    }

    public void click(View view) {
        Toast.makeText(this, "try" , Toast.LENGTH_SHORT).show();
    }
}