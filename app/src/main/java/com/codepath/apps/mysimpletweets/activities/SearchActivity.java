package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapter.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.lvSearch)
    ListView lvSearch;
    private TwitterClient twitterClient;
    ArrayList<Tweet> tweets;
    TweetsArrayAdapter tweetAdapter;
    String query = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle args = getIntent().getExtras();
        query = args.getString("query");
        ButterKnife.bind(this);
        twitterClient = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        tweetAdapter = new TweetsArrayAdapter(this,tweets);
        lvSearch.setAdapter(tweetAdapter);

        searchList(query);
    }

    public void searchList(String query){
        twitterClient.search(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {

                ArrayList<Tweet> tweetList = new ArrayList<Tweet>();
                try {
                    JSONArray arr = responseBody.getJSONArray("statuses");
                    for(int i = 0; i< arr.length(); i++){
                        Tweet tweet = Tweet.fromJSON(arr.getJSONObject(i));
                        tweetList.add(tweet);
                    }
                    tweets.clear();
                    tweets.addAll(tweetList);
                    tweetAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        },query);
    }
}
