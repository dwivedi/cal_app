package se.smartkalender;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.FileHandler;

import se.smartkalender.types.SmartCalendarEvent;

import se.smartkalender.R;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class globals {
	static String[] timeHours;
	static String[] timeMinutes;
	static String[] weekDays;
	static String[] weekDaysFull;
	static String[] months;
	static String[] monthsFull;
	static ArrayList<Integer> iconsIds;
	static String[] iconsPaths;
	public static boolean timeShowHoursMinutes = true;
	
	public static String minutes;
	public static String TheEventWillStartNow;
	public static String TheEventWillStartIn;
	
	public static Long MILLISECONDS_IN_DAY = 1000L * 60L * 60L * 24L;
	public static Long MILLISECONDS_IN_15_MINUTES = 1000L * 60L * 15L;
	public static Long MILLISECONDS_IN_5_MINUTES = 1000L * 60L * 5L;
	public static Long MILLISECONDS_IN_1_MINUTES = 1000L * 60L * 1L;
	public static Long MILLISECONDS_IN_1_SECOND = 1000L;
	public static int MINUTES_PER_DAY = 60 * 24;
	public static int SECONDS_PER_DAY = 60 * 60 * 24;
	public static int SECONDS_PER_HOUR = 60 * 60;
	public static int SECONDS_PER_MINUTE = 60;	
	public static FileHandler logger = null;
	public static View zoomedView = null;
	public static Date selectedDay = new Date();
		
	static void init(Context context) {
		timeHours = context.getResources().getStringArray(R.array.hours);
		timeMinutes = context.getResources().getStringArray(R.array.minutes);
		weekDays = context.getResources().getStringArray(R.array.weekDays);			
		weekDaysFull = context.getResources().getStringArray(R.array.weekDaysFull);	
		months = context.getResources().getStringArray(R.array.months);			
		monthsFull = context.getResources().getStringArray(R.array.monthsFull);	
		iconsPaths = context.getResources().getStringArray(R.array.event_icons_paths);
		iconsIds = getIconsIds(context, context.getResources().getStringArray(R.array.event_icons_paths));
		EventsManager.loadEventsFromTemplateFileConfig(context);
		EventsManager.extractEventsFromDB(context);
		
		minutes = context.getResources().getString(R.string.Minutes);
		TheEventWillStartNow = context.getResources().getString(R.string.TheEventWillStartNow);
		TheEventWillStartIn = context.getResources().getString(R.string.TheEventWillStartIn);
		
	}
	
	public static String[] adjustFirstDayOfWeek(String[] inArray){
		String[] outArray = new String[7];
		outArray[0] = inArray[1];
		outArray[1] = inArray[2];
		outArray[2] = inArray[3];
		outArray[3] = inArray[4];
		outArray[4] = inArray[5];
		outArray[5] = inArray[6];
		outArray[6] = inArray[0];
		return outArray;
	}
	
	private static ArrayList<Integer> getIconsIds(Context context, String[] paths) {

	    int resID=0;        
	    ArrayList<Integer> ids = new ArrayList<Integer>();

	    for (String path : paths) {
	    	resID = context.getResources().getIdentifier(path, "drawable", context.getPackageName());	    
	    	if (resID!=0) ids.add(resID);
	    }
	    return ids;
	}
	
	public static Integer getIconId(Context context, String path) {
	    int resID=0;        
	    resID = context.getResources().getIdentifier(path, "drawable", context.getPackageName());	    
	    if (resID!=0) return resID;
	    
	    return null;
	}
	
	public static String getIconPath(int resID) {
		for (int i = 0; i < iconsIds.size(); i++) {
			if (resID == iconsIds.get(i)) {
				return iconsPaths[i];
			}			
		}		
		return null;
	}

	public static void generateMinutesString(int zoom) {
		int minutesCount = 0;
		if (zoom == 15) minutesCount = 3; // 15 minutes
		else if (zoom == 5) minutesCount = 11; // 5 minutes
		else if (zoom == 1) minutesCount = 59; // 1 minutes

		timeMinutes = new String[minutesCount];
		for (int i = 0; i < minutesCount; i++) timeMinutes[i] = Integer.toString(zoom * (i + 1));
		
	}
	
	public static Date getTimePlusSeconds(Date time, int seconds) {
		Date result = new Date();
		result.setTime(time.getTime() + MILLISECONDS_IN_1_SECOND * seconds);
		return result;
	}
	
	public static Date getTimeFromSeconds(int seconds) {
		Date result = new Date();
		result.setHours(0);
		result.setMinutes(0);
		result.setTime(result.getTime() + MILLISECONDS_IN_1_SECOND * seconds);
		return result;
	}

	public static Date getBeginOfDayPlusSeconds(Date selectedDay, int seconds) {
		Date result = new Date();
		result.setTime(selectedDay.getTime());
		result.setHours(seconds / SECONDS_PER_HOUR);
		result.setMinutes((seconds / SECONDS_PER_MINUTE) % 60);
		result.setSeconds(0);
		return result;
	}
	
	public static boolean isEqualDate(Date date1, Date date2) {
		if ( (date1.getYear() == date2.getYear()) && 
			(date1.getMonth() == date2.getMonth()) && 
			(date1.getDate() == date2.getDate()) ) return true;
		return false;
	}
	
	public static int getDifferenceInMinutes(Date end, Date begin) {
		Date _end = new Date();
		_end.setHours(end.getHours());
		_end.setMinutes(end.getMinutes());
		Date _begin = new Date();
		_begin.setTime(_end.getTime());
		_begin.setHours(begin.getHours());
		_begin.setMinutes(begin.getMinutes());
		long diff = _end.getTime() - _begin.getTime();
		if (diff < 0) {
			diff = (_end.getTime() + globals.MILLISECONDS_IN_DAY) - _begin.getTime();			
		}
		return (int)(diff / globals.MILLISECONDS_IN_1_MINUTES);
	}

	public static boolean isTimeBefore(Date time, Date timeToCompare) {
		long time1 = time.getHours() * SECONDS_PER_HOUR + time.getMinutes() * SECONDS_PER_MINUTE;
		long time2 = timeToCompare.getHours() * SECONDS_PER_HOUR + timeToCompare.getMinutes() * SECONDS_PER_MINUTE;
		if (time2 > time1)  
			return true;
		return false;
	}
	
	public static boolean isTimeAfter(Date time, Date timeToCompare) {
		long time1 = time.getHours() * SECONDS_PER_HOUR + time.getMinutes() * SECONDS_PER_MINUTE;
		long time2 = timeToCompare.getHours() * SECONDS_PER_HOUR + timeToCompare.getMinutes() * SECONDS_PER_MINUTE;
		if (time2 <= time1)  
			return true;
		return false;
	}

	public static boolean isTimeAfter(Date time, Date timeToCompare, long offset_minutes) {
		long time1 = time.getHours() * SECONDS_PER_HOUR + time.getMinutes() * SECONDS_PER_MINUTE;
		long time2 = timeToCompare.getHours() * SECONDS_PER_HOUR + (timeToCompare.getMinutes() + offset_minutes) * SECONDS_PER_MINUTE;
		if (time2 <= time1)  return true;
		return false;
	}

	public static Date getRoundedTime(Date startTime, int zoom) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
    public static void makeDirOnSdCard() {
    	try {
    		File dir = new File(Environment.getExternalStorageDirectory() + "/Android" + Environment.getDataDirectory() + "/se.smartkalender/files/");
    		dir.mkdirs();
    	} 
    	catch(Exception ex) {
    		Log.e("makeDirOnSdCard", ex.getMessage());    		
    	}
    }
	
    public static void addRecordToLog(String message){
    	//Not in release: Log.d("SmartCalendar", message);
    	/*LogRecord logRecord = new LogRecord(Level.INFO, message);
    	if (logger == null) {
	        try {
	        	logger = new FileHandler(Environment.getExternalStorageDirectory() + "/Android" + Environment.getDataDirectory() + "/se.smartkalender/files/log_"  + new Date().getTime() + ".xml");
	        } catch (IOException e) {
	            Log.e("SmartCalendar", e.toString());
	            // it seems that no dirs on sd card
	            makeDirOnSdCard();
	            try {
					logger = new FileHandler(Environment.getExternalStorageDirectory() + "/Android" + Environment.getDataDirectory() + "/se.smartkalender/files/log_"  + new Date().getTime() + ".xml");
				} catch (IOException e1) {
		        	Log.e("SmartCalendar", e1.toString());
		            return;
				}
	        } catch (Exception e) {
	        	Log.e("SmartCalendar", e.toString());
	            return;
	        }
    	}
    	logger.publish(logRecord);    */	
    }

	public static boolean isEventActive(Date selectedDate, SmartCalendarEvent e) {
	    if (globals.isTimeAfter(selectedDate, e.getStartTime())){
	    	if (globals.isTimeBefore(selectedDate, e.getFinishTime())) return true;
	    	else {
	    		if (!globals.isTimeAfter(e.getFinishTime(), e.getStartTime())) return true;	    			    		
	    	}
	    }
	    return false;
	}

}
