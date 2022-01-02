package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {

    ProgressBar progressBar_login;
    EditText et_email_login;
    EditText et_password_login;
    CheckBox checkBox_login;
    Switch switch_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().hide();

        progressBar_login=(ProgressBar) findViewById(R.id.progressBar_login);
        et_email_login=(EditText) findViewById(R.id.et_email_login);
        et_password_login=(EditText) findViewById(R.id.et_password_login);
        checkBox_login=(CheckBox) findViewById(R.id.checkBox_login);
        switch_login=(Switch) findViewById(R.id.switch_login);

        switch_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                {
                    et_password_login.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else
                {
                    et_password_login.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        progressBar_login.setVisibility(View.INVISIBLE);

        SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
        boolean isChecked = settings.getBoolean("stayConnect",false);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (isChecked && (currentUser != null)){
            refUsers.child(currentUser.getUid()).child("User Data").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {

                    User getUser = dS.getValue(User.class);

                    if (getUser.getUserFirstName().isEmpty() || getUser.getUserLastName().isEmpty() || getUser.getUserAge().isEmpty() ||getUser.getUserHomeAddress().isEmpty() || getUser.getUserPhoneNumber().isEmpty())
                    {
                        move_settings();
                        Toast.makeText(LoginActivity.this, "Enter User Data", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, "User Data Error", Toast.LENGTH_SHORT).show();
                }
            });
            move_main();
        }
    }

    public void sign_in(View view) {
        progressBar_login.setVisibility(View.VISIBLE);
        String email=et_email_login.getText().toString();
        String password=et_password_login.getText().toString();
        if (email.isEmpty())
        {
            progressBar_login.setVisibility(View.INVISIBLE);
            et_email_login.setError("Email is required!");
            et_email_login.requestFocus();
        }
        else if (password.isEmpty())
        {
            progressBar_login.setVisibility(View.INVISIBLE);
            et_password_login.setError("Password is required!");
            et_password_login.requestFocus();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "User logged in successfully!", Toast.LENGTH_SHORT).show();
                        move_main();

                        SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("stayConnect",checkBox_login.isChecked());
                        editor.commit();

                        et_email_login.setText("");
                        et_password_login.setText("");
                    }else{
                        Toast.makeText(LoginActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar_login.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void sign_up(View view) {
        Intent sup = new Intent(this, SignupActivity.class);
        startActivity(sup);
        finish();
    }

    public void move_main()
    {
        Intent ma = new Intent(this, MainActivity.class);
        startActivity(ma);
        finish();
    }

    public void move_settings()
    {
        Intent sa = new Intent(this, SettingsActivity.class);
        startActivity(sa);
        finish();
    }
}