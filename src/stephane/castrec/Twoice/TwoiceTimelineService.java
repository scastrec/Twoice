package stephane.castrec.Twoice;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.knallgrau.utils.textcat.TextCategorizer;

import stephane.castrec.Twoice.db.LastTweetsBDD;
import stephane.castrec.Twoice.objects.SessionObject;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.URLEntity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;

/**
 * TwoiceService:
 * 		- rerieve new tweets from twitter API
 * 		- TTS tweets
 * 		- Send event to update ui
 * @implements 
 * 		onInitListener 
 * 		OnUtteranceCompletedListener
 * @author Stephane
 *
 */
public class TwoiceTimelineService extends TwoiceService implements OnInitListener{

	/**
	 * TWEETS_UPDATE : Communication with activity
	 */
	static public String TWEETS_UPDATE = "stephane.castrec.twoice.UPDATE_TWEETS";
	/**
	 * OPTION_TIME_TO_UPDATE : What receive if update time in second receive from parameters
	 */
	static public final int OPTION_TIME_TO_UPDATE = 100;
	/**
	 * OPTION_TIME_TO_UPDATE : What receive if update time in second receive from parameters
	 */
	static public final int OPTION_ENABLE_SOUND = 110;
	/**
	 * OPTION_TIME_TO_UPDATE : What receive if update time in second receive from parameters
	 */
	static public final int OPTION_UPDATE_VOLUME = 120;
	/**
	 * NB_TWEETS_TO_SAVE
	 */
	static private final int NB_TWEETS_TO_SAVE = 10;
	/**
	 * Silence in ms between two tweets
	 */
	static private final int SILENCE_TIME= 1000;
	
	private boolean ttsInitDone = false;

	//checking lanuage from string
	private TextCategorizer textCategorizer = null;

	private static TextToSpeech mTts;
	private static boolean isSpeak = true;
	private static boolean isDefaultLocaleAvailable = false;
	private static boolean isUSAvailable = false;
	private static boolean isUKAvailable = false;
	private static boolean isFrenchAvailable = false;

	/**
	 * Handler to update options
	 */
	public static final Handler serviceTimelineHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OPTION_ENABLE_SOUND:
				Log.d("twoice","TwoiceTimelineService isSpeak="+isSpeak);
				isSpeak = !isSpeak;
				//stop speaking
				mTts.stop();
				break;
			case OPTION_UPDATE_VOLUME:
				Log.d("twoice","TwoiceTimelineService OPTION_UPDATE_VOLUME");
				//setVolumeFromPrefs();
				break;
			default:
				break;
			}
		}
	};


	/**
	 * send update event to activity
	 * 		each time new tweets coming, send event
	 */
	private void sendDataToActivity(){
		Log.d("twoice", "Service sendDataToActivity TWEETS_Update");
		Intent intent = new Intent(TWEETS_UPDATE);
		sendBroadcast(intent);
	}

	/**
	 * perform personnal init depending on the service
	 * 		- init TTS
	 * 		- set audioMgr
	 */
	@SuppressWarnings("static-access")
	protected void customInit(){
		try {			
			//create TTS
			if(mTts == null){
				mTts = new TextToSpeech(this, this);
			}
			
			testLanguageForTTS();

			setVolumeFromPrefs();
		} catch (Exception e) {
			Log.e("twoice", "custom init exception ",e);
		}
	}
	
	/**
	 * set TTS volume 
	 */
	private void setVolumeFromPrefs(){
		SharedPreferences settings = getSharedPreferences(SessionObject.PREFS_NAME, 0);//get settings
		int volumeStep = settings.getInt(this.getString(R.string.prefs_volume), 5);//default max volume step		
		//set volume
		AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		int amStreamMusiclowStepVol = am.getStreamMaxVolume(am.STREAM_MUSIC)/5;//divided into 5 step
		am.setStreamVolume(am.STREAM_MUSIC, amStreamMusiclowStepVol*volumeStep, 0);
	}
	
	
	/**
	 * test what local we can speak
	 */
	private void testLanguageForTTS(){
		if(mTts.isLanguageAvailable(Locale.getDefault()) == mTts.SUCCESS){
			isDefaultLocaleAvailable = true;
		} 
		if(mTts.isLanguageAvailable(Locale.US) == mTts.SUCCESS){
			isUSAvailable = true;
		} else if (mTts.isLanguageAvailable(Locale.UK) == mTts.SUCCESS){
			isUKAvailable = true;
		}
		if(mTts.isLanguageAvailable(Locale.FRENCH) == mTts.SUCCESS){
			isFrenchAvailable = true;
		}
	}

	/**
	 * execute task
	 */
	private void execute() {
		textCategorizer = new TextCategorizer();
		/*
		 * run task in a different thread than UI
		 */
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
				int nbNewTweets = retrieveStatusFromTwitter();

				//send flag to activity
				//check if tts enable and tweets to say
				if(ttsInitDone && SessionObject.getStatuses() != null){
					sendDataToActivity();
				}

				speak(nbNewTweets);				
			}
		});
		t.start();
	}

	/**
	 * createTextToSpeak: adapt tweet to speak it
	 * @param toSpeak
	 * @param u
	 */
	private String createTextToSpeak(String toSpeak, URLEntity[] u){
		try {			
			//rm urls
			String stringAfterTreatment = "";
			for(int i=0; i<u.length; i++){
				stringAfterTreatment = toSpeak.replace(u[i].getURL().toString(), "LINK");
				//stringAfterTreatment = toSpeak.substring(u[i].getStart(), u[i].getEnd())+" ";
			}
			//Understand RT
			stringAfterTreatment = stringAfterTreatment.replace("RT ", "Retweet ");
			//RM #
			stringAfterTreatment = stringAfterTreatment.replaceAll("#", "");
			//RM @
			stringAfterTreatment.replaceAll("@", "");
			//RM $
			stringAfterTreatment = stringAfterTreatment.replaceAll("$", "");
			return stringAfterTreatment;
		} catch (Exception e) {
			Log.e("twoice","createTextToSpeak exception : "+e);
		}
		return toSpeak;
	}

	/**
	 * setTTSLanguage: set language depending on previous detection
	 * Set French or default(US)
	 * @param toSpeak
	 */
	private void setTTSLanguage(String toSpeak){
		String lang = textCategorizer.categorize(toSpeak);
		//Map<String,Integer> dist = textCategorizer.getCategoryDistances(toSpeak);

		if(lang.equalsIgnoreCase("english") && isUSAvailable){
				mTts.setLanguage(Locale.US);	
		} else if(lang.equalsIgnoreCase("english") && isUKAvailable){
				mTts.setLanguage(Locale.UK);	
		} else if(lang.equalsIgnoreCase("french") && isFrenchAvailable){
			mTts.setLanguage(Locale.FRENCH);
		} else {
			//default use default 
			if(isDefaultLocaleAvailable){
				mTts.setLanguage(Locale.getDefault());
			}else {
				//TODO set error if local.us not available by default
				mTts.setLanguage(Locale.US);
			}
		}
	}	
	
	/**
	 * setTTSEnablePrefs
	 * @param ttsOk
	 */
	private int getUpdatesIntervalPrefs(){
		SharedPreferences settings = getSharedPreferences(SessionObject.PREFS_NAME, 0);//get settings
		return settings.getInt("updates_interval", 60000);//default 60s		
	}


	@Override 
	public void onDestroy() {
		//stop tts
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}

		//save 10 last tweets in DB
		saveLastTweets();
		super.onDestroy();
	} 

	/**
	 * saveLastTweets: 
	 * 		save 10 last tweets to improve next update and get all tweets from we close this app
	 */
	private void saveLastTweets(){
		try {
			if(SessionObject.getStatuses() != null && !SessionObject.getStatuses().isEmpty()){
				List<Status> statusToSave = SessionObject.getStatuses().subList(0, NB_TWEETS_TO_SAVE);
				LastTweetsBDD db = new LastTweetsBDD(this);
				db.saveStatus(statusToSave);
			}
		} catch (Exception e) {
			Log.e("twoice", "TwoiceService");
		}		
	}

	/**
	 * retrieveStatusFromTwitter
	 * @return nb tweet to speak
	 */
	private int retrieveStatusFromTwitter(){
		try {
			ResponseList<Status> status = null;
			//check if first tweet
			if(null == SessionObject.getStatuses() || SessionObject.getStatuses().isEmpty()){
				status = twitter.getHomeTimeline();
				SessionObject.setStatuses(status);
			} else {
				//check for updates
				status = twitter.getHomeTimeline(new Paging(0, SessionObject.getStatuses().get(0).getId()));
				SessionObject.addStatuses(status);
			}
			if(status != null)
				return status.size();
		} catch (Exception e) {
			Log.e("twoice", "TwoiceTimelineService error on access Twitter");
		}
		return 0;
	}

	/**
	 * speak: 
	 * @param nbNewTweets to add to TTS
	 */
	private void speak(int nbNewTweets){
		HashMap map = new HashMap<String,String>();
		//if TTS wanted
		if(isSpeak){
			//say each status
			//for (twitter4j.Status SessionObject.getStatuses().get(i) : SessionObject.getStatuses()) {
			for(int i = 0; i<nbNewTweets; i++){
				//create speaking thing
				String tweet = SessionObject.getStatuses().get(i).getUser().getName()+": "+SessionObject.getStatuses().get(i).getText();//.replaceAll(URL_PATTERN.pattern(), "link");
				//Filter tweeter language (#,RT,...) and URL
				String toSpeak = createTextToSpeak(tweet, SessionObject.getStatuses().get(i).getURLEntities());
				//detect language
				setTTSLanguage(toSpeak);
				//add to queue (speak)
				mTts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
				//add silence after each tweet
				mTts.playSilence(SILENCE_TIME, TextToSpeech.QUEUE_ADD, null);
				//log
				Log.d("twoice", "Service spoke \n "+ toSpeak);
			}
		}
	}


	/*
	 * @see android.speech.tts.TextToSpeech.OnInitListener#onInit(int)
	 */
	@Override
	public void onInit(int status) {
		if(status == android.speech.tts.TextToSpeech.SUCCESS) {
			ttsInitDone = true;
			execute();
		}
		else {
			ttsInitDone = false;
		}			
	}

	/*
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onLowMemory() {
		System.out.println("I am on low memory :(");
	}
}
