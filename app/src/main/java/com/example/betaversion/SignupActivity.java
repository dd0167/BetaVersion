package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refUsers;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
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
    Uri imageUri;
    ImageView user_image;
    Switch switch_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().hide();

        progressBar_signup=(ProgressBar) findViewById(R.id.progressBar_signup);
        et_email_signup=(EditText) findViewById(R.id.et_email_signup);
        et_password_signup=(EditText) findViewById(R.id.et_password_signup);
        checkBox_signup=(CheckBox) findViewById(R.id.checkBox_signup);
        switch_signup=(Switch) findViewById(R.id.switch_signup);

        switch_signup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                {
                    et_password_signup.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else
                {
                    et_password_signup.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

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
            et_email_signup.setError("הכנס אימייל!");
            et_email_signup.requestFocus();
        }
        else if (password.isEmpty())
        {
            progressBar_signup.setVisibility(View.INVISIBLE);
            et_password_signup.setError("הכנס סיסמה!");
            et_password_signup.requestFocus();
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

                        imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.user_icon);
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        User user=new User(currentUser.getUid(),"","","","",currentUser.getEmail(),"",imageUri.toString());
                        refUsers.child(currentUser.getUid()).child("User Data").setValue(user);
                        //Toast.makeText(SignupActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();

                        move_settings();
                        Toast.makeText(SignupActivity.this, "הכנס את הפרטים הנדרשים", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(SignupActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar_signup.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void move_settings()
    {
        Intent sa = new Intent(this, SettingsActivity.class);
        startActivity(sa);
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