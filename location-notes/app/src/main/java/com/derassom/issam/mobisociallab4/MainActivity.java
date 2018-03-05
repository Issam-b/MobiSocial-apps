package com.derassom.issam.mobisociallab4;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText titleText;
    EditText messageText;
    Button addMessageButton;
    LocationListener mLocationListener;
    SQLiteHandler database = new SQLiteHandler( this );
    final int NOTIFICATION_PER_HOUR = 60;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 100;
    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();
    static Location mLocation = new Location( "current location" );
    static Location lastLocation = new Location( "last location" );
    LocationManager mLocationManager;
    final private int MIN_GPS_UPDATE_TIME = 30000;
    final private int MIN_DISTANCE_CHANGE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        titleText = findViewById( R.id.titleText );
        messageText = findViewById( R.id.messageText );
        addMessageButton = findViewById( R.id.addMessageButton );

        getCurrentLocation();

        addMessageButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                String title = titleText.getText().toString();
                String message = messageText.getText().toString();

                if(checkPermissions() && isLocationEnabled(MainActivity.this) ) {
                    if(!title.isEmpty() && !message.isEmpty()) {
                        // get last known location
                        Log.d("MobiSocialSQLHandler", mLocation.getLatitude() + " " + mLocation.getLongitude());
                        MessageDataHandler mMessageData = new MessageDataHandler( mLocation.getLatitude(),
                                mLocation.getLongitude(), title, message );

                        // add entry to database
                        database.addEntry( mMessageData );
                    } else {
                        Toast.makeText( MainActivity.this, "Message shouldn't be empty!",
                                Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( MainActivity.this, "Enable location to continue!",
                            Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        // call service
        startNotificationService();
    }


    private void getCurrentLocation() {

        mLocationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        Log.d(DEBUG_TAG, "getLocation method");
        mLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d("Debug Location", "onLocationChanged");
                Toast.makeText(MainActivity.this, location.getLatitude() + " " + location.getLongitude(),
                        Toast.LENGTH_SHORT).show();
                mLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                startActivity( intent );
            }
        };

        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            return;
        }

        mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, MIN_GPS_UPDATE_TIME,
                MIN_DISTANCE_CHANGE, mLocationListener );
        Log.d("Debug Location", "requestLocationUpdates");

//        mLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//        Log.d("Debug Location", mLocation.getLatitude() + " " + mLocation.getLongitude());

    }

    // method to start the alarm manager and service
    private void startNotificationService() {
        AlarmManager alarmManager;
        PendingIntent alarmIntent;

        // get intent of notification service
        Intent serviceIntent = new Intent( MainActivity.this, GeofenceIntentService.class );
        alarmIntent = PendingIntent.getService( this, 0, serviceIntent, 0 );

        // Alarm manager call to notification service
        alarmManager = (AlarmManager) getSystemService( Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR / NOTIFICATION_PER_HOUR,
                AlarmManager.INTERVAL_HOUR / NOTIFICATION_PER_HOUR, alarmIntent);
        Log.d("GeofenceAlarm", "Started service");
    }

    // Check for permissions
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(DEBUG_TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(DEBUG_TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(DEBUG_TAG, "Permission granted.");
            } else {
                Toast.makeText(MainActivity.this, "No permission granted!",
                        Toast.LENGTH_SHORT).show();

                // exit app
                finish();
            }
        }
    }

    // https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
}
