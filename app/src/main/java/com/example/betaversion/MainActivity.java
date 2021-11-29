package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void log_out(View view) {
        mAuth.signOut();
        SharedPreferences settings = getSharedPreferences("Stay_Connect",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("stayConnect",false);
        editor.commit();
        move_login();
    }

    public void move_login()
    {
        Intent la = new Intent(this, LoginActivity.class);
        startActivity(la);
        finish();
    }

    public void create_user(View view) {
    }

    public void create_list(View view) {
    }

    public void create_task(View view) {
    }

    public void create_tasks_day(View view) {
    }
}