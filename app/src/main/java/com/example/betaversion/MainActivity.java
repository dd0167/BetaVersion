package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;
import static com.example.betaversion.FB_Ref.refUsers;
import static com.example.betaversion.FB_Ref.referenceStorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * מסך "הרשימות שלי".
 */
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
    ImageView cancel_bottom_sheet_dialog_list;

    Chip chip_name;
    Chip chip_date;

    ValueEventListener lists_array_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomSheetDialog_list=(BottomSheetDialog) new BottomSheetDialog(MainActivity.this);

        tv_lists_amount=(TextView) findViewById(R.id.tv_lists_amount);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "הרשימות שלי" + "</font>"));

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

        chip_name=(Chip) findViewById(R.id.sort_by_name);
        chip_date=(Chip) findViewById(R.id.sort_by_date);
        chip_name.setClickable(false);

        check_permissions();
    }

    /**
     * מעבר למסך הכניסה.
     */
    public void move_login()
    {
        Intent la = new Intent(this, LoginActivity.class);
        startActivity(la);
        finish();
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
                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // Delete the file
                            String deleteFileName2 = "User Images/" + currentUser.getUid() + " image.png";
                            StorageReference desRef = referenceStorage.child(deleteFileName2);
                            desRef.delete().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            refUsers.child(currentUser.getUid()).removeValue();
                            refTasksDays.child(currentUser.getUid()).removeValue();
                            refLists.child(currentUser.getUid()).removeValue();

                            Toast.makeText(MainActivity.this, "מחיקת החשבון בוצעה בהצלחה", Toast.LENGTH_SHORT).show();

                            move_login();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    /**
     * יצירת רשימה.
     *
     * @param view the view
     */
    public void create_list(View view) {

        list_clicked=null;
        show_bottomSheetDialog();
    }

    /**
     * פתיחת מסך הקלט.
     */
    public void show_bottomSheetDialog()
    {
        bottomSheetDialog_list=new BottomSheetDialog(this,R.style.BottomSheetTheme);

        bottomSheetDialog_list.setContentView(R.layout.bottom_sheet_layout_list);
        bottomSheetDialog_list.show();

        cancel_bottom_sheet_dialog_list=(ImageView) bottomSheetDialog_list.findViewById(R.id.cancel_bottom_sheet_dialog_list);
        cancel_bottom_sheet_dialog_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog_list.cancel();
            }
        });
    }

    /**
     * הוספת הרשימה.
     *
     * @param view the view
     */
    public void add_list (View view){

        EditText et_list_name=(EditText) bottomSheetDialog_list.findViewById(R.id.et_list_name);
        String listName=et_list_name.getText().toString();

        String date=get_current_date();
        list=new List(listName,date);

        if (listName.isEmpty())
        {
            et_list_name.setError("כתוב את שם הרשימה!");
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

            Toast.makeText(this, "עדכון הרשימה בוצע בהצלחה", Toast.LENGTH_SHORT).show();
            bottomSheetDialog_list.cancel();
        }
        else if (lists_array.contains(listName))
        {
            et_list_name.setError("קיימת רשימה עם שם זה!");
            et_list_name.requestFocus();
        }
        else
        {
            refLists.child(currentUser.getUid()).child(listName).child("List Data").setValue(list);
            Toast.makeText(this, "יצירת הרשימה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();

            bottomSheetDialog_list.cancel();

            list_clicked=null;
        }
        chip_name.setChecked(true);
        chip_name.setClickable(false);
        chip_date.setClickable(true);
    }

    /**
     * קבלת התאריך הנוכחי.
     *
     * @return the current date
     */
    public String get_current_date()
    {
        return new SimpleDateFormat("yyyy-MM-dd",new Locale("he")).format(new Date());
    }

    /**
     *   קריאת הרשימות מ-Firebase Realtime Database.
     */
    public void read_lists()
    {
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
                    read_lists();
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
            lists_array_listener = new ValueEventListener() {
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
                    tv_lists_amount.setText("קיימות "+ lists_array.size()+ " רשימות");
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            };
            refLists.child(currentUser.getUid()).addValueEventListener(lists_array_listener);
        }
    }

    /**
     * בודק האם יש אינטרנט.
     *
     * @return the boolean
     */
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

    /**
     * הצגת תפריט לרשימה.
     *
     * @param v the v
     */
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
            add_list.setText("עדכון הרשימה");
            iv_list_layout.setImageResource(R.drawable.update_list);
        }
        else if (item_id == R.id.delete_list)
        {
            AlertDialog.Builder adb;
            adb=new AlertDialog.Builder(this);
            adb.setTitle("מחיקת הרשימה");
            adb.setMessage("אתה בטוח שברצונך למחוק את הרשימה '"+list_clicked.getListName()+"'?");
            adb.setIcon(R.drawable.delete_list);
            adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    refLists.child(currentUser.getUid()).child(list_clicked.getListName()).child("Tasks").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Task task = data.child("Task Data").getValue(Task.class);
                                AlarmHelper.cancel_alarm(task,MainActivity.this);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    refLists.child(currentUser.getUid()).child(list_clicked.getListName()).removeValue();
                    Toast.makeText(MainActivity.this, "מחיקת הרשימה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
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

    /**
     * מיון הרשימות.
     *
     * @param view the view
     */
    public void sort_items(View view) {
        if (chip_name.isChecked())
        {
            Query query=refLists.child(currentUser.getUid()).orderByKey();
            query.addListenerForSingleValueEvent(lists_array_listener);

            chip_name.setClickable(false);
            chip_date.setClickable(true);
        }
        else if (chip_date.isChecked())
        {
            Query query=refLists.child(currentUser.getUid()).orderByChild("List Data/listCreationDate");
            query.addListenerForSingleValueEvent(lists_array_listener);

            chip_date.setClickable(false);
            chip_name.setClickable(true);
        }
    }

    /**
     * בדיקת הרשאות המשתמש.
     */
    public void check_permissions() {
        if (!PermissionsActivity.checkAllPermissions(this) || !LocationHelper.isGPSOn(this))
        {
            Intent pa = new Intent(this, PermissionsActivity.class);
            startActivity(pa);
            finish();
        }
    }
}