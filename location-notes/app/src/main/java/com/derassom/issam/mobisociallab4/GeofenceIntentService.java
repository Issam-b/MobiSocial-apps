package com.derassom.issam.mobisociallab4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by assam on 2/19/2018.
 */

public class GeofenceIntentService extends IntentService {

    NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 1145;

    // distance threshold to 100 meters
    final private int DISTANCE_THRESHOLD = 100;

    public GeofenceIntentService() {
        super( "GeofenceIntentService" );
    }

    public GeofenceIntentService(String name) {
        super( name );
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // crate database instance
        SQLiteHandler database = new SQLiteHandler( this );

        Location curLocation = MainActivity.mLocation;
        if(checkPermissions()) {
            MessageDataHandler mMessageData = database.searchDB( curLocation, DISTANCE_THRESHOLD );

            if(mMessageData.getStatus().equals( "ok" )) {
                pushNotification( mMessageData.getTitle(), mMessageData.getMessage() );
                    MainActivity.lastLocation = curLocation;
            }
        }
        // implement a way to re-check for permissions later


        // check if user leaving geofence and dismiss notification
        if(MainActivity.mLocation.distanceTo( MainActivity.lastLocation ) >= DISTANCE_THRESHOLD) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService( Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    // push notification
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
                .setTicker( "Saved location aware message!" )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon( BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.ic_launcher_foreground))
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        // send notification to notification tray
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    // Check for permissions
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
}
