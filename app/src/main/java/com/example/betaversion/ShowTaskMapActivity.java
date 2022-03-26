package com.example.betaversion;

import static com.example.betaversion.FB_Ref.mAuth;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.List;

public class ShowTaskMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    int REQUEST_LOCATION = 88;

    BottomNavigationView bottomNavigationView;

    Intent gi;
    Task task_clicked;

    TextView tv_task_name, tv_task_address_map;
    TextView tv_task_current_address, tv_distance;

    MapView mapView_Task;
    GoogleMap gmap;
    Bundle mapViewBundle;

    String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    FusedLocationProviderClient fusedLocationProviderClient;
    CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task_map);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);

        bottomNavigationView.getMenu().findItem(R.id.empty).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.empty);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.my_lists) {
                    Intent ma = new Intent(ShowTaskMapActivity.this, MainActivity.class);
                    startActivity(ma);
                    finish();
                } else if (id == R.id.about) {
                    Intent ca = new Intent(ShowTaskMapActivity.this, CreditsActivity.class);
                    startActivity(ca);
                    finish();
                } else if (id == R.id.settings) {
                    Intent sa = new Intent(ShowTaskMapActivity.this, SettingsActivity.class);
                    startActivity(sa);
                    finish();
                }
                else if (id==R.id.tasks_day)
                {
                    Intent td=new Intent(ShowTaskMapActivity.this,TasksDayListsActivity.class);
                    startActivity(td);
                    finish();
                }
                return true;
            }
        });

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Disable Screen Rotation

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "הצגת המטלה על המפה" + "</font>"));

        gi = getIntent();

        task_clicked = gi.getParcelableExtra("task_clicked");

        tv_task_name = (TextView) findViewById(R.id.tv_task_name);
        tv_task_address_map = (TextView) findViewById(R.id.tv_task_address_map);
        tv_task_name.setText(task_clicked.getTaskName());
        tv_task_address_map.setText("כתובת המטלה: " + task_clicked.getTaskAddress());

        //location permission
        getLocation();
        checkPermission();

        tv_task_current_address = (TextView) findViewById(R.id.tv_task_current_address);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        mapView_Task = (MapView) findViewById(R.id.mapView_task);

        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView_Task.onCreate(mapViewBundle);
        mapView_Task.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    //show City and Country
    public void show_locationData(LatLng latLng) {
        Geocoder geocoder = new Geocoder(ShowTaskMapActivity.this);

        try {
            List<Address> address=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            Address user_address = address.get(0);
            tv_task_current_address.setText("מיקומך הנוכחי: " + user_address.getAddressLine(0));
        } catch (IOException e) {
            Toast.makeText(ShowTaskMapActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void get_current_location(View view) {
        progressDialog=ProgressDialog.show(this,"מוצא את המיקום הנוכחי","טוען...",true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        com.google.android.gms.tasks.Task<Location> currentLocationTask = fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.getToken()
        );

        currentLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try {
                    gmap.clear();

                    Geocoder geocoder=new Geocoder(ShowTaskMapActivity.this);

                    List<Address> task_address=geocoder.getFromLocationName(task_clicked.getTaskAddress(),6);
                    LatLng latLng_task=new LatLng(task_address.get(0).getLatitude(),task_address.get(0).getLongitude());

                    double latitude = location.getLatitude();
                    double longitude=location.getLongitude();

                    LatLng latLng_current_location = new LatLng(latitude, longitude);

                    show_locationData(latLng_current_location);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng_current_location);
                    markerOptions.title("המיקום שלי");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon));
                    gmap.addMarker(markerOptions);

                    float zoomLevel = 17.0f; //This goes up to 21
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng_current_location, zoomLevel));

                    // add task list marker
                    List<Address> addressList=geocoder.getFromLocationName(task_clicked.getTaskAddress(),6);
                    Address user_address=addressList.get(0);
                    LatLng latLng = new LatLng(user_address.getLatitude(), user_address.getLongitude());
                    MarkerOptions markerOptions_task = new MarkerOptions();
                    markerOptions_task.title(task_clicked.getTaskName());
                    markerOptions_task.position(latLng);
                    gmap.addMarker(markerOptions_task);

                    double d = distance(latitude, latLng_task.latitude , longitude, latLng_task.longitude);
                    String distance = String.valueOf(d);
                    tv_distance.setText("המרחק בין המטלה לבין מיקומך הנוכחי: " + distance.substring(0, 5) +" ק\"מ");
                    progressDialog.dismiss();
                }

                catch (IOException e) {
                    Toast.makeText(ShowTaskMapActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(ShowTaskMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permission is granted
            requestLocation();
        } else {
            //when permission is denied
            ActivityCompat.requestPermissions(ShowTaskMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 23);
        }
    }

    public void requestLocation() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);
        request.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        builder.setAlwaysShow(true);

        com.google.android.gms.tasks.Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                // do here your task with your location
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(ShowTaskMapActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                        }
                        break;

                    //when device doesn't have location feature
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public void getLocation() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(ShowTaskMapActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                //Toast.makeText(ShowTaskMapActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if (title.equals("Log Out")) {
            AlertDialog.Builder adb;
            adb = new AlertDialog.Builder(this);
            adb.setTitle("התנתקות");
            adb.setMessage("אתה בטוח שברצונך להתנתק מהאפליקציה?");
            adb.setIcon(R.drawable.log_out_icon);
            adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.signOut();
                    SharedPreferences settings = getSharedPreferences("Stay_Connect", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("stayConnect", false);
                    editor.commit();
                    move_login();
                }
            });
            adb.setNeutralButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        return true;
    }

    public void move_login() {
        Intent la = new Intent(this, LoginActivity.class);
        startActivity(la);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.clear();
        gmap.setMaxZoomPreference(21);
        gmap.setMinZoomPreference(0);

        gmap.setIndoorEnabled(true);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        gmap.setMyLocationEnabled(true);
//        gmap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//            @Override
//            public boolean onMyLocationButtonClick() {
//                try {
//                    LatLng latLng = new LatLng(gmap.getMyLocation().getLatitude(), gmap.getMyLocation().getLongitude());
//                    float zoomLevel = 17.0f; //This goes up to 21
//                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
//                } catch (Exception e) {
//                    Toast.makeText(ShowTaskMapActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//                return true;
//          }
//        });
        LatLng isr = new LatLng(31.38269, 35.071805);
        gmap.animateCamera(CameraUpdateFactory.newLatLng(isr));

        float zoomLevel = 7.0f; //This goes up to 21
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(isr, zoomLevel));



        //show task address on map
        Geocoder geocoder=new Geocoder(ShowTaskMapActivity.this);
        try {
            List<Address> addressList=geocoder.getFromLocationName(task_clicked.getTaskAddress(),6);
            Address user_address=addressList.get(0);

            LatLng latLng = new LatLng(user_address.getLatitude(), user_address.getLongitude());

            //gmap.clear();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(task_clicked.getTaskName());
            markerOptions.position(latLng);
            gmap.addMarker(markerOptions);

            zoomLevel = 17.0f; //This goes up to 21
            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
        catch (Exception e)
        {
            Toast.makeText(ShowTaskMapActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static double distance(double lat1, double lat2, double lon1, double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles 6371;
        double r = 3956;

        // calculate the result
        return(c * r);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView_Task.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView_Task.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView_Task.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView_Task.onStop();
    }
    @Override
    protected void onPause() {
        mapView_Task.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView_Task.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView_Task.onLowMemory();
    }
}