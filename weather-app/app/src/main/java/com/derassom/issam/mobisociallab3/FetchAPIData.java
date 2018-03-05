package com.derassom.issam.mobisociallab3;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by assam on 2/13/2018.
 */

public class FetchAPIData {

    private String url;
    private String result;
    URL path = null;

    public FetchAPIData(String url) {
        this.url = url;
    }

    public String FetchData() {
        try {
            path = new URL( this.url );
            HttpURLConnection connection = (HttpURLConnection) path.openConnection();
            int timeout = 60 * 1000;
            connection.setReadTimeout( timeout ); // set request timeout
            connection.setConnectTimeout( timeout );
            connection.setRequestMethod( "GET" ); //set HTTP method
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                // error
                Log.d( "FetchWeather", "bad response!" );

            }
            InputStream stream = connection.getInputStream();
            BufferedReader br = new BufferedReader( new InputStreamReader( stream ) );
            StringBuilder response_body = new StringBuilder( "" );
            String line;
            while ((line = br.readLine()) != null) {
                response_body.append( line );
            }
            br.close();
            stream.close();

            result = response_body.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
