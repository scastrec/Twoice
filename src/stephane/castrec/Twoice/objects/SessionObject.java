package stephane.castrec.Twoice.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;

import twitter4j.Status;

public class SessionObject {
	
	public static final String PREFS_NAME = "twoice";


	static private List<Status> statuses;
	static private Map<Long, Bitmap> avatars; 

	static public void setStatuses(List<Status> pstatuses) {
		statuses = pstatuses;
	}

	static public List<Status> getStatuses() {
		return statuses;
	}

	static public void addStatuses(List<Status> pstatuses) {
		if(statuses == null){
			statuses = pstatuses;
		} else {
			statuses.addAll(0, pstatuses);
		}
	}

	public static void addAvatars(long key, Bitmap bmp) {
		if(avatars == null){
			avatars = new HashMap<Long, Bitmap>();
		}
		SessionObject.avatars.put(key, bmp);
	}

	public static Bitmap getAvatars(long key) {
		if(avatars == null){
			avatars = new HashMap<Long, Bitmap>();
		}
		return avatars.get(key);
	}


}
