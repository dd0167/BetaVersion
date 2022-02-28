package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refUsers;
import static com.example.betaversion.FB_Ref.referenceStorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
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
    ProgressBar progressBar_settings;
    boolean is_changed;
    int PICK_IMAGE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);

        bottomNavigationView.getMenu().findItem(R.id.settings).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.settings);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id==R.id.my_lists)
                {
                    Intent ma = new Intent(SettingsActivity.this,MainActivity.class);
                    startActivity(ma);
                    finish();
                }
                else if (id==R.id.about)
                {
                    Intent ca = new Intent(SettingsActivity.this, CreditsActivity.class);
                    startActivity(ca);
                    finish();
                }
                else if (id==R.id.settings)
                {
                    Intent sa = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(sa);
                    finish();
                }
                else if (id==R.id.tasks_day)
                {
                    Intent td=new Intent(SettingsActivity.this,TasksDayListsActivity.class);
                    startActivity(td);
                    finish();
                }
                return true;
            }
        });

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

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
            else if (correct_address(home_address).equals(""))
            {
                progressBar_settings.setVisibility(View.INVISIBLE);
                et_home_address_settings.setError("Error address!");
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
                                    Toast.makeText(SettingsActivity.this, "User data changed successfully!", Toast.LENGTH_SHORT).show();
                                    User user=new User(currentUser.getUid(),first_name,last_name,age,correct_address(home_address),currentUser.getEmail(),phone,uri.toString());
                                    refUsers.child(currentUser.getUid()).child("User Data").setValue(user);
                                    progressBar_settings.setVisibility(View.INVISIBLE);
                                    is_changed=false;
                                    move_main();
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
                    User user=new User(currentUser.getUid(),first_name,last_name,age,correct_address(home_address),currentUser.getEmail(),phone,imageUri.toString());
                    refUsers.child(currentUser.getUid()).child("User Data").setValue(user);
                    Toast.makeText(SettingsActivity.this, "User data changed successfully!", Toast.LENGTH_SHORT).show();
                    move_main();
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

    public String correct_address(String address)
    {
        Geocoder geocoder=new Geocoder(SettingsActivity.this);
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

//        if (requestCode==PICK_IMAGE && resultCode==RESULT_OK && data!=null)
//        {
//            imageUri=data.getData();
//            user_image_settings.setImageURI(imageUri);
//        }

        if (requestCode==PICK_IMAGE && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
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