package com.codepath.apps.mysimpletweets.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.BaseActivity;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.fragment.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    BaseActivity mActivity;
    public TweetsArrayAdapter(BaseActivity activity, List<Tweet> tweets) {
        super(activity,0, tweets);
        mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);
        }
        viewHolder = new ViewHolder(convertView);
        viewHolder.tvUser.setText('@'+tweet.getUser().getScreenName());
        viewHolder.tvTweetBody.setText(tweet.getBody());
        viewHolder.tvCreatedAt.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvFav.setText(String.valueOf(tweet.getFavoriteCount()));
        viewHolder.tvRetweet.setText(String.valueOf(tweet.getRetweetCount()));
        viewHolder.ivProfileImage.setImageResource(0);
        if(tweet.isFavorited()){
            viewHolder.ivFav.setColorFilter(ContextCompat.getColor(getContext(),R.color.like));
        } else{
            viewHolder.ivFav.setColorFilter(ContextCompat.getColor(getContext(),R.color.default_color));
        }
        if(tweet.isRetweeted()){
            viewHolder.ivretweet.setColorFilter(ContextCompat.getColor(getContext(),R.color.retweet));
        } else{
            viewHolder.ivretweet.setColorFilter(ContextCompat.getColor(getContext(),R.color.default_color));
        }
        if(tweet.getType() != null && tweet.getType().equalsIgnoreCase("photo")){
            viewHolder.ivTweetData.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(tweet.getMediaUrl()).into(viewHolder.ivTweetData);
        } else {
            viewHolder.ivTweetData.setVisibility(View.GONE);
        }

        viewHolder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(tweet);
            }
        });

        final WeakReference<TextView> weakTextViewRef = new WeakReference<TextView>(viewHolder.tvFav);
        viewHolder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv = (ImageView) view.findViewById(R.id.ivFavorites);
                if(tweet.isFavorited()){
                    iv.setColorFilter(ContextCompat.getColor(getContext(),R.color.default_color));
                    tweet.setFavorited(false);
                    tweet.setFavoriteCount(tweet.getFavoriteCount() -1);
                    mActivity.postunFavorite(tweet.getIdStr());
                } else{
                    iv.setColorFilter(ContextCompat.getColor(getContext(),R.color.like));
                    tweet.setFavorited(true);
                    tweet.setFavoriteCount(tweet.getFavoriteCount() + 1);
                    mActivity.postFavorite(tweet.getIdStr());
                }
                TextView tv = weakTextViewRef.get();
                if(tv != null)
                    tv.setText(String.valueOf(tweet.getFavoriteCount()));
            }
        });

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(mActivity,ProfileActivity.class);
                    intent.putExtra("screenName",tweet.getUser().getScreenName());
                mActivity.startActivity(intent);

            }
        });
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);
        return convertView;
    }

    public void showEditDialog(Tweet tweet) {
        mActivity.replyTweet = tweet;
        FragmentManager fm = mActivity.getSupportFragmentManager();
        ComposeTweetFragment composeDialogFragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putBoolean("isReply",true);
        composeDialogFragment.setArguments(args);
        composeDialogFragment.show(fm, "fragment_reply_tweet");

    }

    static class ViewHolder{
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUser) TextView tvUser;
        @BindView(R.id.tvTweetBody) TextView tvTweetBody;
        @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvFavorite) TextView tvFav;
        @BindView(R.id.tvRetweet) TextView tvRetweet;
        @BindView(R.id.ivFavorites) ImageView ivFav;
        @BindView(R.id.ivRetweet) ImageView ivretweet;
        @BindView(R.id.ivTweetData) ImageView ivTweetData;
        @BindView(R.id.ivReply) ImageView ivReply;

        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
