package com.codepath.apps.mysimpletweets.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapter.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.adapter.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    public static final String KEY_NAME = "name";
    public static final String KEY_SCREEN_NAME = "screenName";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    SharedPreferences preferences;
    Activity mActivity;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.lvTweets) ListView lvTweets;
    @BindView(R.id.myFAB) FloatingActionButton myFAB;
    private TwitterClient twitterClient;
    ArrayList<Tweet> tweets;
    TweetsArrayAdapter tweetAdapter;
    long sinceId = Long.MIN_VALUE;
    int count = 25;
    boolean loading = false;
    long maxId = Long.MIN_VALUE;
    public static final String MyPREFERENCES = "tweeter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        mActivity = this;
        twitterClient = TwitterApplication.getRestClient();
        ActionBar action = getSupportActionBar();
        action.setIcon(R.drawable.logo);
        action.setDisplayShowHomeEnabled(true);
        action.setDisplayShowTitleEnabled(false);
        preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        populateTimeline();
        tweets = new ArrayList<>();
        tweetAdapter = new TweetsArrayAdapter(this,tweets);
        lvTweets.setAdapter(tweetAdapter);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                maxId = Long.MIN_VALUE;
                sinceId = Long.MIN_VALUE;
                populateTimelineRefresh();
            }
        });

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if(tweets.isEmpty()) {
                    maxId = Long.MIN_VALUE;
                } else {
                    maxId = (tweets.get(tweets.size() - 1).getUid()) - 1;
                }
                sinceId = Long.MIN_VALUE;
                populateTimeline();
                return loading;
            }
        });

        myFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimelineActivity.this,ComposeTweetActivity.class);
                startActivity(intent);
            }
        });
        userProfile();
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        MenuItem item = menu.findItem(R.id.miCompose);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(TimelineActivity.this,ComposeTweetActivity.class);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }
*/
    public void populateTimeline(){
        loading = true;

        if(Utils.isNetworkAvailable(this)) {
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                loading = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                loading = false;
                ArrayList<Tweet> tweetList = Tweet.fromJSONArray(response);
                tweets.addAll(tweetList);
                tweetAdapter.notifyDataSetChanged();
             }
       },sinceId,count,maxId);
        } else {
            final Context context = this;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("");
            alertDialog
                    .setMessage("NO INTERNET CONNECTION")
                    .setCancelable(true)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                           dialog.cancel();
                            if(mActivity != null){
                                mActivity.finish();
                            }
                        }
                    });
            // create alert dialog
            AlertDialog dialog = alertDialog.create();

            dialog.show();

        }
    }

    public void populateTimelineRefresh(){
        loading = true;
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               loading = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                loading = false;
                ArrayList<Tweet> tweetList = Tweet.fromJSONArray(response);
                tweets.clear();
                tweets.addAll(tweetList);
                tweetAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);

            }
        },sinceId,count,maxId);

    }


    public void userProfile(){
        twitterClient.userProfile(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
              try {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(KEY_NAME, responseBody.getString("name"));
                    editor.putString(KEY_SCREEN_NAME, responseBody.getString("screen_name"));
                    editor.putString(KEY_PROFILE_IMAGE, responseBody.getString("profile_image_url"));
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }
}
