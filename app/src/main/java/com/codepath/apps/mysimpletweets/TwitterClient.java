package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "C00uqJ4Kl6NzvCTBLnNlMimQm";       // Change this
	public static final String REST_CONSUMER_SECRET = "blghCyxjNsVXzMT3Aji5bNalLhU4GUxBkHbyaUCQpE8fgoXdWd"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}


	public void getHomeTimeline(AsyncHttpResponseHandler handler,long sinceId, int count,long maxId){
		Log.d("1111", "getHomeTimeline: " + maxId + sinceId);
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		if(maxId > Long.MIN_VALUE){
			params.put("max_id",maxId);
		}
		if(sinceId > Long.MIN_VALUE){
			params.put("since_id",sinceId);
		}
		params.put("count",count);
		client.get(apiUrl,params,handler);
	}

	public void composeTweets(AsyncHttpResponseHandler handler,String status){
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status",status);
		Log.d("status", "composeTweets: "+status);
		client.post(apiUrl,params,handler);
	}

	public void composeTweetsReply(AsyncHttpResponseHandler handler,String status, String id){
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status",status);
		params.put("in_reply_to_status_id",id);
		Log.d("status", "composeTweets: "+status);
		client.post(apiUrl,params,handler);
	}

	public void userProfile(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl,null,handler);
	}

	public void usersProfile(AsyncHttpResponseHandler handler,String screenName){
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name",screenName);
		client.get(apiUrl,params,handler);
	}

	public void userTimeline(AsyncHttpResponseHandler handler,String screenName,long sinceId, int count,long maxId){
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		if(maxId > Long.MIN_VALUE){
			params.put("max_id",maxId);
		}
		if(sinceId > Long.MIN_VALUE){
			params.put("since_id",sinceId);
		}
		params.put("screen_name",screenName);
		params.put("count",count);
		client.get(apiUrl,params,handler);
	}

	public void getMentionTimeline(AsyncHttpResponseHandler handler,long sinceId, int count,long maxId){
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		if(maxId > Long.MIN_VALUE){
			params.put("max_id",maxId);
		}
		if(sinceId > Long.MIN_VALUE){
			params.put("since_id",sinceId);
		}
		params.put("count",count);
		client.get(apiUrl,params,handler);
	}
    public void makeFavorite(AsyncHttpResponseHandler handler, String id){
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id",id);
        client.post(apiUrl,params,handler);
    }
    public void unFavorite(AsyncHttpResponseHandler handler, String id){
        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id",id);
        client.post(apiUrl,params,handler);
    }

    public void followersList(AsyncHttpResponseHandler handler,String screenName){
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
        params.put("screen_name",screenName);
        params.put("count",200);
        client.get(apiUrl,params,handler);
    }

    public void friendsList(AsyncHttpResponseHandler handler,String screenName){
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        params.put("screen_name",screenName);
        params.put("count",200);
        client.get(apiUrl,params,handler);
    }
}