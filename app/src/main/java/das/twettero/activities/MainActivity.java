package das.twettero.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import das.twettero.MyApplication;
import das.twettero.R;
import das.twettero.TwitterApplication;
import das.twettero.TwitterClient;
import das.twettero.adapter.EndlessScrollListener;
import das.twettero.adapter.TweetsArrayAdapter;
import das.twettero.fragment.TweetFragment;
import das.twettero.models.Tweet;
import das.twettero.models.User;
import das.twettero.utils.Utils;

public class MainActivity extends AppCompatActivity implements TweetFragment.OnTweetPostListener{

    public static final String KEY_NAME = "name";
    public static final String KEY_SCREEN_NAME = "screenName";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    SharedPreferences preferences;
    Activity mActivity;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.lvTweets)
    ListView lvTweets;
    @BindView(R.id.myFAB)
    RelativeLayout myFAB;
    @BindView(R.id.btn_update)
    Button btn_update;
    private TwitterClient twitterClient;
    ArrayList<Tweet> tweets;
    ArrayList<User> users;
    TweetsArrayAdapter tweetAdapter;
    long sinceId = Long.MIN_VALUE;
    int count = 25;
    boolean loading = false;
    long maxId = Long.MIN_VALUE;
    public static final String MyPREFERENCES = "tweeter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mActivity = this;
        twitterClient = TwitterApplication.getRestClient();
        ActionBar action = getSupportActionBar();
        action.setIcon(R.mipmap.ic_launcher);
        action.setDisplayShowHomeEnabled(true);
        action.setDisplayShowTitleEnabled(false);
        preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        populateTimeline();
        tweets = new ArrayList<>();
        users = new ArrayList<>();
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
                showEditDialog();
            }
        });
        userProfile();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tweetAdapter.clear();
                tweetAdapter.notifyDataSetChanged();
                populateTimeline();
                btn_update.setVisibility(View.GONE);
                Toast.makeText(mActivity, "Обновления", Toast.LENGTH_SHORT).show();
            }
        });

        timer();
    }

    private void timer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("TIMER", "");
                timer();
                btn_update.setVisibility(View.VISIBLE);
            }
        }, 60 * 1000);
    }

    public void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        TweetFragment composeDialogFragment = new TweetFragment();
        Bundle args = new Bundle();
        args.putBoolean("isReply",false);

        composeDialogFragment.setArguments(args);
        composeDialogFragment.show(fm, "fragment_compose_tweet");

    }

    public Tweet replyTweet;
    public void showEditDialog(Tweet tweet) {
        replyTweet = tweet;
        FragmentManager fm = getSupportFragmentManager();
        TweetFragment composeDialogFragment = new TweetFragment();
        Bundle args = new Bundle();
        args.putBoolean("isReply",true);
        composeDialogFragment.setArguments(args);
        composeDialogFragment.show(fm, "fragment_reply_tweet");

    }
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
                    .setMessage("Проверьте интернет!")
                    .setCancelable(true)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           dialog.cancel();
                            if(mActivity != null){
                                mActivity.finish();
                            }
                        }
                    });
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

    @Override
    public void onTweetPost() {
        tweetAdapter.clear();
        tweetAdapter.notifyDataSetChanged();
        populateTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {
            MyApplication.getTwitterClient().clearAccessToken();
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
