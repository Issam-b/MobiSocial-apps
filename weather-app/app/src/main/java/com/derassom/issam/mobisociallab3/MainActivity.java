package com.derassom.issam.mobisociallab3;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    static TextView dataView;
    Button searchButton;
    EditText searchText;
    static ProgressBar progressBar;
    private final String APP_ID = "0d12633679d0e0e8680909f77c75fcaa";
    private final  String API_PATH = "http://api.openweathermap.org/data/2.5/weather?q=";
    private  final String METRIC_APPID = "&units=metric&APPID=";
    private final int NOTIFIACTION_PER_HOUR = 120;
//    private int SERVICE_UNIQUE_ID;
    SQLiteHandler db = new SQLiteHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataView = (TextView) findViewById( R.id.dataView );
        searchButton = (Button) findViewById( R.id.searchButton );
        searchText = (EditText) findViewById( R.id.searchText );
        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        progressBar.setVisibility( View.INVISIBLE );

        // search button onclick listener
        searchButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility( View.VISIBLE );
                String city = searchText.getText().toString();

                // create a WeatherAsyncTask instance
                WeatherAsyncTask weatherTask = new WeatherAsyncTask(getApplicationContext());
                String url = API_PATH + city + METRIC_APPID + APP_ID;
                weatherTask.execute( url );

                // start service with alarm manager with exact schedules of 30 seconds
                startNotificationService( url );
            }
        } );
    }

    // method to start the alarm manager and service
    private void startNotificationService(String url) {
        AlarmManager alarmManager;
        PendingIntent alarmIntent;
//        Random rdm = new Random();
        // to assign new intent if city changed
//        SERVICE_UNIQUE_ID = rdm.nextInt(10 - 0);

        // get intent of notification service
        Intent serviceIntent = new Intent( MainActivity.this, NotificationService.class );
        // send url of city weather to service intent
        serviceIntent.putExtra( "url", url );
        alarmIntent = PendingIntent.getService( this, 0, serviceIntent, 0 );
        // Alarm manager call to notification service
        alarmManager = (AlarmManager) getSystemService( Context.ALARM_SERVICE);
        // if the alarm is running stop it and the intentService
        alarmManager.cancel( alarmIntent );
        if(isMyServiceRunning()) {
            alarmIntent.cancel();
        }
        alarmIntent = PendingIntent.getService( this, 0, serviceIntent, 0 );
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR / NOTIFIACTION_PER_HOUR,
                AlarmManager.INTERVAL_HOUR / NOTIFIACTION_PER_HOUR, alarmIntent);
        Log.d("WeatherAlarm", "Started service");
    }

    // check if the service is running or not
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NotificationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
