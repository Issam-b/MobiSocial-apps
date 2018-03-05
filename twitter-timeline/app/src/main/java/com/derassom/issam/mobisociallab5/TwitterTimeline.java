package com.derassom.issam.mobisociallab5;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by issam on 2/27/18.
 * MobiSocial Course
 */

public class TwitterTimeline extends AsyncTask<List, List, List<String>>
{

    // interface to report response to mainActivity
    public interface AsyncTaskResponse {
        void processResponse(List<String> response);
    }

    AsyncTaskResponse delegateResp = null;

    private String username;
    private Resources resources;

    TwitterTimeline(String username, Resources resources) {
        this.username = username;
        this.resources = resources;
    }

    // background task
    @Override
    protected List<String> doInBackground(List[] lists) {
        // build the twitter factory to fetch data
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true).setOAuthConsumerKey(resources.getString(R.string.consumer_key))
                .setOAuthConsumerSecret(resources.getString(R.string.consumer_secret_key))
                .setOAuthAccessToken(resources.getString(R.string.access_token))
                .setOAuthAccessTokenSecret(resources.getString(R.string.access_token_Secret));

        TwitterFactory twitterFactory=new TwitterFactory(configurationBuilder.build());
        Twitter twitter= twitterFactory.getInstance();

        // array to store the user timeline stories
        List<String> listStrings = new ArrayList<>();
        try {
            List<twitter4j.Status> status = twitter.getUserTimeline(this.username);
            // add element from twitter status object to array list
            for(twitter4j.Status st : status) {
                listStrings.add(st.getText());
            }

        } catch (Exception e) {
            // if authentication error pass it to mainActivity to handle it
            if(e.toString().contains("statusCode=400")) {
                listStrings.add(0, "statusCode=400");
                Log.d("TwitterTimeline: ", "Authentication error!");
            }
            e.printStackTrace();
        }

        return listStrings;
    }

    // pass the response to the mainActivity
    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        delegateResp.processResponse(strings);
    }


}
