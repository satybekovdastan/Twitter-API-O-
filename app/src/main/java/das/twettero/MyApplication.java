package das.twettero;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


public class MyApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.context = this;
	}

	public static TwitterClient getTwitterClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, MyApplication.context);
	}

	public static SharedPreferences getPrefs() {
		return context.getSharedPreferences("main", MODE_PRIVATE);
	}

	public static Context getContext() {
		return context;
	}
}