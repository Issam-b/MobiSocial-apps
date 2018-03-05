package com.derassom.issam.mobisociallab3;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

/**
 * Created by assam on 2/12/2018.
 */

public class WeatherAsyncTask extends AsyncTask<String, Void, String> {

    private Context context;
    private WeatherData weatherData = new WeatherData();

    public WeatherAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... url) {
        // create instance of network call to API
        FetchAPIData fetchWeather = new FetchAPIData( url[0] );
        String response = fetchWeather.FetchData();
        SQLiteHandler db = new SQLiteHandler( context );
        String result = "";
        //if repsonse is not null use it as a result
        if(response != null) {
            // parse the json string to data class
            weatherData.ParseJSONData( response );
            db.addEntry( weatherData );
            result = weatherData.WeatherDataToString();
        }
        // if response is null or bad, use the last entry in the database
        else {
            result = db.getLastEntry().WeatherDataToString();
        }

        // return result
        return result;
    }

    @Override
    protected void onPostExecute( String result ) {
        super.onPostExecute( result );

        MainActivity.progressBar.setVisibility( View.INVISIBLE );

        if(result != "") {
            MainActivity.dataView.setText( String.valueOf( result ) );
        }
        else {
            MainActivity.dataView.setText( String.valueOf( "There's no such city!" ) );
        }
    }
}
