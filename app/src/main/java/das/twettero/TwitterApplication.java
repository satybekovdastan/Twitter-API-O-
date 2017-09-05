package das.twettero;

import android.content.Context;

/*
  * Это приложение Android непосредственно и используется для настройки различных настроек
  * включая кэш изображения в памяти и на диске. Это также добавляет синглтон
  * для доступа к соответствующему клиенту отдыха.
  *
  * Клиент RestClient = RestApplication.getRestClient ();
  * // использовать клиент для отправки запросов API
  *
 */
public class TwitterApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		TwitterApplication.context = this;
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
	}
}