package com.codepath.apps.mysimpletweets.activities;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.fragment.UserFragment;
import com.codepath.apps.mysimpletweets.fragment.UserTweetsFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends BaseActivity implements  UserFragment.OnCloseListener{

    @BindView(R.id.ivUsersProfileImage)
    ImageView ivProfile;
    @BindView(R.id.tvTagLineUser)
    TextView tvTagLine;
    @BindView(R.id.tvUserProfNameUser)
    TextView tvUserProfName;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.user_container)
    FrameLayout user_container;

    @BindView(R.id.tvFriendsUser) Button bFollowing;
    @BindView(R.id.tvFollow) Button bFollower;
    Activity mActivity;

    String screenName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Bundle args = getIntent().getExtras();
        screenName = args.getString("screenName");
        ButterKnife.bind(this);
        bFollower.setEnabled(false);
        bFollowing.setEnabled(false);
        mActivity = this;
        twitterClient = TwitterApplication.getRestClient();
        UserTweetsFragment fragment = UserTweetsFragment.getInstance(this, screenName);
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment).commit();
        user_container.setVisibility(View.GONE);
        followersList();
        friendsList();
        usersProfile();

        bFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserFragment fragment = UserFragment.newInstance(followingList, "Following");
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                user_container.setVisibility(View.VISIBLE);
                ft.add(R.id.user_container, fragment).commit();
            }
        });

        bFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserFragment fragment = UserFragment.newInstance(followerList, "Followers");
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                user_container.setVisibility(View.VISIBLE);
                ft.add(R.id.user_container, fragment).commit();
            }
        });

    }

    ArrayList<User> followerList = new ArrayList<>();
    ArrayList<User> followingList = new ArrayList<>();

    public void usersProfile(){
        twitterClient.usersProfile(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {

                try {
                    tvTagLine.setText(responseBody.getString("description"));
                    tvUserProfName.setText(responseBody.getString("name"));
                    bFollower.setText(responseBody.getInt("followers_count")+" Followers");
                    bFollowing.setText(responseBody.getInt("friends_count")+" Following");
                    Glide.with(getApplicationContext()).load(responseBody.getString("profile_image_url")).into(ivProfile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        },screenName);
    }


    public void followersList(){
        twitterClient.followersList(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                ArrayList<User> users = new ArrayList<User>();
                followerList.clear();;
                try {
                    JSONArray arr = responseBody.getJSONArray("users");
                    for(int i = 0; i< arr.length(); i++){
                        User user = User.fromJSON(arr.getJSONObject(i));
                        followerList.add(user);
                    }

                    bFollower.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        },screenName);
    }
    public void friendsList(){
        twitterClient.friendsList(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                ArrayList<User> users = new ArrayList<User>();
                followingList.clear();
                try {
                    JSONArray arr = responseBody.getJSONArray("users");
                    for(int i = 0; i< arr.length(); i++){
                        User user = User.fromJSON(arr.getJSONObject(i));
                        followingList.add(user);
                    }

                    bFollowing.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        },screenName);
    }

    @Override
    public void onClose() {
        user_container.setVisibility(View.GONE);
    }
}