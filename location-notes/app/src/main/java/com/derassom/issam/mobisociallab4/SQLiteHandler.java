package com.derassom.issam.mobisociallab4;

/**
 * Created by assam on 2/19/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "geofence_messages.db";
    private static final String TABLE_NAME = "geofenceData";
    private static final String ID = "id";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";

    public SQLiteHandler(Context context) {
        super( context, DB_NAME, null, 1 );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LATITUDE + " DOUBLE, " +
                LONGITUDE + " DOUBLE, " +
                TITLE + " TEXT, " +
                MESSAGE + " TEXT " +
                ")";
        sqLiteDatabase.execSQL(query);
        Log.d("MobiSocialSQLHandler", "Table create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate( sqLiteDatabase );
    }

    public void addEntry(MessageDataHandler data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( LATITUDE, data.getLatitude() );
        values.put( LONGITUDE, data.getLongitude() );
        values.put( TITLE, data.getTitle() );
        values.put( MESSAGE, data.getMessage() );
        db.insert( TABLE_NAME, null, values );
        db.close();
        Log.d("MobiSocialSQLHandler", "Entry added");
    }

    public MessageDataHandler searchDB(Location locationA, float DISTANCE_THRESHOLD) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY ID DESC LIMIT 1";
        Cursor cursor = db.rawQuery( query, null );
        Location locationB = new Location( "Location B" );
        String title, message;
        int id;
        if (cursor.moveToFirst()) {
            do {
                locationB.setLatitude( cursor.getDouble( cursor.getColumnIndex( LATITUDE ) ) );
                locationB.setLongitude( cursor.getDouble( cursor.getColumnIndex( LONGITUDE ) ) );

                float distance = locationA.distanceTo( locationB );
                Log.d( "MobiSocialSQLHandler", "distance is " + distance );
                if(distance <= DISTANCE_THRESHOLD) {
                    title = cursor.getString( cursor.getColumnIndex( TITLE ) );
                    message = cursor.getString( cursor.getColumnIndex( MESSAGE ) );


                    // return the message that matches distance
                    return new MessageDataHandler(locationB.getLatitude(), locationB.getLongitude(),
                            title, message);
                }
            } while (cursor.moveToNext());
        }
        Log.d( "MobiSocialSQLHandler", "data fetched" );
        db.close();
        cursor.close();

        return new MessageDataHandler( "bad" );
    }
}
