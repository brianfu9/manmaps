package com.example.manmaps3;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String DESTINATION_LATITUDE = "com.example.myfirstapp.MESSAGE";
    public static final String DESTINATION_LONGITUDE = "com.example.myfirstapp.MESSAGE";
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, CompassActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


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
            System.out.println("BORKEN " + errorMessage);
        } else {
            Address address = addresses.get(0);

            Log.i(TAG, "address_found");

            intent.putExtra(DESTINATION_LATITUDE, address.getLatitude() + "");
            intent.putExtra(DESTINATION_LONGITUDE, address.getLongitude() + "");
            startActivity(intent);
        }
    }
}
