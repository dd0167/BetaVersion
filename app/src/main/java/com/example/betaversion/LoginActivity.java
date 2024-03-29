package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.example.betaversion.FB_Ref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * מסך "כניסה".
 */
public class LoginActivity extends AppCompatActivity {

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

        SharedPreferences settings = getSharedPreferences("STAY_CONNECT",MODE_PRIVATE);
        boolean isChecked = settings.getBoolean("stayConnect",false);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (isChecked && (currentUser != null)){
            ProgressDialog progressDialog = ProgressDialog.show(this, "מתחבר לחשבונך", "טוען...", true);

            refUsers.child(currentUser.getUid()).child("User Data").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {

                    User getUser = dS.getValue(User.class);

                    if (getUser.getUserFirstName().isEmpty() || getUser.getUserLastName().isEmpty() || getUser.getUserAge().isEmpty() ||getUser.getUserHomeAddress().isEmpty() || getUser.getUserPhoneNumber().isEmpty())
                    {
                        progressDialog.dismiss();
                        move_settings();
                        Toast.makeText(LoginActivity.this, "הכנס את הפרטים הנדרשים", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        move_main();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "שגיאה", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * כפתור הכניסה.
     *
     * @param view the view
     */
    public void sign_in(View view) {
        String email=et_email_login.getText().toString();
        String password=et_password_login.getText().toString();
        if (email.isEmpty())
        {
            et_email_login.setError("הכנס אימייל!");
            et_email_login.requestFocus();
        }
        else if (password.isEmpty())
        {
            et_password_login.setError("הכנס סיסמה!");
            et_password_login.requestFocus();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        AlarmHelper.create_all_alarms(getApplicationContext());

                        move_main();

                        SharedPreferences settings = getSharedPreferences("STAY_CONNECT",MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("stayConnect",checkBox_login.isChecked());
                        editor.commit();

                        et_email_login.setText("");
                        et_password_login.setText("");
                    }else{
                        Toast.makeText(LoginActivity.this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /**
     * מעבר למסך ההרשמה.
     *
     * @param view the view
     */
    public void sign_up(View view) {
        Intent sup = new Intent(this, SignupActivity.class);
        startActivity(sup);
        finish();
    }

    /**
     * מעבר למסך הרשימות.
     */
    public void move_main()
    {
        Intent ma = new Intent(this, MainActivity.class);
        startActivity(ma);
        finish();
    }

    /**
     * מעבר למסך ההגדרות.
     */
    public void move_settings()
    {
        Intent sa = new Intent(this, SettingsActivity.class);
        startActivity(sa);
        finish();
    }
}