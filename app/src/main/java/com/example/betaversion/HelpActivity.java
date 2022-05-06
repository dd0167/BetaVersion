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

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class HelpActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FirebaseUser currentUser;

    BottomSheetDialog bottomSheetDialog_help;
    ImageView cancel_bottom_sheet_dialog_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        bottomSheetDialog_help=(BottomSheetDialog) new BottomSheetDialog(HelpActivity.this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false).setIcon(R.drawable.ic_notification);
        bottomNavigationView.setSelectedItemId(R.id.empty);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.my_lists) {
                    Intent ma = new Intent(HelpActivity.this, MainActivity.class);
                    startActivity(ma);
                    finish();
                } else if (id == R.id.about) {
                    Intent ca = new Intent(HelpActivity.this, CreditsActivity.class);
                    startActivity(ca);
                    finish();
                } else if (id == R.id.settings) {
                    Intent sa = new Intent(HelpActivity.this, SettingsActivity.class);
                    startActivity(sa);
                    finish();
                } else if (id == R.id.tasks_day) {
                    Intent td = new Intent(HelpActivity.this, TasksDayListsActivity.class);
                    startActivity(td);
                    finish();
                }
                return true;
            }
        });

        currentUser = mAuth.getCurrentUser();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "עזרה" + "</font>"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
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
                                            Toast.makeText(HelpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // Delete the file
                            String deleteFileName2 = "User Images/" + currentUser.getUid() + " image.png";
                            StorageReference desRef = referenceStorage.child(deleteFileName2);
                            desRef.delete().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(HelpActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            refUsers.child(currentUser.getUid()).removeValue();
                            refTasksDays.child(currentUser.getUid()).removeValue();
                            refLists.child(currentUser.getUid()).removeValue();

                            Toast.makeText(HelpActivity.this, "מחיקת החשבון בוצעה בהצלחה", Toast.LENGTH_SHORT).show();

                            move_login();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HelpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void move_login() {
        Intent la = new Intent(this, LoginActivity.class);
        startActivity(la);
        finish();
    }

    public void show_bottomSheetDialog()
    {
        bottomSheetDialog_help=new BottomSheetDialog(this,R.style.BottomSheetTheme);

        bottomSheetDialog_help.setContentView(R.layout.bottom_sheet_layout_list);
        bottomSheetDialog_help.show();

        cancel_bottom_sheet_dialog_help=(ImageView) bottomSheetDialog_help.findViewById(R.id.cancel_bottom_sheet_dialog_help);
        cancel_bottom_sheet_dialog_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog_help.cancel();
            }
        });
    }

    public void show_explanation(View view)
    {
        show_bottomSheetDialog();
        ImageView iv_help_layout=bottomSheetDialog_help.findViewById(R.id.iv_help_layout);
        TextView tv_help=bottomSheetDialog_help.findViewById(R.id.tv_help);

        if (view.getId()==R.id.cv_myLists)
        {
            iv_help_layout.setImageResource(R.drawable.my_lists_icon);
        }
        else if (view.getId()==R.id.cv_myTasksDays)
        {
            iv_help_layout.setImageResource(R.drawable.tasks_day);
        }
        else if (view.getId()==R.id.cv_about)
        {
            iv_help_layout.setImageResource(R.drawable.about_icon);
        }
        else if (view.getId()==R.id.cv_addUser)
        {
            iv_help_layout.setImageResource(R.drawable.add_user);
        }
        else if (view.getId()==R.id.cv_settings)
        {
            iv_help_layout.setImageResource(R.drawable.settings_icon);
        }
        else if (view.getId()==R.id.cv_help)
        {
            iv_help_layout.setImageResource(R.drawable.help_icon);
        }
        else if (view.getId()==R.id.cv_addImage)
        {
            iv_help_layout.setImageResource(R.drawable.add_image_icon);
        }
        else if (view.getId()==R.id.cv_selectColor)
        {
            iv_help_layout.setImageResource(R.drawable.colors_icon);
        }
        else if (view.getId()==R.id.cv_add)
        {
            iv_help_layout.setImageResource(R.drawable.ic_add);
        }
        else if (view.getId()==R.id.cv_playBackground)
        {
            iv_help_layout.setImageResource(R.drawable.play_icon);
        }
        else if (view.getId()==R.id.cv_findLocation)
        {
            iv_help_layout.setImageResource(R.drawable.show_address_in_map);
        }
        else if (view.getId()==R.id.cv_userImage)
        {
            iv_help_layout.setImageResource(R.drawable.user_icon);
        }
    }
}