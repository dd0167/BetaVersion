package com.example.betaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
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

public class PermissionsActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final int REQUEST_CODE_PERMISSION = 0;
    Switch storage_switch;
    Switch location_switch;
    Switch backgroundLocation_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "הרשאות" + "</font>"));

        storage_switch=(Switch) findViewById(R.id.storage_switch);
        location_switch=(Switch) findViewById(R.id.location_switch);
        backgroundLocation_switch=(Switch) findViewById(R.id.backgroundLocation_switch);

        isPermissions();
    }

    public void isPermissions() {
        if (EasyPermissions.hasPermissions(this,Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            storage_switch.setChecked(true);
        }
        else
        {
            EasyPermissions.requestPermissions(
                    this,
                    "אפשר גישה לאחסון המכשיר",
                    REQUEST_CODE_PERMISSION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            );
        }

        if (EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_FINE_LOCATION))
        {
            location_switch.setChecked(true);
        }
        else EasyPermissions.requestPermissions(
                this,
                "אפשר גישה למיקום המכשיר",
                REQUEST_CODE_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
        );

        if (EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        {
            backgroundLocation_switch.setChecked(true);

        }
        else EasyPermissions.requestPermissions(
                this,
                "אפשר גישה למיקום המכשיר ברקע",
                REQUEST_CODE_PERMISSION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        );

        if (!isGPSOn())
        {
            Toast.makeText(this, "הפעל את מיקום המכשיר", Toast.LENGTH_SHORT).show();
            turnGPSOn();
        }
    }

    public void continue_to_app(View view) {
        if (!isGPSOn() || !checkAllPermissions(this))
        {
            Toast.makeText(this, "הפעל את מיקום המכשיר", Toast.LENGTH_SHORT).show();
            turnGPSOn();
        }
        else
        {
            Intent ma = new Intent(this, MainActivity.class);
            startActivity(ma);
            finish();
        }
    }

    public static boolean checkAllPermissions(Context context)
    {
        if (!EasyPermissions.hasPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            Toast.makeText(context, "אפשר גישה לאחסון המכשיר", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            Toast.makeText(context, "אפשר גישה למיקום המכשיר", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        {
            Toast.makeText(context, "אפשר גישה למיקום המכשיר ברקע", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isGPSOn() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public void turnGPSOn() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        com.google.android.gms.tasks.Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {


            @Override
            public void onComplete(com.google.android.gms.tasks.Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        PermissionsActivity.this,
                                        101);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (!isGPSOn())
        {
            turnGPSOn();
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
}