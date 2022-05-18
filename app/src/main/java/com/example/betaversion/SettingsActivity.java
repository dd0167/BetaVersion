package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refLists;
import static com.example.betaversion.FB_Ref.refTasksDays;
import static com.example.betaversion.FB_Ref.refUsers;
import static com.example.betaversion.FB_Ref.referenceStorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    ImageView user_image_settings;
    EditText et_first_name_settings;
    EditText et_last_name_settings;
    EditText et_age_settings;
    EditText et_home_address_settings;
    EditText et_phone_number_settings;
    CheckBox checkBox_settings;
    FirebaseUser currentUser;
    Uri imageUri;
    boolean is_changed;
    int PICK_IMAGE=2;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);

        bottomNavigationView.getMenu().findItem(R.id.settings).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.settings);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.my_lists) {
                    Intent ma = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(ma);
                    finish();
                } else if (id == R.id.about) {
                    Intent ca = new Intent(SettingsActivity.this, CreditsActivity.class);
                    startActivity(ca);
                    finish();
                } else if (id == R.id.settings) {
                    Intent sa = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(sa);
                    finish();
                } else if (id == R.id.tasks_day) {
                    Intent td = new Intent(SettingsActivity.this, TasksDayListsActivity.class);
                    startActivity(td);
                    finish();
                }
                return true;
            }
        });

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "הגדרות" + "</font>"));

        user_image_settings = (ImageView) findViewById(R.id.user_image_settings);
        et_first_name_settings = (EditText) findViewById(R.id.et_first_name_settings);
        et_last_name_settings = (EditText) findViewById(R.id.et_last_name_settings);
        et_age_settings = (EditText) findViewById(R.id.et_age_settings);
        et_home_address_settings = (EditText) findViewById(R.id.et_home_address_settings);
        et_phone_number_settings = (EditText) findViewById(R.id.et_phone_number_settings);
        checkBox_settings = (CheckBox) findViewById(R.id.checkBox_settings);

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            refUsers.child(currentUser.getUid()).child("User Data").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {

                    User getUser = dS.getValue(User.class);

                    Glide.with(user_image_settings.getContext()).load(getUser.getUserPictureUid()).into(user_image_settings);
                    et_first_name_settings.setText(getUser.getUserFirstName());
                    et_last_name_settings.setText(getUser.getUserLastName());
                    et_age_settings.setText(getUser.getUserAge());
                    et_home_address_settings.setText(getUser.getUserHomeAddress());
                    et_phone_number_settings.setText(getUser.getUserPhoneNumber());
                    SharedPreferences settings = getSharedPreferences("STAY_CONNECT", MODE_PRIVATE);
                    boolean isChecked = settings.getBoolean("stayConnect", false);
                    checkBox_settings.setChecked(isChecked);
                    imageUri = Uri.parse(getUser.getUserPictureUid());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SettingsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                                            Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // Delete the file
                            String deleteFileName2 = "User Images/" + currentUser.getUid() + " image.png";
                            StorageReference desRef = referenceStorage.child(deleteFileName2);
                            desRef.delete().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(SettingsActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            refUsers.child(currentUser.getUid()).removeValue();
                            refTasksDays.child(currentUser.getUid()).removeValue();
                            refLists.child(currentUser.getUid()).removeValue();

                            Toast.makeText(SettingsActivity.this, "מחיקת החשבון בוצעה בהצלחה", Toast.LENGTH_SHORT).show();

                            move_login();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void move_login()
    {
        Intent la = new Intent(this, LoginActivity.class);
        startActivity(la);
        finish();
    }

    public void update(View view) {
        if (!is_Internet_Connected()) {
            AlertDialog.Builder adb;
            adb=new AlertDialog.Builder(this);
            adb.setTitle("אין חיבור אינטרנט");
            adb.setMessage("אין אפשרות לעדכן את הנתונים, אנא התחבר לאינטרנט");
            adb.setIcon(R.drawable.no_wifi);
            adb.setCancelable(false);
            adb.setPositiveButton("נסה שוב", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    update(view);
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
            String first_name=et_first_name_settings.getText().toString();
            String last_name=et_last_name_settings.getText().toString();
            String age=et_age_settings.getText().toString();
            String home_address=et_home_address_settings.getText().toString();
            String phone=et_phone_number_settings.getText().toString();

            if (first_name.isEmpty())
            {
                et_first_name_settings.setError("כתוב שם פרטי!");
                et_first_name_settings.requestFocus();
            }
            else if (last_name.isEmpty())
            {
                et_last_name_settings.setError("כתוב שם משפחה!");
                et_last_name_settings.requestFocus();
            }
            else if (age.isEmpty())
            {
                et_age_settings.setError("הכנס את גילך!");
                et_age_settings.requestFocus();
            }
            else if (Integer.parseInt(age)>120 || Integer.parseInt(age)<=0)
            {
                et_age_settings.setError("גיל שגוי!");
                et_age_settings.requestFocus();
            }
            else if (home_address.isEmpty())
            {
                et_home_address_settings.setError("כתוב את כתובת ביתך!");
                et_home_address_settings.requestFocus();
            }
            else if (correct_address(home_address).equals(""))
            {
                et_home_address_settings.setError("כתובת שגויה!");
                et_home_address_settings.requestFocus();
            }
            else if (phone.isEmpty())
            {
                et_phone_number_settings.setError("הכנס מספר טלפון!");
                et_phone_number_settings.requestFocus();
            }
            else if (phone.length()!=10 || !phone.startsWith("05"))
            {
                et_phone_number_settings.setError("מספר טלפון שגוי!");
                et_phone_number_settings.requestFocus();
            }
            else
            {
                progressDialog=ProgressDialog.show(this,"מעדכן נתונים","טוען...",true);

                SharedPreferences settings = getSharedPreferences("STAY_CONNECT",MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("stayConnect",checkBox_settings.isChecked());
                editor.commit();

                String file_name="User Images/"+currentUser.getUid()+" image.png";
                StorageReference fileRef=referenceStorage.child(file_name);
                if (is_changed)
                {
                    fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(SettingsActivity.this, "עדכון הנתונים בוצע בהצלחה", Toast.LENGTH_SHORT).show();
                                    User user=new User(currentUser.getUid(),first_name,last_name,age,correct_address(home_address),currentUser.getEmail(),phone,uri.toString());
                                    refUsers.child(currentUser.getUid()).child("User Data").setValue(user);
                                    is_changed=false;
                                    progressDialog.dismiss();
                                    move_main();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            is_changed=false;
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            is_changed=false;
                        }
                    });
                }
                else
                {
                    progressDialog=ProgressDialog.show(this,"שומר נתונים","טוען...",true);

                    User user=new User(currentUser.getUid(),first_name,last_name,age,correct_address(home_address),currentUser.getEmail(),phone,imageUri.toString());
                    refUsers.child(currentUser.getUid()).child("User Data").setValue(user);
                    Toast.makeText(SettingsActivity.this, "עדכון הנתונים בוצע בהצלחה", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    move_main();
                }
            }
        }
        et_first_name_settings.clearFocus();
        et_last_name_settings.clearFocus();
        et_age_settings.clearFocus();
        et_home_address_settings.clearFocus();
        et_phone_number_settings.clearFocus();
    }

    public String correct_address(String address)
    {
        Geocoder geocoder=new Geocoder(SettingsActivity.this);
        try {
            List<Address> addressList=geocoder.getFromLocationName(address,6);
            Address user_address=addressList.get(0);
            return user_address.getAddressLine(0);
        }
        catch (Exception e) {
            Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    public void change_image(View view) {
        String[] items={"בחר תמונה מהגלריה","בחר את תמונת ברירת המחדל"};

        AlertDialog.Builder adb;
        adb=new AlertDialog.Builder(this);
        adb.setTitle("שנה תמונת פרופיל");
        adb.setIcon(R.drawable.user_icon);
        adb.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0)
                {
                    Intent galleryIntent=new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent,PICK_IMAGE);
                    is_changed=true;
                }
                else if (which==1)
                {
                    user_image_settings.setImageResource(R.drawable.user_icon);
                    imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.user_icon);
                    is_changed=true;
                }
            }
        });
        adb.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog ad= adb.create();
        ad.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_IMAGE && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setBorderCornerLength(1)
                    .setBorderLineColor(Color.WHITE)
                    .setAutoZoomEnabled(true)
                    .setActivityTitle("חתוך תמונה")
                    .setFixAspectRatio(true)
                    .setCropMenuCropButtonTitle("סיום")
                    .start(this);
        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK)
            {
                imageUri=result.getUri();
                user_image_settings.setImageURI(imageUri);
            }
            else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error=result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
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

    public void move_main()
    {
        Intent ma = new Intent(this, MainActivity.class);
        startActivity(ma);
        finish();
    }
}