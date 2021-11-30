package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class SignupActivity extends AppCompatActivity {

    EditText et_email_signup;
    EditText et_password_signup;
    ProgressBar progressBar_signup;
    CheckBox checkBox_signup;
    AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressBar_signup=(ProgressBar) findViewById(R.id.progressBar_signup);
        et_email_signup=(EditText) findViewById(R.id.et_email_signup);
        et_password_signup=(EditText) findViewById(R.id.et_password_signup);
        checkBox_signup=(CheckBox) findViewById(R.id.checkBox_signup);

        progressBar_signup.setVisibility(View.INVISIBLE);
    }

    public void sign_in(View view) {
        Intent la = new Intent(this, LoginActivity.class);
        startActivity(la);
        finish();
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
            adb=new AlertDialog.Builder(this);
            adb.setCancelable(false);
            adb.setTitle("Enter User Data");
            adb.setIcon(R.drawable.data_icon);
            EditText et_first_name=new EditText(this);
            et_first_name.setGravity(Gravity.CENTER);
            et_first_name.setHint("Enter First Name");
            adb.setView(et_first_name);
            adb.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String first_name=et_first_name.getText().toString();
                    if (first_name.isEmpty())
                    {
                        progressBar_signup.setVisibility(View.INVISIBLE);
                        et_first_name.setError("First name is required!");
                        et_first_name.requestFocus();
                        ///////////////////////////////////////
                    }
                    else
                    {
//                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()){
//                                    Toast.makeText(SignupActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
//                                    move_main();
//
//                                    SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = settings.edit();
//                                    editor.putBoolean("stayConnect",checkBox_signup.isChecked());
//                                    editor.commit();
//                                }else{
//                                    Toast.makeText(SignupActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                                progressBar_signup.setVisibility(View.INVISIBLE);
//                            }
//                        });
//                        et_email_signup.setText("");
//                        et_password_signup.setText("");
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
        }
    }

    public void move_main()
    {
        Intent ma = new Intent(this, MainActivity.class);
        startActivity(ma);
        finish();
    }
}