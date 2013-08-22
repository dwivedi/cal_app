package se.smartkalender;

import se.smartkalender.dialogs.EventNotificationDialog;
import se.smartkalender.types.SmartCalendarEvent;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EventNotification extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent _intent) { // TODO : in intent should be info about event
		
		globals.addRecordToLog("received alarm");	
		globals.init(context);
		SmartCalendarEvent event = new SmartCalendarEvent(EventsManager.getEvent(_intent.getExtras().getInt("eventId")));
		globals.addRecordToLog("received alarm for event - "  +  event.getName());
        Intent intent = new Intent(context, EventNotificationDialog.class);
        intent.putExtra("eventId", event.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

		// and again schedule alarms
		EventsManager.setAlarmForFirstEvent(context);		
	}
}