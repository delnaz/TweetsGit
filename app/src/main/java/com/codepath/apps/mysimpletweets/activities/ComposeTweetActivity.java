package com.codepath.apps.mysimpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ComposeTweetActivity extends AppCompatActivity {

    SharedPreferences preferences;
    @BindView(R.id.tvBody) EditText tvBody;
    @BindView(R.id.tvCount) TextView tvCount;
    @BindView(R.id.tvUserName)TextView tvUserName;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.ivProfile)ImageView ivProfile;
   // TextView tvCount;
    //EditText tvBody;
    String status = "";
    TwitterClient twitterClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        ButterKnife.bind(this);
        twitterClient = TwitterApplication.getRestClient();
      //  tvBody = (EditText) findViewById(R.id.tvBody);
       // tvCount = (TextView)findViewById(R.id.tvCount);
        preferences = getSharedPreferences(TimelineActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String name = preferences.getString(TimelineActivity.KEY_NAME, null);
        if(name == null)
            tvUserName.setVisibility(View.INVISIBLE);
        else
            tvUserName.setText(name);

        name = preferences.getString(TimelineActivity.KEY_SCREEN_NAME, null);
        if(name == null)
            tvScreenName.setVisibility(View.INVISIBLE);
        else
            tvScreenName.setText('@'+name);

        name = preferences.getString(TimelineActivity.KEY_PROFILE_IMAGE, null);
        if(name != null){
            Glide.with(this).load(name).into(ivProfile);
        }

        tvBody.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                tvCount.setText(140 - s.toString().length() + "/140");
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

    }

    public void composeTweet(View view) {

        //tvBody = (EditText)findViewById(R.id.tvBody);
        status = tvBody.getText().toString();
        //twitterClient = TwitterApplication.getRestClient();
        postTweet();
      //  Toast.makeText(getBaseContext(),"testing tweet",Toast.LENGTH_SHORT).show();
    }

    public void postTweet(){
        twitterClient.composeTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                Log.d("success", "onSuccess: " + responseBody.toString());
          //      Toast.makeText(getBaseContext(),"inside successs",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ComposeTweetActivity.this,TimelineActivity.class);
                startActivity(intent);
               /*
                TimelineActivity timeAc = new TimelineActivity();
                timeAc.populateTimeline();*/
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("error", "onFailure: " + errorResponse.toString());
          //      Toast.makeText(getBaseContext(),"inside failure",Toast.LENGTH_SHORT).show();

            }
        },status);
    }

}
