package com.codepath.apps.mysimpletweets.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.activities.BaseActivity;
import com.codepath.apps.mysimpletweets.adapter.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.adapter.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class UserTweetsFragment extends BlankFragment {


    int count = 25;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.lvTweets)
    ListView lvTweets;

    BaseActivity mActivity;
    String mScreenName;

    public UserTweetsFragment() {
        // Required empty public constructor
    }


    private TwitterClient twitterClient;
    ArrayList<Tweet> tweets;
    TweetsArrayAdapter tweetAdapter;

    // static TimelineFragment fragment;
    public static UserTweetsFragment getInstance(BaseActivity activity, String screenName) {
        //if(fragment == null) {
        UserTweetsFragment fragment = new UserTweetsFragment();
        fragment.mActivity = activity;
        fragment.mActivity.registerOnRefreshListListener(new WeakReference<BaseActivity.OnRefreshListListener>(fragment));
        fragment.mScreenName = screenName;
        //}
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        twitterClient = TwitterApplication.getRestClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    // Methods for Home fragment
    long sinceId = Long.MIN_VALUE;
    boolean loading = false;
    long maxId = Long.MIN_VALUE;

    public void populateTimeline(){
        loading = true;

        if(Utils.isNetworkAvailable(getActivity())) {
            twitterClient.userTimeline(new JsonHttpResponseHandler() {
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
            },mScreenName,sinceId,count,maxId);
        } else {
            final Context context = getContext();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("");
            alertDialog
                    .setMessage("NO INTERNET CONNECTION")
                    .setCancelable(true)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                            if(getActivity() != null){
                                getActivity().finish();
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
        maxId = Long.MIN_VALUE;
        sinceId = Long.MIN_VALUE;
        twitterClient.userTimeline(new JsonHttpResponseHandler() {
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
                if(UserTweetsFragment.this.getUserVisibleHint()) {
                    tweetAdapter.notifyDataSetChanged();
                }
                //TODO swipe disable fragment
                swipeContainer.setRefreshing(false);

            }
        },mScreenName,sinceId,count,maxId);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        tweets = new ArrayList<>();
        tweetAdapter = new TweetsArrayAdapter(mActivity,tweets);

        lvTweets.setAdapter(tweetAdapter);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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

        populateTimeline();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && tweetAdapter != null) {
            tweetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        populateTimelineRefresh();
    }

}

