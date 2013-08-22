package se.smartkalender;

import java.util.ArrayList;

import java.util.Date;

import se.smartkalender.globals;
import se.smartkalender.types.SmallEventReturnType;
import se.smartkalender.types.SmartCalendarEvent;

import se.smartkalender.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.WindowManager;

public class SmartCalendarFactory {
	private static final int TIME_CIRCLE_ID = 5;
	//private static final int TIME_CIRCLE_KEY_TIME = 1;
	//private static final int TIME_CIRCLE_KEY_ICON = 2;
	static int hourTextHeight = 0;
	static int hourTextWidth = 0;	
	static int minutesTextHeight = 0;
	static int timeLineHeight = 0;
	static int timeCircleWidth = 0;
	static int timeCircleHeight = 0;
	static int timeGridStart = 0;
	static int screenWidth = 0;
	static int screenHeight = 0;
	static int eventAreaWidth = 0;
	static float timeGridContainerHeight = 0;
	static int timeGridContainerPaddingTop = 0;
	
	static GradientDrawable progressGradientDrawable = new GradientDrawable(
	        GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
	                0xff1eff90,0xff6ab600,0xff7ba836});
	static ClipDrawable progressClipDrawable = new ClipDrawable(
	        progressGradientDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
	static Drawable[] progressDrawables = {
	        new ColorDrawable(0xffffffff),
	        progressClipDrawable, progressClipDrawable};
	static LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
	
	static float SCALE = 1;
	
	static int d1 = 0;
	
	static int zoom = 15;
	
	static float spSz = 0;
	public static float pixelsToSp(Context context, Float px) {
	    float scaledDensity = context.getResources().getDisplayMetrics().density;
	    return px/scaledDensity;
	}
	
	public static int dipsToPixels(int valueDips) {
		// Convert dips to pixels
		return (int)(valueDips * SCALE + 0.5f); // 0.5f for rounding
	}
	
	static View makeCalendarView(Activity a, int _zoom) {
		boolean hourMeasured = false;
		boolean minuteMeasured = false;	
		
		LayoutInflater layoutInflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout mainView = (LinearLayout)layoutInflater.inflate(R.layout.main, null);   
		LinearLayout timeGridContainer = (LinearLayout) mainView.findViewById(R.id.timeGridContainer2);
		LinearLayout timeLinesContainer = (LinearLayout) mainView.findViewById(R.id.timeLinesContainer);
		SCALE = a.getResources().getDisplayMetrics().density;

		
		zoom = _zoom;
		globals.generateMinutesString(zoom);
		// now add times text for all hours and minutes	
		for (String hourString : globals.timeHours) {
			View vh = addHourText(hourString, timeGridContainer, layoutInflater);	
			if (!hourMeasured) {
				vh.measure(0, 0);	
				hourTextHeight = vh.getMeasuredHeight();
				hourTextWidth = vh.getMeasuredWidth(); 
				hourMeasured = true;
			}
			for (String minuteString : globals.timeMinutes) {
				View vm = addMinutesText(minuteString, timeGridContainer, layoutInflater);
				if (!minuteMeasured && minuteString.equals("30")) {
					vm.measure(0, 0);	
					minutesTextHeight = vm.getMeasuredHeight();
					minuteMeasured = true;
				}
			}			
		}
		d1 = hourTextHeight + minutesTextHeight*globals.timeMinutes.length;
		
		timeGridContainer.measure(0, 0);
		timeGridContainerHeight = timeGridContainer.getMeasuredHeight();
		timeGridContainerPaddingTop = timeGridContainer.getPaddingTop();
		
		//int hours = 0;
		boolean first = true;
		for (int hours = 0; hours < 24; hours++) {
			int minutes = 0;
			addTimeLine(first, false, timeLinesContainer, layoutInflater);
			if (first) d1 = d1 - timeLineHeight*4;
			//if (zoom == 15) addTimeCircle(a, getTimeObject(hours, minutes * zoom)).setId(TIME_CIRCLE_ID);
			//else if (zoom == 5) addTimeCircle(a, getTimeObject(hours, minutes * zoom)).setId(TIME_CIRCLE_ID);
			//else addInvisibleTimeCircle(a, getTimeObject(hours, minutes * zoom)).setId(TIME_CIRCLE_ID);
			for (int i = 0; i < 4; i++) {
				if (i != 0) addTimeLine(first, true, timeLinesContainer, layoutInflater);
				if (zoom == 15) addTimeCircle(a, getTimeObject(hours, minutes * zoom), first, i == 0, timeLinesContainer).setId(TIME_CIRCLE_ID);
				else if (zoom == 5) addTimeCircle(a, getTimeObject(hours, minutes * 15), first, i == 0, timeLinesContainer).setId(TIME_CIRCLE_ID);
				else addInvisibleTimeCircle(a, getTimeObject(hours, minutes * 15), first, i == 0, timeLinesContainer).setId(TIME_CIRCLE_ID);
				minutes++;
				first = false;
			}	
			//hours++;
		}	
		Display display = ((WindowManager) a.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		eventAreaWidth = screenWidth - timeCircleWidth - hourTextWidth;
		
		return mainView;
	}
	
	static void recalculate(Activity a) {
		LinearLayout timeGridContainer = (LinearLayout) a.findViewById(R.id.timeGridContainer2);		
		timeGridContainer.measure(0, 0);
		timeGridContainerHeight = (float) timeGridContainer.getMeasuredHeight();		
	}
	
	static Date getTimeObject(int hours, int minutes) {
		Date time = new Date();
		time.setHours(hours);
		time.setMinutes(minutes);		
		return time;
	}
	
	static View addHourText(String name, LinearLayout timeGridContainer, LayoutInflater layoutInflater) {
		TextView tv = (TextView)layoutInflater.inflate(R.layout.hour_text, null);   		
		tv.setText(name);			     
		timeGridContainer.addView(tv);	
		tv.setPadding(0, 0, 0, 1);
		return tv;
	}	
	
	static View addMinutesText(String name, LinearLayout timeGridContainer, LayoutInflater layoutInflater) {
		TextView tv = (TextView)layoutInflater.inflate(R.layout.minutes_text, null);   
        tv.setText(name);		
		timeGridContainer.addView(tv);		
		if (name.equals("15")) tv.setPadding(0, 0, 0, 7);
		else if (name.equals("30")) tv.setPadding(0, 2, 0, 8);
		else if (name.equals("45")) tv.setPadding(0, 0, 0, 13);
		else tv.setPadding(0, 0, 0, 10);
		return tv;		
	}	
	
	static void addTimeLine(boolean isFirstCalculation, boolean line_dot, LinearLayout timeLinesContainer, LayoutInflater layoutInflater) {
		LinearLayout ll;
		if (line_dot) ll = (LinearLayout)layoutInflater.inflate(R.layout.divider_minutes, null);  
		else ll = (LinearLayout)layoutInflater.inflate(R.layout.divider_hours, null);  
		LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		if (isFirstCalculation) { 
			ll.measure(0, 0);
			timeLineHeight = ll.getMeasuredHeight();
			int diff = hourTextHeight - timeLineHeight;
			params.topMargin = diff / 2;
		}
		else params.topMargin = 0;
        params.bottomMargin = 0;
        if (!line_dot)  params.leftMargin = dipsToPixels(10) + hourTextWidth;
        else params.leftMargin = dipsToPixels(10);
        if (zoom == 15) params.rightMargin = dipsToPixels(5);
        ll.setLayoutParams(params);  		
		timeLinesContainer.addView(ll);

	}
	
	static View addTimeCircle(Activity a, Date time, boolean isFirstCalculation, boolean isFirstCircle, LinearLayout timeLinesContainer) {      
		ImageView iv = new ImageView(a);
		iv.setScaleType(ScaleType.FIT_XY);
		
		if (zoom == 15)	iv.setImageResource(R.drawable.time_dot_inactive);
		else iv.setImageResource(R.drawable.time_dot_inactive_big);
		
		iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        timeLinesContainer.addView(iv);
        if (isFirstCalculation) {
        	iv.measure(0, 0);
        	timeCircleHeight = iv.getMeasuredHeight();
        }
		iv.measure(0, 0);
		int diff = d1/4 - timeCircleHeight;//(timeGridContainerHeight / 24) / 4 - iv.getMeasuredHeight();
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv.getLayoutParams();//new LinearLayout.LayoutParams(10, 10);
        params.topMargin = (int)(diff / 2);
        if (isFirstCircle) params.topMargin += d1 - (d1/4)*4;
        params.bottomMargin = (int)(diff - (diff / 2));
        if (zoom == 15) params.rightMargin = dipsToPixels(8);
        else params.rightMargin = dipsToPixels(1);
        iv.setLayoutParams(params);  
        if (isFirstCalculation) {
			iv.measure(0, 0);
			timeCircleWidth = iv.getMeasuredWidth();	
        }
        iv.setTag(time);
        Integer timeKey = time.getHours() * 60 + time.getMinutes();
        iv.setTag(R.id.timeKey, timeKey);
        iv.setTag(R.id.timeCircleIconBroken, false);
		return iv;
	}	
	
	static View addInvisibleTimeCircle(Activity a, Date time,  boolean isFirstCalculation, boolean isFirstCircle, LinearLayout timeLinesContainer) {
		View v  = addTimeCircle(a, time,  isFirstCalculation, isFirstCircle, timeLinesContainer);
		v.setVisibility(View.INVISIBLE);
		return v;
	}	
	
	static View addEventToPage(Activity a, SmartCalendarEvent e, Date startTime, LayoutInflater layoutInflater) {
        View v = layoutInflater.inflate(R.layout.event_item, null);   
        RelativeLayout placeForEvents = (RelativeLayout) a.findViewById(R.id.placeForEvents);
        placeForEvents.addView(v);
        
        TextView eventName = (TextView) v.findViewById(R.id.eventName);
        eventName.setText(DateFormat.format("kk:mm", e.getStartTime()).toString() + " " + e.getName());
        ImageView eventIcon = (ImageView) v.findViewById(R.id.eventIcon);
        eventIcon.setImageDrawable(a.getResources().getDrawable(globals.getIconId(a, e.getIconId())));
        
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.getLayoutParams();
        params.topMargin = getLocationForTime(a, startTime);
        Date finish = new Date();
       	finish.setTime(e.getFinishTime().getTime());
        if (globals.isTimeAfter(finish, startTime))
        	params.height = getLocationForTime(a, finish) - params.topMargin;
        else {
        	finish.setHours(23);
        	finish.setMinutes(59);
        	params.height = getLocationForTime(a, finish) - params.topMargin;
        }
        params.width = eventAreaWidth;
        params.leftMargin = hourTextWidth + dipsToPixels(10);
        params.rightMargin = dipsToPixels(25);
        v.setLayoutParams(params); 
        v.setTag(e);
        
	
		LinearLayout eventBody = (LinearLayout) v.findViewById(R.id.eventBody);
		eventBody.getBackground().setColorFilter(e.getColor(), PorterDuff.Mode.MULTIPLY );
		
        v.setOnClickListener(((ISmartCalendarInterface)a).getShowEventClickListener());
        
        updateTimeCircleIcon(a, startTime.getHours()*60 + startTime.getMinutes(), false);
        return v;
	}

	static SmallEventReturnType addSetOfEventsToPage(Activity a,  ArrayList<SmartCalendarEvent> events, Date startTime, LayoutInflater layoutInflater) {
        View frame = layoutInflater.inflate(R.layout.container_for_small_events, null); 
        SmallEventReturnType result = new SmallEventReturnType();
        		
        LinearLayout container = (LinearLayout) frame.findViewById(R.id.container);
        RelativeLayout placeForEvents = (RelativeLayout) a.findViewById(R.id.placeForEvents);      
        placeForEvents.addView(frame);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)frame.getLayoutParams();
        for (SmartCalendarEvent e : events) {        	
	        View eventBody = layoutInflater.inflate(R.layout.event_item_small, null);  
	        ImageView eventIcon = (ImageView) eventBody.findViewById(R.id.eventIcon);
	        eventIcon.setImageDrawable(a.getResources().getDrawable(globals.getIconId(a, e.getIconId())));
	        	
			eventBody.getBackground().setColorFilter(e.getColor(), PorterDuff.Mode.MULTIPLY );
			eventBody.setTag(e);
			if (zoom != 1) 	{
				eventBody.setOnClickListener(((ISmartCalendarInterface)a).getSmallEventClickListener()); 
				eventBody.setOnLongClickListener(((ISmartCalendarInterface)a).getSmallEventLongClickListener());  
			}
			else eventBody.setOnClickListener(((ISmartCalendarInterface)a).getShowEventClickListener());
			
	        container.addView(eventBody);	  
	        result.smallEventsViews.add(eventBody);
        }
        Date start = new Date();
        Date finish = new Date();
        finish.setTime(start.getTime() + globals.MILLISECONDS_IN_1_MINUTES*zoom);
        params.height = getLocationForTime(a, finish) - getLocationForTime(a, start);
        params.width = eventAreaWidth;
        params.topMargin = getLocationForTime(a, startTime);
        params.leftMargin = dipsToPixels(65);
        params.rightMargin = dipsToPixels(45);
        result.mainView = frame;
        
        frame.setLayoutParams(params);   
        
        updateTimeCircleIcon(a, startTime.getHours()*60 + startTime.getMinutes(), true);

        return result;
	}

	static int getLocationForTime(Activity a, Date time) {
		float minuteHeight = timeGridContainerHeight / (24*60);
		return (int) (((time.getHours() * 60 + time.getMinutes()) * minuteHeight) + (hourTextHeight / 2) + timeGridContainerPaddingTop);
	}

	public static void removeEventView(Activity a, View v) {
		RelativeLayout placeForEvents = (RelativeLayout) a.findViewById(R.id.placeForEvents);
		placeForEvents.removeView(v);		
	}
	
	public static void updateEventView(Activity a, View v, Date selectedDate) {
		SmartCalendarEvent e = (SmartCalendarEvent)v.getTag();
		if (e != null && EventsManager.isEventOnDate(e, selectedDate)) {
			LinearLayout timeLeftPanel = (LinearLayout) v.findViewById(R.id.timeLeftPanel);
			if (globals.isEventActive(selectedDate, e)) {
		    	ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		    	progressLayerDrawable.setId(0, android.R.id.background);
		        progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
		        progressLayerDrawable.setId(2, android.R.id.progress);
		        progressBar.setProgressDrawable(progressLayerDrawable);
		        
		    	TextView timeLeft = (TextView) v.findViewById(R.id.timeLeft);
		    	timeLeftPanel.setVisibility(View.VISIBLE);	
		    	int eventTimeMinutes = globals.getDifferenceInMinutes(e.getFinishTime(), e.getStartTime());	
		    	int diff = (int)((globals.getDifferenceInMinutes(selectedDate, e.getStartTime()) * 100) / eventTimeMinutes);
			    progressBar.setProgress(/*100 - */diff); 
			    if (globals.timeShowHoursMinutes) {
			    	int hours = (eventTimeMinutes - globals.getDifferenceInMinutes(selectedDate, e.getStartTime())) / 60;
			    	int minutes = (eventTimeMinutes - globals.getDifferenceInMinutes(selectedDate, e.getStartTime())) % 60;
			    	
			    	if( hours > 0 ) {
			    		timeLeft.setText(Integer.toString(hours) + "h " + Integer.toString(minutes) + "min");
			    	}
			    	else {
			    		timeLeft.setText(Integer.toString(minutes) + " min");
			    	}
			    }
			    else 
			    	timeLeft.setText(Integer.toString(eventTimeMinutes - globals.getDifferenceInMinutes(selectedDate, e.getStartTime())) + " " + globals.minutes);
		    }
		    else timeLeftPanel.setVisibility(View.GONE);			
		}				
	}
	
	public static void updateTimeCircleIcon(Activity a) {
		LinearLayout timeLinesContainer = (LinearLayout) a.findViewById(R.id.timeLinesContainer);
		for (int i = 0; i < timeLinesContainer.getChildCount(); i++) {
			View v = timeLinesContainer.getChildAt(i);
			if (v.getId() == TIME_CIRCLE_ID) {
				v.setTag(R.id.timeCircleIconBroken, false);					
			}
		}
	}
	
	public static void updateTimeCircleIcon(Activity a, Integer timeKey, boolean broken) {
		LinearLayout timeLinesContainer = (LinearLayout) a.findViewById(R.id.timeLinesContainer);
		for (int i = 0; i < timeLinesContainer.getChildCount(); i++) {
			View v = timeLinesContainer.getChildAt(i);
			if (v.getId() == TIME_CIRCLE_ID) {
				Integer _timeKey = (Integer)v.getTag(R.id.timeKey);	
				if ((timeKey >= _timeKey) && timeKey < (_timeKey + 15)) {
					v.setTag(R.id.timeCircleIconBroken, broken);		
				}				
			}
		}
	}
	
	public static void updateTimeCircleViews(Activity a, Date selectedDate) {
		Date timecurrent = new Date();
		LinearLayout timeLinesContainer = (LinearLayout) a.findViewById(R.id.timeLinesContainer);
		for (int i = 0; i < timeLinesContainer.getChildCount(); i++) {
			View v = timeLinesContainer.getChildAt(i);
			if (v.getId() == TIME_CIRCLE_ID) {
				Date _time = (Date)v.getTag();
				Boolean broken = (Boolean)v.getTag(R.id.timeCircleIconBroken);
				int hour = _time.getHours(); // backup hour & minutes info
				int minutes = _time.getMinutes();
				_time.setTime(selectedDate.getTime());
				_time.setHours(hour);
				_time.setMinutes(minutes);
				if (zoom == 15) {
					if (timecurrent.after(_time)) {
						if (broken)
							((ImageView)v).setImageResource(R.drawable.time_dot_broken_inactive);		
						else ((ImageView)v).setImageResource(R.drawable.time_dot_inactive);	
					}
					else {
						if (broken)
							((ImageView)v).setImageResource(R.drawable.time_dot_broken_active);
						else ((ImageView)v).setImageResource(R.drawable.time_dot_active);
					}
				}
				else {
					if (timecurrent.after(_time)) {
						((ImageView)v).setImageResource(R.drawable.time_dot_inactive_big);					
					}
					else ((ImageView)v).setImageResource(R.drawable.time_dot_active_big);					
				}
			}			
		}		
	}
	
	public static void updateTimeCircleViews2(Activity a, Date selectedDate) {
		Date timecurrent = new Date();
		LinearLayout timeLinesContainer = (LinearLayout) a.findViewById(R.id.timeLinesContainer);
		for (int i = 0; i < timeLinesContainer.getChildCount(); i++) {
			View v = timeLinesContainer.getChildAt(i);
			if (v.getId() == TIME_CIRCLE_ID) {
				Date _time = (Date)v.getTag();
				int hour = _time.getHours(); // backup hour & minutes info
				int minutes = _time.getMinutes();
				_time.setTime(selectedDate.getTime());
				_time.setHours(hour);
				_time.setMinutes(minutes);
				if (zoom == 15) {
					if (timecurrent.after(_time)) {
						if (EventsManager.getEventsForPeriod(_time, zoom).size() > 1)
							((ImageView)v).setImageResource(R.drawable.time_dot_broken_inactive);		
						else ((ImageView)v).setImageResource(R.drawable.time_dot_inactive);	
					}
					else {
						if (EventsManager.getEventsForPeriod(_time, zoom).size() > 1)
							((ImageView)v).setImageResource(R.drawable.time_dot_broken_active);
						else ((ImageView)v).setImageResource(R.drawable.time_dot_active);
					}
				}
				else {
					if (timecurrent.after(_time)) {
						((ImageView)v).setImageResource(R.drawable.time_dot_inactive_big);					
					}
					else ((ImageView)v).setImageResource(R.drawable.time_dot_active_big);					
				}
			}			
		}
	}

	public static void setZoom(int _zoom) {
		zoom = _zoom;		
	}

	public static void scrollToEvent(Activity a, SmartCalendarEvent eventToScroll) {
		final int scrollY = getLocationForTime(a, eventToScroll.getStartTime());	
		final ScrollView scrollView = (ScrollView) a.findViewById(R.id.scrollview);    		
		scrollView.post(new Runnable() {
	         public void run() {
	        	 scrollView.scrollTo(0, scrollY);
	        	 if (scrollView.getScrollY() == scrollY) scrollView.scrollTo(0, scrollY - (screenHeight / 3));
	         }
	    });
	}
	
	public static void scrollToTime(Activity a, Date time) {
		final int scrollY = getLocationForTime(a, time);	
		final ScrollView scrollView = (ScrollView) a.findViewById(R.id.scrollview);    
		scrollView.post(new Runnable() {
	         public void run() {
	        	 scrollView.scrollTo(0, scrollY);
	        	 if (scrollView.getScrollY() == scrollY) scrollView.scrollTo(0, scrollY - (screenHeight / 3));
	         }
	    });
	}


	public static SmartCalendarEvent getActiveEvent(ArrayList<SmartCalendarEvent> periodEvents, Date curDate) {
		for (SmartCalendarEvent e : periodEvents) {
			if (globals.isEventActive(curDate, e)) {
		    	return e;		    		    
		    }
		}
		return null;
	}

}
