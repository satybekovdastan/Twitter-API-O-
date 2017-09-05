package das.twettero;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
  * Это объект, ответственный за связь с REST API.
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "Idjml0d7SL2u01bDGf7qDIZTz";
	public static final String REST_CONSUMER_SECRET = "Hp9UGFoIaLouK0LxXfJNuU8IfWqpAIadgIH4ZsAhzTS3cyIosl";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets";

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

	public void composeTweetsReply(AsyncHttpResponseHandler handler, String status, String id){
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status",status);
		params.put("in_reply_to_status_id",id);
		Log.d("status", "composeTweets: "+status);
		client.post(apiUrl,params,handler);
	}

	public void userProfile(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		RequestParams params = new RequestParams();
		client.get(apiUrl,params,handler);
	}
}