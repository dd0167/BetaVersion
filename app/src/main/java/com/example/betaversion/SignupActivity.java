package com.example.betaversion;

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

public class SignupActivity extends AppCompatActivity {

    EditText et_email_signup;
    EditText et_password_signup;
    ProgressBar progressBar_signup;
    CheckBox checkBox_signup;

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
            et_email_signup.setError("The email field can't be empty!");
            et_email_signup.requestFocus();
        }
        else if (password.isEmpty())
        {
            progressBar_signup.setVisibility(View.INVISIBLE);
            et_password_signup.setError("The password field can't be empty!");
            et_password_signup.requestFocus();
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignupActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                        move_main();

                        SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("stayConnect",checkBox_signup.isChecked());
                        editor.commit();
                    }else{
                        Toast.makeText(SignupActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar_signup.setVisibility(View.INVISIBLE);
                }
            });
            et_email_signup.setText("");
            et_password_signup.setText("");
        }
    }

    public void move_main()
    {
        Intent ma = new Intent(this, MainActivity.class);
        startActivity(ma);
        finish();
    }
}