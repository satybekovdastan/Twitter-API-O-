package das.twettero.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import java.util.ArrayList;

@Parcel
public class Tweet {

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    String body;
    long uid; //unique id for tweet
    String createdAt;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    User user;

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    int favoriteCount;

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    boolean favorited,retweeted;
    int retweetCount;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String mediaUrl;
    String type;

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    String idStr;
    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.idStr = jsonObject.getString("id_str");
            if(jsonObject.has("extended_entities")){
               JSONObject ext = jsonObject.getJSONObject("extended_entities");
                if(ext.has("media")){
                    JSONArray mediaArr = ext.getJSONArray("media");
                    if(mediaArr.length() > 0){
                        tweet.mediaUrl = ((JSONObject)mediaArr.get(0)).getString("media_url");
                        tweet.type = ((JSONObject)mediaArr.get(0)).getString("type");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray json){
        ArrayList<Tweet> tweet = new ArrayList<>();
        for(int i =0 ; i < json.length() ; i++){
            try {
                JSONObject tweetJson =  json.getJSONObject(i);
                Tweet tweets = Tweet.fromJSON(tweetJson);
                if(tweets != null){
                    tweet.add(tweets);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweet;
    }
}
