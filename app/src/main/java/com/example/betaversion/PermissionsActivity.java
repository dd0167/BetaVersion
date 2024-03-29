package com.example.betaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * מסך "הרשאות".
 */
public class PermissionsActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final int REQUEST_CODE_PERMISSION_STORAGE = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private static final int REQUEST_CODE_PERMISSION_BACKGROUND_LOCATION = 3;

    Switch storage_switch;
    Switch location_switch;
    Switch backgroundLocation_switch;

    LinearLayout linearLayout_storage, linearLayout_location, linearLayout_backgroundLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "הרשאות" + "</font>"));

        storage_switch=(Switch) findViewById(R.id.storage_switch);
        location_switch=(Switch) findViewById(R.id.location_switch);
        backgroundLocation_switch=(Switch) findViewById(R.id.backgroundLocation_switch);
        linearLayout_storage=(LinearLayout) findViewById(R.id.linearLayout_storage);
        linearLayout_location=(LinearLayout) findViewById(R.id.linearLayout_location);
        linearLayout_backgroundLocation=(LinearLayout) findViewById(R.id.linearLayout_backgroundLocation);

        isPermissions();
    }

    /**
     * הצגת ההרשאות שאושרו על ידי המשתמש.
     */
    public void isPermissions() {
        if (EasyPermissions.hasPermissions(this,Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            storage_switch.setChecked(true);
            linearLayout_storage.setClickable(false);
        }

        if (EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_FINE_LOCATION))
        {
            location_switch.setChecked(true);
            linearLayout_location.setClickable(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            if (EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                backgroundLocation_switch.setChecked(true);
                linearLayout_backgroundLocation.setClickable(false);
            }
        }
        else
        {
            if (EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                backgroundLocation_switch.setChecked(true);
                linearLayout_backgroundLocation.setClickable(false);
            }
        }

        if (!LocationHelper.isGPSOn(this))
        {
            Toast.makeText(this, "הפעל את מיקום המכשיר", Toast.LENGTH_SHORT).show();
            LocationHelper.turnGPSOn(this);
        }
    }

    /**
     * כפתור "המשך" העובר למסך הרשימות.
     *
     * @param view the view
     */
    public void continue_to_app(View view) {
        if (!LocationHelper.isGPSOn(this) )
        {
            Toast.makeText(this, "הפעל את מיקום המכשיר", Toast.LENGTH_SHORT).show();
            LocationHelper.turnGPSOn(this);
        }
        else if (!checkAllPermissions(this))
        {
            Toast.makeText(this, "אפשר גישה לכל ההרשאות", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent ma = new Intent(this, MainActivity.class);
            startActivity(ma);
            finish();
        }
    }

    /**
     * בדיקה האם כל ההרשאות אושרו על ידי המשתמש.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean checkAllPermissions(Context context)
    {
        boolean result=true;
        if (!EasyPermissions.hasPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            Toast.makeText(context, "אפשר גישה לאחסון המכשיר", Toast.LENGTH_SHORT).show();
            result=false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            if (!EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Toast.makeText(context, "אפשר גישה למיקום המכשיר", Toast.LENGTH_SHORT).show();
                result=false;
            }
            if (!EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            {
                Toast.makeText(context, "אפשר גישה למיקום המכשיר ברקע", Toast.LENGTH_SHORT).show();
                result=false;
            }
        }
        else
        {
            if (!EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Toast.makeText(context, "אפשר גישה למיקום המכשיר", Toast.LENGTH_SHORT).show();
                result=false;
            }
        }

        return result;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (!LocationHelper.isGPSOn(this))
        {
            LocationHelper.turnGPSOn(this);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms))
        {
            AppSettingsDialog.Builder builder = new AppSettingsDialog.Builder(this);
            builder.build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    storage_switch.setChecked(true);
                    linearLayout_storage.setClickable(false);
                } else {
                    //not granted
                    Toast.makeText(this, "אפשר גישה לאחסון המכשיר", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    location_switch.setChecked(true);
                    linearLayout_location.setClickable(false);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                    {
                        backgroundLocation_switch.setChecked(true);
                        linearLayout_backgroundLocation.setClickable(false);
                    }
                } else {
                    //not granted
                    Toast.makeText(this, "אפשר גישה למיקום המכשיר", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_PERMISSION_BACKGROUND_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    backgroundLocation_switch.setChecked(true);
                    linearLayout_backgroundLocation.setClickable(false);
                } else {
                    //not granted
                    Toast.makeText(this, "אפשר גישה למיקום המכשיר ברקע", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * בקשת הרשאות מהמשתמש.
     *
     * @param view the view
     */
    public void requestPermission(View view) {
        if (view.getId()==R.id.linearLayout_storage)
        {
            EasyPermissions.requestPermissions(
                    this,
                    "אפשר גישה לאחסון המכשיר",
                    REQUEST_CODE_PERMISSION_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            );
        }
        else if (view.getId()==R.id.linearLayout_location)
        {
            EasyPermissions.requestPermissions(
                    this,
                    "אפשר גישה למיקום המכשיר",
                    REQUEST_CODE_PERMISSION_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            if (view.getId()==R.id.linearLayout_backgroundLocation)
            {
                EasyPermissions.requestPermissions(
                        this,
                        "אפשר גישה למיקום המכשיר ברקע",
                        REQUEST_CODE_PERMISSION_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                );
            }
        }
    }
}