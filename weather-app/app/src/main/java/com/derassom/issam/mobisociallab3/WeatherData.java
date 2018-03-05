package com.derassom.issam.mobisociallab3;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by assam on 2/13/2018.
 */

public class WeatherData {

    private String weatherStatus;
    private String weatherDescription;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private String country;
    private String city;

    public WeatherData( ) {
    }

    public  void ParseJSONData(String jsonString) {
        try {
            // Parse the JSON data
            JSONObject resultObject = new JSONObject( jsonString );
            JSONObject weatherObject = resultObject.getJSONArray( "weather" ).getJSONObject( 0 );
            JSONObject mainObject = new JSONObject( resultObject.getString( "main" ) );
            this.setWeatherStatus( weatherObject.getString( "main" ) );;
            this.setWeatherDescription( weatherObject.getString( "description" ) );
            this.setTemperature( Double.parseDouble( mainObject.getString( "temp" ) ) );
            this.setHumidity( Double.parseDouble( mainObject.getString( "humidity" ) ) );
            this.setWindSpeed( Double.parseDouble( resultObject.getJSONObject( "wind" ).getString( "speed" ) ) );
            this.setCountry( resultObject.getJSONObject( "sys" ).getString( "country" ) );
            this.setCity( resultObject.getString( "name" ) );

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String WeatherDataToString() {
        return this.city + ", " + this.country + "\n" + "Temperature: " + this.temperature.toString() +
                "℃\nHumidity: " + this.humidity.toString() + "%\nWeather: " + this.weatherStatus +
                "\nDescription: " + this.weatherDescription + "\nWind speed: " + this.windSpeed.toString() + "m/s";
    }

    public String getWeatherStatus() {
        return weatherStatus;
    }

    public void setWeatherStatus(String weatherStatus) {
        this.weatherStatus = weatherStatus;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
