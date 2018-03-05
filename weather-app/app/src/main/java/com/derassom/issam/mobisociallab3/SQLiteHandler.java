package com.derassom.issam.mobisociallab3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by assam on 2/13/2018.
 */

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "weather.db";
    private static final String TABLE_NAME = "weatherData";
    private static final String  ID = "id";
    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String STATUS = "weatherStatus";
    private static final String DESCRIPTION = "weatherDescription";
    private static final String TEMP = "temperature";
    private static final String WIND = "windSpeed";
    private static final String HUMIDITY = "humidity";

    public SQLiteHandler(Context context) {
        super( context, DB_NAME, null, 1 );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CITY + " TEXT, " +
                COUNTRY + " TEXT, " +
                STATUS + " TEXT, " +
                DESCRIPTION + " TEXT, " +
                TEMP + " DOUBLE, " +
                WIND + " DOUBLE, " +
                HUMIDITY + " DOUBLE " +
                ")";
        sqLiteDatabase.execSQL(query);
        Log.d("MobiSocialSQLHandler", "Table create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate( sqLiteDatabase );
    }

    public void addEntry(WeatherData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( CITY, data.getCity() );
        values.put( COUNTRY, data.getCountry() );
        values.put( STATUS, data.getWeatherStatus() );
        values.put( DESCRIPTION, data.getWeatherDescription() );
        values.put( TEMP, data.getTemperature() );
        values.put( WIND, data.getWindSpeed() );
        values.put( HUMIDITY, data.getHumidity() );
        db.insert( TABLE_NAME, null, values );
        db.close();
        Log.d("MobiSocialSQLHandler", "Entry added");
    }

    public WeatherData getLastEntry() {
        SQLiteDatabase db = this.getWritableDatabase();
        WeatherData data = new WeatherData();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY ID DESC LIMIT 1";
        Cursor cursor = db.rawQuery( query, null );

        if (cursor.moveToFirst()) {
            do {
                data.setCity( cursor.getString( cursor.getColumnIndex( CITY ) ) );
                data.setCountry( cursor.getString( cursor.getColumnIndex( COUNTRY ) ) );
                data.setWeatherStatus( cursor.getString( cursor.getColumnIndex( STATUS ) ) );
                data.setWeatherDescription( cursor.getString( cursor.getColumnIndex( DESCRIPTION ) ) );
                data.setTemperature( cursor.getDouble( cursor.getColumnIndex( TEMP ) ) );
                data.setWindSpeed( cursor.getDouble( cursor.getColumnIndex( WIND ) ) );
                data.setHumidity( cursor.getDouble( cursor.getColumnIndex( HUMIDITY ) ) );
            } while (cursor.moveToNext());
        }
        Log.d( "MobiSocialSQLHandler", "data fetched" );
        db.close();
        return data;
    }
}
