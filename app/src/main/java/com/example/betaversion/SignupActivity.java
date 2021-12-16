package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refUsers;
import static com.example.betaversion.FB_Ref.reference;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {

    EditText et_email_signup;
    EditText et_password_signup;
    ProgressBar progressBar_signup;
    CheckBox checkBox_signup;
    AlertDialog.Builder adb;
    Uri imageUri;
    ImageView user_image;
    Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        progressBar_signup=(ProgressBar) findViewById(R.id.progressBar_signup);
        et_email_signup=(EditText) findViewById(R.id.et_email_signup);
        et_password_signup=(EditText) findViewById(R.id.et_password_signup);
        checkBox_signup=(CheckBox) findViewById(R.id.checkBox_signup);

        progressBar_signup.setVisibility(View.INVISIBLE);
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

    public void sign_up(View view) {
        progressBar_signup.setVisibility(View.VISIBLE);
        String email=et_email_signup.getText().toString();
        String password=et_password_signup.getText().toString();
        if (email.isEmpty())
        {
            progressBar_signup.setVisibility(View.INVISIBLE);
            et_email_signup.setError("Email is required!");
            et_email_signup.requestFocus();
        }
        else if (password.isEmpty())
        {
            progressBar_signup.setVisibility(View.INVISIBLE);
            et_password_signup.setError("Password is required!");
            et_password_signup.requestFocus();
        }
        else
        {
            progressBar_signup.setVisibility(View.INVISIBLE);
            Typeface typeface=Typeface.create("casual",Typeface.NORMAL);
            adb=new AlertDialog.Builder(this);
            adb.setCancelable(false);

            adb.setTitle("Enter User Data");
            adb.setMessage("Enter all the required information");
            adb.setIcon(R.drawable.data_icon);

            LinearLayout myView = new LinearLayout(this);
            myView.setOrientation(LinearLayout.VERTICAL);
            EditText et_first_name=new EditText(this);
            et_first_name.setGravity(Gravity.CENTER);
            et_first_name.setHint("Enter First Name");
            et_first_name.setInputType(InputType.TYPE_CLASS_TEXT);
            EditText et_last_name=new EditText(this);
            et_last_name.setGravity(Gravity.CENTER);
            et_last_name.setHint("Enter Last Name");
            et_last_name.setInputType(InputType.TYPE_CLASS_TEXT);
            EditText et_age=new EditText(this);
            et_age.setGravity(Gravity.CENTER);
            et_age.setHint("Enter Age");
            et_age.setInputType(InputType.TYPE_CLASS_NUMBER);
            EditText et_home_address=new EditText(this);
            et_home_address.setGravity(Gravity.CENTER);
            et_home_address.setHint("Enter Home Address");
            et_home_address.setInputType(InputType.TYPE_CLASS_TEXT);
            EditText et_phone=new EditText(this);
            et_phone.setGravity(Gravity.CENTER);
            et_phone.setHint("Enter Phone number");
            et_phone.setInputType(InputType.TYPE_CLASS_PHONE);
            user_image=new ImageView(this);
            user_image.setImageResource(R.drawable.user_icon);
            imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.user_icon);
            model = new Model(imageUri.toString());
            Button select_image=new Button(this);
            select_image.setText("Select Image");
            select_image.setGravity(Gravity.CENTER);
            select_image.setTextColor(Color.BLACK);
            select_image.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onClick(View v) {
                    Intent galleryIntent=new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent,2);
                }
            });

            select_image.setTypeface(typeface);
            et_first_name.setTypeface(typeface);
            et_last_name.setTypeface(typeface);
            et_age.setTypeface(typeface);
            et_home_address.setTypeface(typeface);
            et_phone.setTypeface(typeface);

            myView.addView(et_first_name);
            myView.addView(et_last_name);
            myView.addView(et_age);
            myView.addView(et_home_address);
            myView.addView(et_phone);
            myView.addView(select_image);
            myView.addView(user_image);
            user_image.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, // width
                    500, // height
                    1f));
            adb.setView(myView);
            adb.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressBar_signup.setVisibility(View.VISIBLE);
                    String first_name=et_first_name.getText().toString();
                    String last_name=et_last_name.getText().toString();
                    String age=et_age.getText().toString();
                    String home_address=et_home_address.getText().toString();
                    String phone=et_phone.getText().toString();
                    if (!is_Internet_Connected()) {
                        Toast.makeText(SignupActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        progressBar_signup.setVisibility(View.INVISIBLE);
                    }
                    else if (first_name.isEmpty() || last_name.isEmpty() || age.isEmpty() || home_address.isEmpty() || phone.isEmpty())
                    {
                        Toast.makeText(SignupActivity.this, "Enter all the required information!", Toast.LENGTH_SHORT).show();
                        progressBar_signup.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putBoolean("stayConnect",checkBox_signup.isChecked());
                                    editor.commit();

                                    String file_name="User Images/"+et_first_name.getText().toString()+" "+et_last_name.getText().toString()+" image.png";
                                    StorageReference fileRef=reference.child(file_name);
                                    fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    model = new Model(uri.toString());
                                                    Toast.makeText(SignupActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                                    User user=new User(currentUser.getUid(),first_name,last_name,age,home_address,currentUser.getEmail(),phone,model.getImageUrl());
                                                    refUsers.child(first_name+" "+last_name).child("User Data").setValue(user);

                                                    move_main();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignupActivity.this, "Uploading Image Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            progressBar_signup.setVisibility(View.VISIBLE);
                                        }
                                    });

                                }else{
                                    Toast.makeText(SignupActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                                progressBar_signup.setVisibility(View.INVISIBLE);
                            }
                        });
                        et_email_signup.setText("");
                        et_password_signup.setText("");
                    }
                }
            });
            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad=adb.create();
            ad.show();
            progressBar_signup.setVisibility(View.INVISIBLE);
        }
    }

    public void move_main()
    {
        Intent ma = new Intent(this, MainActivity.class);
        startActivity(ma);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==2 && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            user_image.setImageURI(imageUri);
        }
    }

    public void sign_in(View view) {
        Intent sin = new Intent(this, LoginActivity.class);
        startActivity(sin);
        finish();
    }
}