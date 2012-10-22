package stephane.castrec.Twoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimerPollingReceiver extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context,Intent intent) 
    {
        Intent myService = new Intent(context,TwoiceTimelineService.class);
        context.startService(myService);
    }
}
