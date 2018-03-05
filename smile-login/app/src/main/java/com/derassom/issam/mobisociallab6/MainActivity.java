package com.derassom.issam.mobisociallab6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    protected SurfaceView cameraView;
    private SurfaceHolder cameraHolder;
    private CameraSource cameraSource;
    private FaceDetector faceDetector;

    private StatusUpdater statusUpdater = new StatusUpdater();
    final private String UPDATE_FACE = "UPDATE_FACE";
    final private String NO_FACE_DETECTION = "Face detection not supported!";
    final protected String BROADCAST_EXTRA = "smile";
    final private String INITIALISE_DETECTOR = "Initialising face detection...";
    final protected double DETECTION_PROBABILITY = 0.9;
    final protected int FACE_DETECTION_PERMISSION_ID = 12;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check and/or ask for permissions for camera and storage
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                FACE_DETECTION_PERMISSION_ID);
        }

        statusText = findViewById(R.id.statusText);
        statusText.setText(getString(R.string.status_text, "no"));
        cameraView = findViewById(R.id.cameraView);
        cameraHolder = cameraView.getHolder();
        cameraHolder.setFixedSize(Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels);
    }

    // handle the button click, camera and face detector setup
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_FACE);
        registerReceiver(statusUpdater, filter);

        final Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), INITIALISE_DETECTOR,
                        Toast.LENGTH_SHORT).show();

                if (cameraSource != null) cameraSource.stop();

                if (faceDetector == null) {
                    faceDetector = new FaceDetector.Builder(getApplicationContext())
                            .setTrackingEnabled(true)
                            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS) // open eyes, smile
                            .build();
                }

                // whether required dependencies are available locally for detection to work
                if (faceDetector.isOperational()) {
                    faceDetector.setProcessor(new LargestFaceFocusingProcessor(faceDetector, new FaceTracker()));
                    cameraSource = new CameraSource.Builder(getApplicationContext(), faceDetector)
                            .setFacing(CameraSource.CAMERA_FACING_FRONT)
                            .setRequestedFps(30)
                            .build();
                    try {
                        cameraSource.start(cameraHolder);
                    } catch (SecurityException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("Error", NO_FACE_DETECTION);
                    Toast.makeText(getApplicationContext(), NO_FACE_DETECTION,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case FACE_DETECTION_PERMISSION_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permissionOk", "permissions granted!");
                } else
                    finish();
            }
        }
    }

    // release camera and detector objects and stop the broadcast receiver
    @Override
    protected void onPause() {
        super.onPause();
        if (statusUpdater != null) unregisterReceiver(statusUpdater);
        if (cameraSource != null) cameraSource.release();
        if (faceDetector != null) faceDetector.release();
    }

    // class to check camera feed for faces and get the smile probability
    // which will be checked, if it exceeds the threshold, a broadcast will be sent
    private class FaceTracker extends Tracker<Face> {
        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            super.onUpdate(detections, face);
            if (face.getIsSmilingProbability() > DETECTION_PROBABILITY) {
                Intent smilingFace = new Intent(UPDATE_FACE);
                smilingFace.putExtra(BROADCAST_EXTRA, true);
                sendBroadcast(smilingFace);
            }
        }
    }

    // Broadcast receiver to update text and switch to success activity
    private class StatusUpdater extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // when smile detected update status text and go to success activity
            // and clear the current one
            if (Objects.equals(intent.getAction(), UPDATE_FACE)) {
                statusText.setText(getString(R.string.status_text, "yes"));
                Intent successIntent = new Intent(MainActivity.this, SuccessLogin.class);
                successIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                successIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(successIntent);
            }
            statusText.invalidate();
        }
    }
}
