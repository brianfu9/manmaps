package com.example.manmaps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String DESTINATION_LATITUDE = "com.example.myfirstapp.MESSAGE1";
    public static final String DESTINATION_LONGITUDE = "com.example.myfirstapp.MESSAGE2";
    private static final String TAG = MainActivity.class.getName();
    Geocoder geocoder;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location myLocation;
    LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geocoder = new Geocoder(this, Locale.getDefault());


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    myLocation = location;
                }
                System.out.println("********************    myLocation " + myLocation.getLongitude() + ", " + myLocation.getLatitude());
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 42);
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);

    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    // *****************

    public void start() {
        startLocationUpdates();
    }

    public void stop() {
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 42);
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        Intent intent = new Intent(this, CompassActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();

        List<Address> addresses = null;
        String errorMessage = "";

        try {
            addresses = geocoder.getFromLocationName(message, 3);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "service_not_available";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "invalid_lat_long_used";
            Log.e(TAG, errorMessage + ". " +
                    "Querry string = " + message, illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no_address_found";
                Log.e(TAG, errorMessage);
            }
            alertDialog.setMessage("Location not found")
                    .setCancelable(false)
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            alertDialog.show();
            System.out.println("\n\nBORKEN " + errorMessage + "\n\n");
        } else {
            Address address = null;
            double best = Double.MAX_VALUE;
            for (Address a : addresses) {
                Location dummy = new Location("dummy");
                dummy.setLongitude(a.getLongitude());
                dummy.setLatitude(a.getLatitude());
                double compare = myLocation.distanceTo(dummy);
                if (compare < best) {
                    best = compare;
                    address = a;
                }
            }

            Log.i(TAG, "address_found");

            intent.putExtra(DESTINATION_LATITUDE, address.getLatitude() + "");
            intent.putExtra(DESTINATION_LONGITUDE, address.getLongitude() + "");
            System.out.println("******************** WORKEN " + address.getLatitude() + " " + address.getLongitude());
            startActivity(intent);
        }
    }

    //**************************

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }
}
