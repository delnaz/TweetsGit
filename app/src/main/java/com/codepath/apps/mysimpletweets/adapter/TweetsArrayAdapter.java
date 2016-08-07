package com.codepath.apps.mysimpletweets.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {


    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context,0, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
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
        }
        if(tweet.isRetweeted()){
            viewHolder.ivretweet.setColorFilter(ContextCompat.getColor(getContext(),R.color.retweet));
        }
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);
        return convertView;
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

        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}