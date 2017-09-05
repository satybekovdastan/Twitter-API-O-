package das.twettero.adapter;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import das.twettero.R;
import das.twettero.activities.MainActivity;
import das.twettero.models.Tweet;
import das.twettero.utils.Utils;


public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    MainActivity timelineActivity;
    public TweetsArrayAdapter(MainActivity activity, List<Tweet> tweets) {
        super(activity,0, tweets);
        timelineActivity = activity;
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
                timelineActivity.showEditDialog(tweet);
            }
        });
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);
        return convertView;
    }



    static class ViewHolder{
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvUser)
        TextView tvUser;
        @BindView(R.id.tvTweetBody)
        TextView tvTweetBody;
        @BindView(R.id.tvCreatedAt)
        TextView tvCreatedAt;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvFavorite)
        TextView tvFav;
        @BindView(R.id.tvRetweet)
        TextView tvRetweet;
        @BindView(R.id.ivFavorites)
        ImageView ivFav;
        @BindView(R.id.ivRetweet)
        ImageView ivretweet;
        @BindView(R.id.ivTweetData)
        ImageView ivTweetData;
        @BindView(R.id.ivReply)
        ImageView ivReply;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
