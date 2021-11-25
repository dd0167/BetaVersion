package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    ProgressBar progressBar_login;
    EditText et_email_login;
    EditText et_password_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar_login=(ProgressBar) findViewById(R.id.progressBar_login);
        et_email_login=(EditText) findViewById(R.id.et_email_login);
        et_password_login=(EditText) findViewById(R.id.et_password_login);

        progressBar_login.setVisibility(View.INVISIBLE);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            move_main();
        }
    }

    public void sign_in(View view) {
        progressBar_login.setVisibility(View.VISIBLE);
        String email=et_email_login.getText().toString();
        String password=et_password_login.getText().toString();
        if (email.isEmpty() || password.isEmpty())
        {
            progressBar_login.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Enter all the required information!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "User logged in successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
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