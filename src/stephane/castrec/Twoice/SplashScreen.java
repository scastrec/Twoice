package stephane.castrec.Twoice;

import stephane.castrec.Twoice.db.LastTweetsBDD;
import stephane.castrec.Twoice.objects.SessionObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class SplashScreen extends Activity {
	
	final private int MY_DATA_CHECK_CODE = 0x008;

	
	private Intent i;

	/*private Handler splashHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {	
			case STOPSPLASH:	
				//remove SplashScreen from view
				Log.d("twoice", "SplashScreen");
				startActivity(i);
				break;
			}
			super.handleMessage(msg);
		}
	};*/
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("twoice", "SplashScreen onCreate Start");
        setContentView(R.layout.splash);
        
        //do action in other thread
        Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
		        tryTTSService();        				
			}
		});
        t.start();
    }
    

	/**
	 * setTTSEnablePrefs
	 * @param ttsOk
	 */
	private void setTTSEnablePrefs(boolean ttsOk){
		SharedPreferences settings = getSharedPreferences(SessionObject.PREFS_NAME, 0);//get settings
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean("tts_enabled", ttsOk);//get if tts ok: default yes
		editor.commit();
	}
    

	/**
	 * tryTTSService: try to start TTS to check if available
	 */
	protected void tryTTSService(){
		try {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		} catch (Exception e) {
			Log.e("twoice", "TwoiceActivity onLoadingFinished",e);			
		}
	}

	/**
	 * onActivityResult
	 * 		Define if TTS is available
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				Log.d("twoice","TwoiceActivity TTS OK");
				setTTSEnablePrefs(true);
				
				//load tweets from  db
		        loadSavedTweets();

		        i = new Intent (this, TwoiceActivity.class);
		        startActivity(i);
		        this.finish();
				
			} else {
				Log.d("twoice","TwoiceActivity TTS NOK: has to install more package ");
				setTTSEnablePrefs(false);
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(
						TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}
	
	private void loadSavedTweets(){
		try {
			LastTweetsBDD db = new LastTweetsBDD(this);
			SessionObject.addStatuses(db.getAllSavedStatus());
		} catch (Exception e) {
			Log.e("twoice", "loadSavedTweets exception ",e);
		}
	}
}

