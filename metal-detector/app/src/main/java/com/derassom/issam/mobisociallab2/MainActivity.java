package com.derassom.issam.mobisociallab2;

import android.content.DialogInterface;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import 	android.content.Context;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements  SensorEventListener {

    // Declare variables
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView valueText, sensorTypeText, detectionText;
    private int threshold = 100;
    MediaPlayer mPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        valueText = (TextView) findViewById(R.id.valueText);
        sensorTypeText = (TextView) findViewById(R.id.sensorTypeText);
        detectionText = (TextView) findViewById(R.id.detectionText);

        // MediaPlayer instance to play the beep sound
        mPlay = MediaPlayer.create(this, R.raw.beep);

        // Since the permission to access the magnetometer and compass is granted directly
        // there's no need to request it.
        // Below method to see the device has a magnetometer or not and exit if no appropriate
        // sensor is present.
        MagnetometerExist();
    }

    // Get the sensor data again after resuming the app
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // stop using the sensor when the app is paused
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    // Get the type of sensor being present in the device
    protected void MagnetometerExist() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorTypeText.setText(getString(R.string.magnetValueText));
        }
        else {
            sensorTypeText.setText(getString(R.string.no_sensor));

            // Alert pop up to exit app when no sensor is found
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No sensor!");
            builder.setMessage("No appropriate sensor was found!");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Toast.makeText(getApplicationContext(),"Exiting!",Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                            // Kill the app
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    });
            // show the alert dialog
            builder.show();
        }
    }

    // Read the sensor data and calculate the needed values
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // Get sensor values from each axis
            float magnetoX = event.values[0];
            float magnetoY = event.values[1];
            float magnetoZ = event.values[2];
            double magnetoMagnitude = Math.sqrt((Math.pow(magnetoX, 2)) + (Math.pow(magnetoY, 2)) +
                    (Math.pow(magnetoZ, 2)));

            // set value on the screen
            valueText.setText(String.format("%.2f", magnetoMagnitude) + " \u00B5Tesla");

            // update metal detection text according to the threshold
            if (magnetoMagnitude > threshold) {
                // update detection text and background color and play beep sound
                detectionText.setText(R.string.metal_detected);
                detectionText.setBackgroundColor(getResources().getColor(R.color.red));
                mPlay.start();
            }
            else {
                // update detection text and background color
                detectionText.setText(R.string.no_metal);
                detectionText.setBackgroundColor(0);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
