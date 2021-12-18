package com.example.betaversion;

import static com.example.betaversion.FB_Ref.FBCS;
import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refUsers;
import static com.example.betaversion.FB_Ref.reference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SettingsActivity extends AppCompatActivity {

    ImageView user_image_settings;
    EditText et_first_name_settings;
    EditText et_last_name_settings;
    EditText et_age_settings;
    EditText et_home_address_settings;
    EditText et_phone_number_settings;
    CheckBox checkBox_settings;
    FirebaseUser currentUser;
    Uri imageUri;
    ProgressBar progressBar_settings;
    boolean is_changed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Settings" + "</font>"));

        user_image_settings=(ImageView) findViewById(R.id.user_image_settings);
        et_first_name_settings=(EditText) findViewById(R.id.et_first_name_settings);
        et_last_name_settings=(EditText) findViewById(R.id.et_last_name_settings);
        et_age_settings=(EditText) findViewById(R.id.et_age_settings);
        et_home_address_settings=(EditText) findViewById(R.id.et_home_address_settings);
        et_phone_number_settings=(EditText) findViewById(R.id.et_phone_number_settings);
        checkBox_settings=(CheckBox) findViewById(R.id.checkBox_settings);
        progressBar_settings=(ProgressBar) findViewById(R.id.progressBar_settings);

        currentUser = mAuth.getCurrentUser();

        if (currentUser!=null)
        {
            progressBar_settings.setVisibility(View.VISIBLE);
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
                    SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
                    boolean isChecked = settings.getBoolean("stayConnect",false);
                    checkBox_settings.setChecked(isChecked);
                    imageUri=Uri.parse(getUser.getUserPictureUid());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SettingsActivity.this, "User Data Error", Toast.LENGTH_SHORT).show();
                }
            });
            progressBar_settings.setVisibility(View.INVISIBLE);
        }
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
            if (et_first_name_settings.getText().toString().isEmpty() || et_last_name_settings.getText().toString().isEmpty() || et_age_settings.getText().toString().isEmpty() || et_home_address_settings.getText().toString().isEmpty() || et_phone_number_settings.getText().toString().isEmpty())
            {
                Toast.makeText(SettingsActivity.this, "Enter User Data", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent in=new Intent(this,MainActivity.class);
                startActivity(in);
                finish();
            }
        }
        else if (title.equals("About"))
        {
            if (et_first_name_settings.getText().toString().isEmpty() || et_last_name_settings.getText().toString().isEmpty() || et_age_settings.getText().toString().isEmpty() || et_home_address_settings.getText().toString().isEmpty() || et_phone_number_settings.getText().toString().isEmpty())
            {
                Toast.makeText(SettingsActivity.this, "Enter User Data", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent in = new Intent(this, CreditsActivity.class);
                startActivity(in);
                finish();
            }
        }
        else if (title.equals("Settings"))
        {
            if (et_first_name_settings.getText().toString().isEmpty() || et_last_name_settings.getText().toString().isEmpty() || et_age_settings.getText().toString().isEmpty() || et_home_address_settings.getText().toString().isEmpty() || et_phone_number_settings.getText().toString().isEmpty())
            {
                Toast.makeText(SettingsActivity.this, "Enter User Data", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent in=new Intent(this,SettingsActivity.class);
                startActivity(in);
                finish();
            }
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
            Toast.makeText(SettingsActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
                progressBar_settings.setVisibility(View.INVISIBLE);
                et_first_name_settings.setError("First name is required!");
                et_first_name_settings.requestFocus();
            }
            else if (last_name.isEmpty())
            {
                progressBar_settings.setVisibility(View.INVISIBLE);
                et_last_name_settings.setError("Last name is required!");
                et_last_name_settings.requestFocus();
            }
            else if (age.isEmpty())
            {
                progressBar_settings.setVisibility(View.INVISIBLE);
                et_age_settings.setError("Age is required!");
                et_age_settings.requestFocus();
            }
            else if (Integer.parseInt(age)>120 || Integer.parseInt(age)<=0)
            {
                progressBar_settings.setVisibility(View.INVISIBLE);
                et_age_settings.setError("Error age!");
                et_age_settings.requestFocus();
            }
            else if (home_address.isEmpty())
            {
                progressBar_settings.setVisibility(View.INVISIBLE);
                et_home_address_settings.setError("Home address is required!");
                et_home_address_settings.requestFocus();
            }
            else if (phone.isEmpty())
            {
                progressBar_settings.setVisibility(View.INVISIBLE);
                et_phone_number_settings.setError("Phone number is required!");
                et_phone_number_settings.requestFocus();
            }
            else if (phone.length()!=10 || !phone.startsWith("05"))
            {
                progressBar_settings.setVisibility(View.INVISIBLE);
                et_phone_number_settings.setError("Error Phone number!");
                et_phone_number_settings.requestFocus();
            }
            else
            {
                progressBar_settings.setVisibility(View.VISIBLE);
                SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("stayConnect",checkBox_settings.isChecked());
                editor.commit();

                String file_name="User Images/"+et_first_name_settings.getText().toString()+" "+et_last_name_settings.getText().toString()+" image.png";
                StorageReference fileRef=reference.child(file_name);
                if (is_changed)
                {
                    fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(SettingsActivity.this, "User data changed successfully!", Toast.LENGTH_SHORT).show();
                                    User user=new User(currentUser.getUid(),first_name,last_name,age,home_address,currentUser.getEmail(),phone,uri.toString());
                                    refUsers.child(currentUser.getUid()).child("User Data").setValue(user);
                                    progressBar_settings.setVisibility(View.INVISIBLE);
                                    is_changed=false;
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar_settings.setVisibility(View.INVISIBLE);
                            is_changed=false;
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            progressBar_settings.setVisibility(View.VISIBLE);
                            is_changed=false;
                        }
                    });
                }
                else
                {
                    progressBar_settings.setVisibility(View.VISIBLE);
                    User user=new User(currentUser.getUid(),first_name,last_name,age,home_address,currentUser.getEmail(),phone,imageUri.toString());
                    refUsers.child(currentUser.getUid()).child("User Data").setValue(user);
                    Toast.makeText(SettingsActivity.this, "User data changed successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        progressBar_settings.setVisibility(View.INVISIBLE);
        et_first_name_settings.clearFocus();
        et_last_name_settings.clearFocus();
        et_age_settings.clearFocus();
        et_home_address_settings.clearFocus();
        et_phone_number_settings.clearFocus();
    }

    public void change_image(View view) {
        String[] items={"Select Image From Gallery","Select The Default Image"};
        Typeface typeface=Typeface.create("casual",Typeface.NORMAL);
        AlertDialog.Builder adb;
        adb=new AlertDialog.Builder(this);
        adb.setTitle("Change Profile Image");
        //adb.setMessage("Select Image");
        adb.setIcon(R.drawable.user_icon);
        adb.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0)
                {
                    Intent galleryIntent=new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent,2);
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
        adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
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

        if (requestCode==2 && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            user_image_settings.setImageURI(imageUri);
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
}