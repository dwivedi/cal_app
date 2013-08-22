package se.smartkalender;

import java.util.ArrayList;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import se.smartkalender.types.SmartCalendarEvent;


import android.content.Context;

public class EventHandler extends DefaultHandler {

	ArrayList<SmartCalendarEvent> events = new ArrayList<SmartCalendarEvent>(0);
	//private Context context;
	
	public EventHandler(Context context) {
		//this.context = context;
	}

	public ArrayList<SmartCalendarEvent> getEvents() {
		return events;
	} 
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    	if (localName.equals("event")) {
    		SmartCalendarEvent event = new SmartCalendarEvent(0, attributes.getValue("name"), 
    				attributes.getValue("icon"),
    				(Long.decode(attributes.getValue("color"))).intValue(),
    				new Date());
    		event.setDescription(attributes.getValue("description"));
    		// fill start and finish time
    		String value = attributes.getValue("start");
    		if (value != null) {
	    		String time[] = value.split(":");
	    		if (time.length == 2) {
	    			int hour = Integer.parseInt(time[0]);
	    			int minutes = Integer.parseInt(time[1]);
	    			Date t = new Date();
	    			t.setHours(hour);
	    			t.setMinutes(minutes);
	    			event.setStartTime(t);
	    		}
    		}
    		
    		value = attributes.getValue("end");
    		if (value != null) {    		
    			String[] time = value.split(":");
	    		if (time.length == 2) {
	    			int hour = Integer.parseInt(time[0]);
	    			int minutes = Integer.parseInt(time[1]);
	    			Date t = new Date();
	    			t.setHours(hour);
	    			t.setMinutes(minutes);
	    			event.setFinishTime(t);
	    		} 
    		}
    		value = attributes.getValue("reminder");
    		if (value != null) {
    			event.setReminder(Integer.parseInt(value));
    		}
    		value = attributes.getValue("repeat");
    		if (value != null) {
    			event.setRepeatDays(value);
    		}
    		value = attributes.getValue("category");
    		if (value != null) {
    			event.setCategory(value);
    	    	globals.addRecordToLog("category was "+value+" in xml-file, and stored as "+event.getCategory());
    		}
    		else {
    	    	globals.addRecordToLog("category was null in xml-file");
    		}
    		//repeat="Mon,Tue"
    		
    		
    		events.add(event);    		
    	}
    }

}
