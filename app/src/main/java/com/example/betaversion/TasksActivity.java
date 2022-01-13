package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

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
                    Task stuTmp=data.child("Tasks").child("exampleTask").child("Task Data").getValue(Task.class);
                    tasks_values.add(stuTmp);
                    String taskName = stuTmp.getTaskName();
                    tasks_array.add(taskName);
                }
                ArrayAdapter<String> adp = new ArrayAdapter<String>(TasksActivity.this, R.layout.support_simple_spinner_dropdown_item, tasks_array);
                tasks_listview.setAdapter(adp);
                tv_tasks_amount.setText("You have "+ tasks_array.size()+ " lists");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TasksActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        refLists.child(currentUser.getUid()).addValueEventListener(tasks_array_listener);
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