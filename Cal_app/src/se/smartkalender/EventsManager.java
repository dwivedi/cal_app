package se.smartkalender;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import se.smartkalender.types.EventAlarmTime;
import se.smartkalender.types.SmartCalendarEvent;

import se.smartkalender.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateFormat;

public class EventsManager {
	private static final int TEMPLATE_EVENTS_MAX_COUNT = 1000;
	static ArrayList<SmartCalendarEvent> events = new ArrayList<SmartCalendarEvent>(
			0);
	static ArrayList<SmartCalendarEvent> templateEvents = null;
	static ArrayList<String> templateCategories = null;
	static String[] projection = { EventsContentProvider.EVENT_ID,
			EventsContentProvider.TITLE, EventsContentProvider.DESCRIPTION,
			EventsContentProvider.ICON_ID, EventsContentProvider.COLOR,
			EventsContentProvider.DATE, EventsContentProvider.START_TIME,
			EventsContentProvider.FINISH_TIME, EventsContentProvider.REMINDER,
			EventsContentProvider.REPEAT_DAYS, };

	public static ArrayList<SmartCalendarEvent> getAllEvents() {
		return events;
	}

	public static ArrayList<SmartCalendarEvent> getAllEvents(Date selectedDay) {
		ArrayList<SmartCalendarEvent> _events = new ArrayList<SmartCalendarEvent>(
				0);
		for (SmartCalendarEvent event : events) {
			if (event.getDate().getDate() == selectedDay.getDate()
					&& event.getDate().getMonth() == selectedDay.getMonth()
					&& event.getDate().getYear() == selectedDay.getYear()) {
				_events.add(event);
			}
		}
		return _events;
	}

	public static ArrayList<SmartCalendarEvent> getEventsForPeriod(
			Date startTime, int periodMinutes) {
		ArrayList<SmartCalendarEvent> _events = new ArrayList<SmartCalendarEvent>(
				0);
		for (SmartCalendarEvent event : events) {
			if (!isEventOnDate(event, startTime))
				continue;
			if (globals.isTimeAfter(event.getStartTime(),
					globals.getTimePlusSeconds(startTime, 0))) {
				if (globals.isTimeBefore(event.getStartTime(), globals
						.getTimePlusSeconds(startTime, periodMinutes * 60))) {
					_events.add(event);
				}
			}
		}
		return _events;
	}

	public static boolean isEventOnDate(SmartCalendarEvent event, Date startTime) {
		if (globals.isEqualDate(event.getDate(), startTime))
			return true;
		if (event.getDate().after(startTime))
			return false;

		String[] repeat_days = event.getRepeatDays().split(",");
		if (repeat_days.length > 0) {
			int dayOfWeek = startTime.getDay();
			for (int i = 0; i < repeat_days.length; i++) {
				if (globals.weekDays[dayOfWeek].contentEquals(repeat_days[i]))
					return true;
			}
		}
		return false;
	}

	public static ArrayList<SmartCalendarEvent> getTemplateEvents() {
		if (templateEvents == null) {
			templateEvents = new ArrayList<SmartCalendarEvent>(0);
		}
		return templateEvents;
	}

	public static ArrayList<String> getTemplateCategories() {
		if (templateCategories == null) {
			templateCategories = new ArrayList<String>(0);
		}
		return templateCategories;
	}

	public static SmartCalendarEvent getEvent(int id) {
		for (SmartCalendarEvent event : events) {
			if (event.getId() == id)
				return event;
		}
		for (SmartCalendarEvent event : templateEvents) {
			if (event.getId() == id)
				return event;
		}
		return null;
	}

	public static boolean addEvent(SmartCalendarEvent e, Activity a) {
		events.add(e);
		addEventToDB(e, a);
		setAlarmForFirstEvent(a); // reschedule alarms
		return true;
	}

	// todo :
	public static int changeEvent(int id, SmartCalendarEvent event, Activity a) {
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).getId() == id) {
				removeEventFromDB(a, events.get(i));
				events.remove(i);// .set(i, event);
				break;
			}
		}
		// if no event with id and id == 0, then it appears that user add new
		// event
		// events.add(new SmartCalendarEvent(event, getUniqueId()));
		int newid = getUniqueId();
		addEvent(new SmartCalendarEvent(event, newid), a);
		return newid;
	}

	private static int getUniqueId() {
		int id = TEMPLATE_EVENTS_MAX_COUNT;
		while (true) {
			boolean continue_search = false;
			id++;
			for (SmartCalendarEvent event : events) {
				if (event.getId() == id) {
					continue_search = true;
					break;
				}
			}
			if (!continue_search)
				return id;
		}
	}

	public static void deleteEvent(Activity a, int id) {
		for (SmartCalendarEvent event : events) {
			if (event.getId() == id) {
				events.remove(event);
				removeEventFromDB(a, event);
				setAlarmForFirstEvent(a); // reschedule alarms
				break;
			}
		}
	}

	public static ArrayList<SmartCalendarEvent> loadEventsFromTemplateFileConfig(
			Context context) {
		InputStream inputStream = context.getResources().openRawResource(
				R.raw.events_templates);
		templateEvents = new ArrayList<SmartCalendarEvent>(0);
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			EventHandler eventHandler = new EventHandler(context);
			parser.parse(inputStream, eventHandler);
			ArrayList<SmartCalendarEvent> events = eventHandler.getEvents();
			int ix = 0;
			for (SmartCalendarEvent templateEvent : events) {
				templateEvents.add(new SmartCalendarEvent(templateEvent, ix++));
			}
			templateCategories = extractTemplateCategories(templateEvents);
			return templateEvents;
		} catch (SAXException e) {
		} catch (IOException e) {
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static ArrayList<String> extractTemplateCategories(
			ArrayList<SmartCalendarEvent> templates) {
		globals.addRecordToLog("start extract template categories");
		ArrayList<String> templateCategories = new ArrayList<String>(0);
		String lastCategory = "";

		for (SmartCalendarEvent template : templates) {
			globals.addRecordToLog("checking " + template.getName() + " ("
					+ template.getCategory() + ")");

			globals.addRecordToLog("compare category " + template.getCategory()
					+ " with " + lastCategory + " results in "
					+ lastCategory.compareToIgnoreCase(template.getCategory()));

			if (lastCategory.compareToIgnoreCase(template.getCategory()) != 0) {
				globals.addRecordToLog("found category "
						+ template.getCategory());
				templateCategories.add(template.getCategory());
				lastCategory = template.getCategory();
			}
		}

		globals.addRecordToLog("finish extract template categories");
		return templateCategories;
	}

	public static int getEventPeriod(SmartCalendarEvent event) {
		return globals.getDifferenceInMinutes(event.getFinishTime(),
				event.getStartTime());
	}

	/* add an event to calendar */
	private static void addEventToDB(SmartCalendarEvent event, Activity a) {

		Uri uri = EventsContentProvider.CONTENT_URI;
		ContentResolver cr = a.getContentResolver();
		ContentValues values = new ContentValues();

		values.put(EventsContentProvider.EVENT_ID, event.getId());
		values.put(EventsContentProvider.TITLE, event.getName());
		values.put(EventsContentProvider.DESCRIPTION, event.getDesciption());

		values.put(EventsContentProvider.ICON_ID, event.getIconId());
		values.put(EventsContentProvider.COLOR, event.getColor());
		values.put(EventsContentProvider.DATE, event.getDate().getTime());
		values.put(EventsContentProvider.START_TIME, event.getStartTime()
				.getTime());
		values.put(EventsContentProvider.FINISH_TIME, event.getFinishTime()
				.getTime());
		values.put(EventsContentProvider.REPEAT_DAYS, event.getRepeatDays());
		values.put(EventsContentProvider.REMINDER, event.getReminder());

		cr.insert(uri, values);

	}

	private static void removeEventFromDB(Activity a, SmartCalendarEvent event) {
		ContentResolver cr = a.getContentResolver();
		cr.delete(EventsContentProvider.CONTENT_URI, "_id=?",
				new String[] { String.valueOf(event.getId()) });
	}

	public static void extractEventsFromDB(Context context) {
		events.clear();
		try {
			Cursor cursor = context.getContentResolver().query(
					EventsContentProvider.CONTENT_URI, projection, null, null,
					null);
			if (cursor != null) {
				cursor.moveToFirst();
				while (true) {
					int id = cursor
							.getInt(cursor
									.getColumnIndexOrThrow(EventsContentProvider.EVENT_ID));
					String iconId = cursor
							.getString(cursor
									.getColumnIndexOrThrow(EventsContentProvider.ICON_ID));
					String desc = cursor
							.getString(cursor
									.getColumnIndexOrThrow(EventsContentProvider.DESCRIPTION));
					String name = cursor
							.getString(cursor
									.getColumnIndexOrThrow(EventsContentProvider.TITLE));
					int color = cursor
							.getInt(cursor
									.getColumnIndexOrThrow(EventsContentProvider.COLOR));
					Date date = new Date();
					date.setTime(cursor.getLong(cursor
							.getColumnIndexOrThrow(EventsContentProvider.DATE)));
					Date startTime = new Date();
					startTime
							.setTime(cursor.getLong(cursor
									.getColumnIndexOrThrow(EventsContentProvider.START_TIME)));
					Date finishTime = new Date();
					finishTime
							.setTime(cursor.getLong(cursor
									.getColumnIndexOrThrow(EventsContentProvider.FINISH_TIME)));
					String repeatDays = cursor
							.getString(cursor
									.getColumnIndexOrThrow(EventsContentProvider.REPEAT_DAYS));
					Integer reminderTime = cursor
							.getInt(cursor
									.getColumnIndexOrThrow(EventsContentProvider.REMINDER));

					SmartCalendarEvent e = new SmartCalendarEvent(id, name,
							iconId, color, date, startTime, finishTime);
					e.setDescription(desc);
					e.setRepeatDays(repeatDays);
					e.setReminder(reminderTime);
					events.add(e);

					if (!cursor.moveToNext())
						break;
				}
				// Always close the cursor
				if (cursor != null)
					cursor.close();
			}
		} catch (Exception ex) {
		}

	}

	public static EventAlarmTime getNextEventAlarmTime() {
		Date timeToReturn = null;
		Date currentTime = new Date();
		SmartCalendarEvent returnEvent = null;
		globals.addRecordToLog("getNextEventAlarmTime - "
				+ Integer.toString(events.size()));
		for (SmartCalendarEvent event : events) {
			Date nextTimeForEvent = getNextTimeForEvent(event, new Date(), true);
			globals.addRecordToLog("getNextEventAlarmTime going to compare - "
					+ DateFormat.format("EEEE d MMM, kk:mm", nextTimeForEvent)
					+ " with "
					+ DateFormat.format("EEEE d MMM, kk:mm", currentTime));
			if (nextTimeForEvent != null && nextTimeForEvent.after(currentTime)) {
				if (timeToReturn == null
						|| timeToReturn.after(nextTimeForEvent)) {
					timeToReturn = nextTimeForEvent;
					returnEvent = event;
				}
			}
		}
		if (timeToReturn != null) {
			globals.addRecordToLog("getNextEventAlarmTime timeToReturn = "
					+ DateFormat.format("EEEE d MMM, kk:mm", timeToReturn));
			EventAlarmTime eat = new EventAlarmTime(returnEvent,
					timeToReturn.getTime());
			return eat;
		} else
			return null;
	}

	// event next time is time when alarm for event happens next time
	// that can be time start of event - reminder, or start of event
	public static Date getNextTimeForEvent(SmartCalendarEvent event,
			Date currentTime, boolean remind) {
		ArrayList<String> repeatDays = new ArrayList<String>(
				Arrays.asList(event.getRepeatDays().split(",")));
		Date date = new Date();
		date.setTime(event.getDate().getTime());
		date.setHours(event.getStartTime().getHours());
		date.setMinutes(event.getStartTime().getMinutes());
		// should use repeat days if date was already
		if (date.before(currentTime)) {
			if (repeatDays.size() == 0)
				return null;

			int day = currentTime.getDay(); // day of week
			int daysAfterToday = 0;
			while (daysAfterToday <= 6) {
				int ix = day + daysAfterToday;
				if (ix > 6)
					ix -= 7;
				if (repeatDays.indexOf(globals.weekDays[ix]) != -1) {
					break;
				}
				daysAfterToday++;
			}
			date = globals.getBeginOfDayPlusSeconds(currentTime,
					(int) (daysAfterToday * globals.SECONDS_PER_DAY));
		}
		// if reminder exist using it first
		Date startTime = new Date();
		if (remind) {
			Date reminderTime = event.getReminderTime();
			if (reminderTime == null)
				startTime = event.getStartTime();
			else {
				startTime.setTime(event.getDate().getTime());
				startTime.setHours(reminderTime.getHours());
				startTime.setMinutes(reminderTime.getMinutes());
				if (globals.isTimeAfter(currentTime, startTime))
					startTime = event.getStartTime();
			}
		} else
			startTime = event.getStartTime();

		date.setHours(startTime.getHours());
		date.setMinutes(startTime.getMinutes());
		date.setSeconds(0);

		return date;
	}

	public static void setAlarmForFirstEvent(Context context) {
		setAlarmForFirstEvent(context, false);
	}

	public static void setAlarmForFirstEvent(Context context, boolean snooze) {
		// set alarm for first event in future
		globals.addRecordToLog("setAlarmForFirstEvent - started");
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, EventNotification.class);
		EventAlarmTime eventAlarmTime = EventsManager.getNextEventAlarmTime();
		if (eventAlarmTime != null)
			i.putExtra("eventId", eventAlarmTime.event.getId());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		am.cancel(pendingIntent);
		if (eventAlarmTime != null) {
			if (snooze) {
				Date currentTime = new Date();
				currentTime.setTime(currentTime.getTime()
						+ globals.MILLISECONDS_IN_5_MINUTES);
				if (currentTime.getTime() < eventAlarmTime.nextTimeForEvent) {
					globals.addRecordToLog("setting reminder to "
							+ DateFormat.format("EEEE d MMM, kk:mm",
									currentTime).toString());
					am.set(AlarmManager.RTC_WAKEUP, currentTime.getTime(),
							pendingIntent);
					return;
				}
			}
			globals.addRecordToLog("setting reminder to "
					+ DateFormat.format("EEEE d MMM, kk:mm",
							eventAlarmTime.getDate()));
			am.set(AlarmManager.RTC_WAKEUP, eventAlarmTime.nextTimeForEvent,
					pendingIntent);
		} else
			globals.addRecordToLog("setAlarmForFirstEvent eventAlarmTime = null");
	}

}
