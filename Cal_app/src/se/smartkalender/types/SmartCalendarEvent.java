package se.smartkalender.types;

import java.io.Serializable;
import java.util.Date;

import se.smartkalender.globals;


import android.graphics.Color;
import android.text.format.DateFormat;

public class SmartCalendarEvent implements Comparable<SmartCalendarEvent>,Serializable{
	private int color = Color.YELLOW;
	private int id;
	private String name = "Event Default Name";
	private Date date;
	private Date startTime;
	private Date finishTime;
	private Integer reminderTime;
	private String repeatDays = "";
	private String iconId = "ic_sunny";
	private String description = "";
	private String category = "";
	private boolean isYourImage = false;
	private String yourImagePath = "NON"; // defult image path
	
	public SmartCalendarEvent(int id, String name, String iconId, Integer color, Date date) {
		this.name = name;
		this.date = date;	
		this.iconId = iconId;
		this.id = id;
		this.color = color;
	}
	
	public SmartCalendarEvent(int id, String name, String iconId, Integer color, Date date, Date startTime, Date finishTime,boolean isYourImage,String yourImagePath) {
		this.name = name;
		this.date = date;	
		this.startTime = startTime;	
		this.finishTime = finishTime;	
		this.startTime.setSeconds(0);
		this.finishTime.setSeconds(0);
		this.iconId = iconId;
		this.id = id;
		this.color = color;
		this.isYourImage = isYourImage;
		this.yourImagePath = yourImagePath;
	}
	
	public SmartCalendarEvent(SmartCalendarEvent event) {
		this.color = event.getColor();
		this.id = event.getId();
		this.name = event.getName();
		this.date = new Date(event.getDate().getTime());
		this.startTime = new Date(event.getStartTime().getTime());	
		this.finishTime = new Date(event.getFinishTime().getTime());	
		this.startTime.setSeconds(0);
		this.finishTime.setSeconds(0);		
		this.repeatDays = event.getRepeatDays();
		this.iconId = event.getIconId();		
		this.description = event.getDesciption();
		this.reminderTime = event.reminderTime;
		this.category = event.category;
		this.isYourImage = event.getYourImageFlag();
		this.yourImagePath =event.getYourImagePath();
	}

	public SmartCalendarEvent(SmartCalendarEvent event, int id) {
		this(event);
		this.id = id;	
	}

	public boolean getYourImageFlag(){
		return isYourImage;
	}
	public void setYourImageFlag(boolean isYourImage) {
		this.isYourImage = isYourImage;
	}
	public int getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public Date getDate() {
		if (date == null) return new Date();
		return date;
	}
	
	public String getIconId() {
		return iconId;
	}	

	public Date getStartTime() {
		if (startTime == null) return new Date();
		return startTime;
	}
	
	public Date getFinishTime() {
		if (finishTime == null) return new Date();
		return finishTime;		
	}

	public int getId() {
		return id;
	}

	public void setId(int newid) {
		id = newid;
	}

	public String getEventDate() {
		if (repeatDays.trim().length() == 0)	
			return DateFormat.format("EEEE, dd MMMM ", date).toString();
		else return getRepeatDays();			
	}
	
	public String getRepeatDays() {
		return repeatDays;		
	}

	public Date getReminderTime() {
		if (reminderTime != null) {
			Date time = new Date();
			time.setTime(getStartTime().getTime() - reminderTime * globals.MILLISECONDS_IN_1_MINUTES);	
			return time;
		}
		else return null;
	}
		
	public Integer getReminder() {
		return reminderTime;
	}
	
	public String getCategory() {
		return category;
	}

	public void setColor(int color) {
		this.color = color;		
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;		
	}

	public void setStartTime(Date time) {
		this.startTime = time;		
		this.startTime.setSeconds(0);
	}

	public void setFinishTime(Date time) {
		this.finishTime = time;	
		this.finishTime.setSeconds(0);
		
	}
	
	public void setDescription(String description) {
		this.description = description;	
		
	}

	public String getDesciption() {
		return description;
	}

	public void setDate(Date date) {
		this.date = date;		
	}

	public void setName(String name) {
		this.name = name;		
	}

	public void setReminder(int reminderTime) {
		this.reminderTime = reminderTime;
	}
	
	public void setRepeatDays(String value) {
		this.repeatDays = value;		
	}

	private Boolean isEmptyString(String toTest){
		return (toTest == null || toTest.trim().equals("") );
	}
	
	public void addRepeatDay(String day) {
		if ( !isEmptyString(this.repeatDays) ) 
			this.repeatDays += ",";	
		this.repeatDays += day;		
	}
	
	public void setCategory(String category) {
		this.category = category;		
	}

	@Override
	public int compareTo(SmartCalendarEvent event) {
		if (repeatDays.trim().length() == 0) {
			if (event.repeatDays.trim().length() != 0) return 1;
			if(globals.isEqualDate(date, event.date)) {
				if (globals.isTimeAfter(event.getStartTime(), this.getStartTime())) 
					return 1;
			}
			else if (date.before(event.date)) 
				return 1;	
		}
		return -1;
	}

	public void setYourImagePath(String filePath) {
 		this.yourImagePath = filePath;
	}
	public String getYourImagePath( ) {
 		return this.yourImagePath ; 
	}
}
