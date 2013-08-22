package se.smartkalender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// here on boot stage we are schedule alarms for events
public class BootReceiver extends BroadcastReceiver  {
	@Override
	public void onReceive(Context context, Intent intent) {		
		try{			
			globals.init(context);
			EventsManager.setAlarmForFirstEvent(context);
		}
		catch(Exception ex) {	
			globals.addRecordToLog(ex.getMessage());
		}
	}

}