package se.smartkalender.types;

import java.util.Date;


public class EventAlarmTime {
	public EventAlarmTime(SmartCalendarEvent event, Long nextTimeForEvent) {
		this.nextTimeForEvent = nextTimeForEvent;
		this.event = event;
	}
	public Long nextTimeForEvent;
	public SmartCalendarEvent event;
	public Date getDate() {
		Date res = new Date();
		res.setTime(nextTimeForEvent);
		return res;
	}
}
