package stephane.castrec.Twoice;

import android.os.Bundle;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class TwoiceParametersActivity extends PreferenceActivity implements OnPreferenceChangeListener{
	
	private static final String PREF_TWOICE = "twoice";
	
	private ListPreference volumeList = null;


	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		PreferenceScreen prefs = getPreferenceScreen();
		volumeList = (ListPreference) prefs.findPreference(PREF_TWOICE); 
		volumeList.setOnPreferenceChangeListener(this);  
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Message msg = new Message();
		msg.setTarget(TwoiceTimelineService.serviceTimelineHandler);//set the target
		msg.what = TwoiceTimelineService.OPTION_UPDATE_VOLUME;
		msg.sendToTarget();
		return false;
	}
}
