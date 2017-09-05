package das.twettero.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import das.twettero.R;
import das.twettero.TwitterApplication;
import das.twettero.TwitterClient;
import das.twettero.activities.MainActivity;
import das.twettero.models.Tweet;

public class TweetFragment extends DialogFragment {

    SharedPreferences preferences;
    @BindView(R.id.tvBody)
    EditText tvBody;
    @BindView(R.id.tvCount)
    TextView tvCount;
    @BindView(R.id.tvUserName)TextView tvUserName;
    @BindView(R.id.tvScreenName)
    TextView tvScreenName;
    @BindView(R.id.ivProfile)ImageView ivProfile;
    @BindView(R.id.btnTweet)
    Button btnTweet;
    @BindView(R.id.ivClose)ImageView ivClose;
    @BindView(R.id.replyName)
    TextView replyName;
    String status = "";
    TwitterClient twitterClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_compose_tweet, container);
    }

    Tweet tweet;
    Boolean isReply = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        Bundle args = getArguments();

        if(args.getBoolean("isReply")) {
            tweet = ((MainActivity)getActivity()).replyTweet;
            tvBody.setText("@"+tweet.getUser().getScreenName()+" ");
            tvBody.setSelection(tvBody.length());
            replyName.setVisibility(View.VISIBLE);
            replyName.setText("In Reply To: "+tweet.getUser().getName());
            isReply = true;
        } else {
            replyName.setVisibility(View.INVISIBLE);
        }

        twitterClient = TwitterApplication.getRestClient();
        preferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String name = preferences.getString(MainActivity.KEY_NAME, null);
        if(name == null)
            tvUserName.setVisibility(View.INVISIBLE);
        else
            tvUserName.setText(name);

        name = preferences.getString(MainActivity.KEY_SCREEN_NAME, null);
        if(name == null)
            tvScreenName.setVisibility(View.INVISIBLE);
        else
            tvScreenName.setText('@'+name);

        name = preferences.getString(MainActivity.KEY_PROFILE_IMAGE, null);
        if(name != null){
            Glide.with(this).load(name).into(ivProfile);
        }

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postTweet();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetFragment.this.dismiss();
            }
        });
        tvBody.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.toString().length() > 140){
                    tvCount.setTextColor(ContextCompat.getColor(getContext(),android.R.color.holo_red_dark));
                } else{
                    tvCount.setTextColor(ContextCompat.getColor(getContext(),android.R.color.holo_green_dark));
                }
                tvCount.setText(140 - s.toString().length() + "/140");
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    public void postTweet(){
        status = tvBody.getText().toString();
        if(status.length() > 140){
            Toast.makeText(getActivity(), "Character limit exceeded , only 140 allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isReply) {
            twitterClient.composeTweetsReply(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                    if(getActivity() instanceof OnTweetPostListener){
                        ((OnTweetPostListener) getActivity()).onTweetPost();
                    }
                    TweetFragment.this.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    TweetFragment.this.dismiss();
                }
            },status, tweet.getUid()+"");
        } else {
            twitterClient.composeTweets(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                    if(getActivity() instanceof OnTweetPostListener){
                        ((OnTweetPostListener) getActivity()).onTweetPost();
                    }
                    TweetFragment.this.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    TweetFragment.this.dismiss();
                }
            },status);
        }
    }

    public interface OnTweetPostListener{
        void onTweetPost();
    }
}
