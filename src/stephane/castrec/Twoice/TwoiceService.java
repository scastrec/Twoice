package stephane.castrec.Twoice;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Service;

public abstract class TwoiceService extends Service {
	
	static protected Twitter twitter;

	
	@Override
	public void onCreate() { 
		super.onCreate(); 
		configTwitter();
		customInit();
	}
	
	protected abstract void customInit();
	
	/**
	 * configTwitter
	 */
	static private void configTwitter(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("g2ZvFgHRJTCtnb5VHcA")
		.setOAuthConsumerSecret("aqexfuTxVtJZ6fYpyzP0gnjIxoVt9ZFE8Xam8Bjq0Ss")
		.setOAuthAccessToken("24149509-u84F6755kmRep8gbAxaTf1FvB43FtsGPSn9PuFTuS")
		.setOAuthAccessTokenSecret("yKKxJ8X3vseVPvOu2gdXcy0JCm5GilNOD1o1oADnpk");
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}
}
