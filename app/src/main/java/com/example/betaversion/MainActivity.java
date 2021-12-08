package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;
import static com.example.betaversion.FB_Ref.refUsers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    User user;
    String namechild;
    List list;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0C000000")));

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            user=new User(currentUser.getUid()+"","Dean","David","17","Home",currentUser.getEmail()+"","0544953999","Uidpicture");
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
            Intent in=new Intent(this,MainActivity.class);
            startActivity(in);
            finish();
        }
        else if (title.equals("About"))
        {
            Intent in=new Intent(this,CreditsActivity.class);
            startActivity(in);
            finish();
        }
        return true;
    }
}