package com.example.betaversion;

import static com.example.betaversion.FB_Ref.currentUser;
import static com.example.betaversion.FB_Ref.mAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar_login=(ProgressBar) findViewById(R.id.progressBar_login);
        et_email_login=(EditText) findViewById(R.id.et_email_login);
        et_password_login=(EditText) findViewById(R.id.et_password_login);
        checkBox_login=(CheckBox) findViewById(R.id.checkBox_login);

        progressBar_login.setVisibility(View.INVISIBLE);

        SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
        boolean isChecked = settings.getBoolean("stayConnect",false);
        currentUser = mAuth.getCurrentUser();
        if (isChecked && (currentUser != null)){
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
            et_email_login.setError("The email field can't be empty!");
            et_email_login.requestFocus();
        }
        else if (password.isEmpty())
        {
            progressBar_login.setVisibility(View.INVISIBLE);
            et_password_login.setError("The password field can't be empty!");
            et_password_login.requestFocus();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "User logged in successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("stayConnect",checkBox_login.isChecked());
                        editor.commit();
                    }else{
                        Toast.makeText(LoginActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar_login.setVisibility(View.INVISIBLE);
                }
            });
            et_email_login.setText("");
            et_password_login.setText("");
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
}