package com.derassom.issam.mobisociallab5;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TwitterTimeline.AsyncTaskResponse {


    TextView listLabel;
    EditText usernameText;
    Button fetchButton;
    ListView listView;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get UI elements
        listLabel = findViewById(R.id.listLabel);
        fetchButton = findViewById(R.id.fetchButton);
        usernameText = findViewById(R.id.usernameText);
        listView = findViewById(R.id.listView);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = String.valueOf(usernameText.getText());
                // check if there's internet access
                if(!checkInternetConnectivity(MainActivity.this))
                    Toast.makeText(MainActivity.this, "Check internet access!", Toast.LENGTH_SHORT).show();
                // check username is not empty
                else if(username.matches("")) {
                    Toast.makeText(MainActivity.this, "A username is needed!", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Create twitterTimeline instance and pass username and resources object
                    TwitterTimeline twitterTimeline = new TwitterTimeline(username, getResources());
                    // point the interface instance to this activity
                    twitterTimeline.delegateResp = MainActivity.this;
                    // execute the AsyncTask, and process the response in processResponse
                    twitterTimeline.execute();
                }
            }});
    }

    // return internet connectivity state
    private boolean checkInternetConnectivity(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    // implements method of interface AsyncTaskResponse to get response from the AsyncTask
    @Override
    public void processResponse(List<String> response) {
        Log.d("TwitterTimeline", response + "");
        if (response.isEmpty()) {
            Toast.makeText(MainActivity.this, "No user or no stories!", Toast.LENGTH_SHORT).show();
        }
        else if(response.get(0).contains("statusCode=400")) {
            Toast.makeText(MainActivity.this, "Authentication problem!", Toast.LENGTH_SHORT).show();
            response.remove(0);
        }
        else {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    MainActivity.this, android.R.layout.simple_list_item_1, response);
            listView.setAdapter(arrayAdapter);
        }
    }
}
