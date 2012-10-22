package stephane.castrec.Twoice;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import stephane.castrec.Twoice.R;
import stephane.castrec.Twoice.objects.SessionObject;
import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * TwoiceActivity:
 * 		- update ui
 * @author Stephane
 *
 */
public class TwoiceActivity extends Activity implements OnSharedPreferenceChangeListener {
	/**
	 * TWOICE : Notification id
	 */
	private static final int TWOICE = 1;

	/**
	 * default 1min
	 */
	private static int mUpdateTime = 60000;


	private static boolean isTtsActivate = true;

	private Context mContext;
	protected ResponseList<Status> statuses;

	//receiver
	TwoiceServiceReceiver mTwoiceServiceReceiver = null;

	final Handler _Handler = new Handler(){
		public void handleMessage(Message msg) {
			updateResultsInUi();
		}
	};

	/**
	 * Interface to communicate with frament
	 * @author Stephane
	 *
	 */
	public interface TweetListFragmentInterface {
		public void setListContent(List<Status> statuses);
		public void updateListContent(List<Status> statuses);
	}

	/**
	 * TweetListFragmentInterface
	 * 		- interface of the list fragment to communicate
	 */
	private TweetListFragmentInterface mList = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;

		//get fragment
		mList = (TweetListFragmentInterface) getFragmentManager().findFragmentById(R.id.listFragment);

		mUpdateTime = getUpdatesIntervalPrefs();
	}

	@Override
	public void onStart(){
		//Start service
		startTimelineService(mUpdateTime);
		super.onStart();
	}

	@Override
	public void onStop(){
		try {
			//Stop the service
			Intent intent = new Intent(this,TwoiceTimelineService.class); 
			stopService(intent);
		} catch (Exception e) {
			Log.e("twoice","Activity: Fail to stop service");
		}
		super.onStop();
	}

	@Override
	public void onResume(){
		//register listener for service update
		IntentFilter updateEvent;
		updateEvent = new IntentFilter(TwoiceTimelineService.TWEETS_UPDATE);
		if(mTwoiceServiceReceiver == null){
			mTwoiceServiceReceiver = new TwoiceServiceReceiver();
		}
		registerReceiver(mTwoiceServiceReceiver, updateEvent);
		//if tweets stored
		if(SessionObject.getStatuses() != null && !SessionObject.getStatuses().isEmpty()){
			updateResultsInUi();
		}
		getSharedPreferences(SessionObject.PREFS_NAME, 0).registerOnSharedPreferenceChangeListener(this);

		super.onResume();
	}

	@Override
	public void onPause(){
		//unregister listener for service update
		unregisterReceiver(mTwoiceServiceReceiver);

		getSharedPreferences(SessionObject.PREFS_NAME, 0).unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(this.getString(R.string.prefs_update_interval))) {
			mUpdateTime = sharedPreferences.getInt("updates_interval", 60000);
			stopTimelineService();
			startTimelineService(mUpdateTime);
		}
	}

	/**
	 * onCreateOptionsMenu: create menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * onOptionsItemSelected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_create_tweet:
			Log.d("twoice", "onOptionsItemSelected item clicked");
			createTweet();
			return true;
		case R.id.menu_start_service:
			//enable/disable TTS
			Message msg = new Message();
			msg.setTarget(TwoiceTimelineService.serviceTimelineHandler);//set the target
			msg.what = TwoiceTimelineService.OPTION_ENABLE_SOUND;
			msg.sendToTarget();
			//change item
			if(isTtsActivate){
				item.setTitle(this.getString(R.string.menu_tts_disable));
				item.setIcon(R.drawable.muted);
			} else {
				item.setTitle(this.getString(R.string.menu_tts_enable));
				item.setIcon(R.drawable.sound);
			}
			isTtsActivate = !isTtsActivate;
			return true;
		case R.id.menu_params:
			Intent settingsIntent = new Intent(this, TwoiceParametersActivity.class);
			startActivity(settingsIntent);
			return true;
		case R.id.menu_rate_app:
			//Rate app
			String appPackageName = this.getApplication().getPackageName().toString();
			Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appPackageName));
			marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			startActivity(marketIntent);
			return true;
		case R.id.menu_contact:
			/* Fill it with Data */
			Intent i = new Intent();
			i.setType("plain/text");
			i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"stephane.castrec@gmail.com"});
			i.putExtra(android.content.Intent.EXTRA_SUBJECT, this.getString(R.string.app_name));
			i.putExtra(android.content.Intent.EXTRA_TEXT, "");
			/* Send it off to the Activity-Chooser */
			this.startActivity(Intent.createChooser(i, "Send mail..."));
			return true;
		case R.id.menu_quit:
			stopTimelineService();
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}	

	/**
	 * getUpdatesIntervalPrefs
	 * @param int
	 */
	private int getUpdatesIntervalPrefs(){
		SharedPreferences settings = getSharedPreferences(SessionObject.PREFS_NAME, 0);//get settings
		return settings.getInt(this.getString(R.string.prefs_update_interval), 60000);//default 60s		
	}
	
	/**
	 * start polling on service
	 */
	private void startTimelineService(int updateInterval){
		//display notification
		setNotification();		
		//start polling
		Intent myAlarm = new Intent(getApplicationContext(),TimerPollingReceiver.class);
		//myAlarm.putExtra("project_id",project_id); //Put Extra if needed
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(getApplicationContext(),0,myAlarm,PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		Calendar updateTime = Calendar.getInstance();
		//updateTime.setWhatever(0);    //set time to start first occurence of alarm 
		alarms.setRepeating(AlarmManager.RTC_WAKEUP,updateTime.getTimeInMillis(),mUpdateTime,recurringAlarm); //you can modify the interval of course
	}

	/**
	 * Stop polling on service
	 */
	private void stopTimelineService() {
		//rm notification
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(TWOICE);
		//Stop polling
		Intent myAlarm = new Intent(getApplicationContext(),TimerPollingReceiver.class);
		//myAlarm.putExtra("project_id",project_id); //put the SAME extras
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(getApplicationContext(),0,myAlarm,PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(recurringAlarm);
	}

	/**
	 * createTweet: display popup to enter new tweet
	 */
	private void createTweet(){
		try {
			Log.d("twoice","TwoiceActivity create Tweet");
			//enable speak to tweet
			final Dialog dialog = new Dialog(this);

			dialog.setContentView(R.layout.create_tweet);
			dialog.setTitle(this.getString(R.string.create_tweet_title));

			final EditText text = (EditText) dialog.findViewById(R.id.create_tweet_input);
			Button send = (Button) dialog.findViewById(R.id.create_tweet_send);
			send.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// create service to post tweet
					Intent intent = new Intent(mContext,TwoiceSendTweetService.class);
					intent.putExtra("message", text.getText().toString());					
					startService(intent);
					dialog.dismiss();
				}
			});
			dialog.show();
		} catch (Exception e) {
			Log.e("twoice", "createTweet");
		}
	}


	/**
	 * updateResultsInUi : display tweets
	 */
	private void updateResultsInUi(){
		try {
			Log.d("twoice","TwoiceActivity updateResultsInUi");
			mList.setListContent(SessionObject.getStatuses());
		} catch (Exception e) {
			Log.e("twoice", "updateResultsInUi exception",e);
		}
	}

	/**
	 * Configure and display notification icon
	 */
	protected void setNotification(){
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
		//instanciate notif
		int icon = R.drawable.icon;
		CharSequence tickerText = this.getString(R.string.app_name);
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		//define message and Intent
		CharSequence contentTitle = this.getString(R.string.app_name);
		CharSequence contentText = this.getString(R.string.message_notification);
		Intent notificationIntent = new Intent(this, TwoiceActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		mNotificationManager.notify(TWOICE, notification);
	}

	/**
	 * TwoiceServiceReceiver : listen event from service
	 * @author Stephane
	 *
	 */
	public class TwoiceServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent){//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
			// update GUI
			Log.d("twoice", "UPDATE tweets");
			Message msg = new Message();
			msg.setTarget(_Handler);//set the target
			msg.sendToTarget();
		}
	}


}