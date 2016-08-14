package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragment.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class BaseActivity extends AppCompatActivity implements ComposeTweetFragment.OnTweetPostListener{

    public Tweet replyTweet;

    protected TwitterClient twitterClient;

    public void registerOnRefreshListListener(WeakReference<OnRefreshListListener> wRefListener) {
        this.mList.add(wRefListener);
    }

    ArrayList<WeakReference<OnRefreshListListener>> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    public void refreshAll(){
        OnRefreshListListener listener;
        for(WeakReference<OnRefreshListListener> l : mList){
            listener = l.get();
            if(listener!= null) {
                listener.onRefresh();
            }
        }
    }

    @Override
    public void onTweetPost() {
        this.refreshAll();
    }


    public interface OnRefreshListListener{
        void onRefresh();
    }

    public void postFavorite(String id) {
        twitterClient.makeFavorite(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        }, id);
    }

    public void postunFavorite(String id){
        twitterClient.unFavorite(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        },id);
    }
}
