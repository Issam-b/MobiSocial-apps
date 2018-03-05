package com.derassom.issam.mobisociallab3;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by assam on 2/13/2018.
 */

public class NotificationService extends IntentService {

    NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 15464;
    private  static  final String NOTIFICATION_TITLE = "Current weather";
    // weather data object instance
    WeatherData weatherData = new WeatherData( );

    public NotificationService() {
        super( "NotificationService" );
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("MobiSocialService", "Service is running!");

        // receive url from the mainActivity
        String url = intent.getStringExtra("url");
        // create instance of network call to API
        FetchAPIData fetchWeather = new FetchAPIData( url );
        String response = fetchWeather.FetchData();
        SQLiteHandler db = new SQLiteHandler( this );
        String notificationMessage;
        if(response != null) {
            db.addEntry( weatherData );
            // parse the json string to data class
            weatherData.ParseJSONData( response );
        }
        else {
            // if no internet connection or bad response use the data stored in database
            Log.d( "FetchService", "Couldn't fetch data, read from database" );
            weatherData = db.getLastEntry();
        }
        notificationMessage = "It's " + weatherData.getTemperature() + " and there's " +
                weatherData.getWeatherStatus() + " now in " + weatherData.getCity();
        pushNotification( NOTIFICATION_TITLE, notificationMessage);
    }

    private void pushNotification(String title, String message) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Send it to MainActivity
        Intent intentP = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intentP,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // build notification
        Notification notification  = new Notification.Builder(this)
                .setContentTitle( title )
                .setContentText( message )
                .setTicker( "New weather info!" )
                .setSmallIcon(R.drawable.icon_sun)
                .setLargeIcon( BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.icon_sun))
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        // send notification to notification tray
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
