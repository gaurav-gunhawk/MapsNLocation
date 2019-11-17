package com.example.imported_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int UPDATE_INTERVAL = 5000;
    FusedLocationProviderClient locationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private Location currentLocation;

    private int LOCATION_PERMISSION = 100;

    Button buttonGetLocation , buttonStopLocation;
    TextView textViewLatitude , textViewLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonGetLocation = (Button) findViewById(R.id.get_location_button);
        buttonStopLocation = (Button) findViewById(R.id.stop_location_button);

        textViewLatitude = (TextView)findViewById(R.id.latitide_textView);
        textViewLongitude = (TextView) findViewById(R.id.longitude_textView);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);

        locationCallback = new LocationCallback(){

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if(locationAvailability.isLocationAvailable()){
                    Log.i(TAG,"Location is available");
                }
                else{
                    Log.i(TAG,"Location is unAvailable");
                }
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG,"Location result is available");
            }
        };

        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGettingLocation();
            }
        });

        buttonStopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopGettingLocation();
            }
        });

    }

    private void startGettingLocation()
    {
        if(ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)
        {
            locationProviderClient.requestLocationUpdates
                    (locationRequest,locationCallback,MainActivity.this.getMainLooper());

            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                    textViewLatitude.setText(""+currentLocation.getLatitude());
                    textViewLongitude.setText(""+currentLocation.getLongitude());
                }
            });

            locationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG,"Exceptional while getting the location : " + e.getMessage());
                }
            });
        }
        else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(MainActivity.this,"Permission Needed",
                        Toast.LENGTH_LONG).show();}
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION ,
                        Manifest.permission.ACCESS_FINE_LOCATION },
                        LOCATION_PERMISSION);}
            }
    }

    private void stopGettingLocation()
    {
        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGettingLocation();
    }
}
