package stephane.castrec.Twoice;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

/**
 * TwoiceService:
 * 		- ask for new tweets
 * 		- TTS tweets
 * 		- Send event to update ui
 * @author Stephane
 *
 */
public class TwoiceSendTweetService extends TwoiceService {


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startSend(intent.getExtras().getString("message"));

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * display notification icon
	 */
	@SuppressWarnings("static-access")
	protected Notification getNotification(){
		String ns = this.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		//instanciate notif
		int icon = R.drawable.icon;
		CharSequence tickerText = this.getString(R.string.message_notification_send);
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		//define message and Intent
		Context context = getApplicationContext();
		CharSequence contentTitle = this.getString(R.string.app_name);
		CharSequence contentText = this.getString(R.string.message_notification_send);
		Intent notificationIntent = new Intent(this, TwoiceActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		//add notification
		mNotificationManager.notify(2, notification);
		return notification;
	}


	/**
	 * startScan: scanning for new tweets
	 */
	private void startSend(final String message) {
		try {
			//create thread to perform network request
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						twitter.updateStatus(message);
					} catch (TwitterException e) {
						Log.e("twoice", "TwoiceSendTweetService thread exception",e);
					}					
				}
			});
			t.start();
		} catch (Exception e) {
			Log.e("twoice", "TwoiceSendTweetService exception",e);
		}
	}

	@Override 
	public void onDestroy() {
		super.onDestroy();
	} 

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected void customInit() {
		//do nothing		
	}

}
