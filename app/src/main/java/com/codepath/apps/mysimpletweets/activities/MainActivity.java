package com.codepath.apps.mysimpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.fragment.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.fragment.MentionsFragment;
import com.codepath.apps.mysimpletweets.fragment.TimelineFragment;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActivity {

    public static final String MyPREFERENCES = "tweeter";
    public static final String KEY_NAME = "name";
    public static final String KEY_SCREEN_NAME = "screenName";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_FOLLOWERS_COUNT = "followers_count";
    public static final String KEY_FOLLOWING_COUNT = "friends_count";
    public static final String KEY_TAGLINE = "description";
    SharedPreferences preferences;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @BindView(R.id.tablayout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        twitterClient = TwitterApplication.getRestClient();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });
        userProfile();
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
                    editor.putString(KEY_TAGLINE, responseBody.getString("description"));
                    editor.putString(KEY_FOLLOWERS_COUNT, responseBody.getString("followers_count"));
                    editor.putString(KEY_FOLLOWING_COUNT, responseBody.getString("friends_count"));
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

    public void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment composeDialogFragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putBoolean("isReply",false);

        composeDialogFragment.setArguments(args);
        composeDialogFragment.show(fm, "fragment_compose_tweet");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline,menu);

        Drawable drawable = menu.findItem(R.id.miProfile).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,android.R.color.white));
        menu.findItem(R.id.miProfile).setIcon(drawable);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);

    }
    public void onProfileView(MenuItem item) {
        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
        intent.putExtra("screenName",preferences.getString(KEY_SCREEN_NAME, null));
        MainActivity.this.startActivity(intent);
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0) {
                return TimelineFragment.getInstance(MainActivity.this);
            } else {
                return MentionsFragment.getInstance(MainActivity.this);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                    return "Mentions";
            }
            return null;
        }
    }


}
