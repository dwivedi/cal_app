package se.smartkalender.listviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import se.smartkalender.globals;
import se.smartkalender.types.SmartCalendarEvent;

import se.smartkalender.R;

/**
 * listview that display all events
 *
 */
public class EventsListView extends CustomListView  {

	private EventsAdapter events_adapter;
	private Activity activity;
	int layoutResourceId;

	public EventsListView(Activity a, ArrayList<SmartCalendarEvent> events, int layoutResourceId,
			OnItemClickListener itemClickListener, String categoryFilter) {
		super(a);
		activity = a;
		this.layoutResourceId = layoutResourceId;
		Reload(a, events, categoryFilter);
		setOnItemClickListener(itemClickListener);
	}
	
	public EventsListView(Activity a, ArrayList<SmartCalendarEvent> events, int layoutResourceId,
			OnItemClickListener itemClickListener) {
		this(a,events,layoutResourceId,itemClickListener,"");
	}

	public synchronized void Reload(Context context, ArrayList<SmartCalendarEvent> events, String categoryFilter) {
		ArrayList<SmartCalendarEvent>  _events = new ArrayList<SmartCalendarEvent>();
		for (SmartCalendarEvent e : events) {
			if( categoryFilter == "" || categoryFilter.compareToIgnoreCase(e.getCategory()) == 0 )
				_events.add(e);
		}
		Collections.sort(_events);
		
        this.events_adapter = new EventsAdapter(context, layoutResourceId, _events);
        setAdapter(this.events_adapter);
	}	

	public synchronized void Reload(Context context, ArrayList<SmartCalendarEvent> events) {
		Reload(context,events,"");
	}

	private class EventsAdapter extends ArrayAdapter<SmartCalendarEvent> {

		private ArrayList<SmartCalendarEvent> items;

		public EventsAdapter(Context context, int layoutResourceId,
				ArrayList<SmartCalendarEvent> items) {
			super(context, layoutResourceId, items);
			this.items = items;
		}

		@Override
		public synchronized View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
            if(items == null || items.size() <= 0 ){
                return null;
            }

        	SmartCalendarEvent e = items.get(position);

            LayoutInflater vi = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(layoutResourceId, null);

            if (e != null) 
                v.setTag(e);{
                TextView t = (TextView) v.findViewById(R.id.eventName);
                t.setText(e.getName());    
				ImageView eventIcon = (ImageView) v.findViewById(R.id.eventIcon); 
				String iconId = e.getIconId();
				if (iconId == null) {
					globals.addRecordToLog("EventsList: iconId is null");
				}
				else{
					Integer iconId2 = globals.getIconId(activity, iconId);
					if (iconId2 == null) {					
						globals.addRecordToLog("EventsList: iconId2 is null");
					}
					else {
						Drawable drawable = getResources().getDrawable(iconId2);
						if (drawable == null) {
							globals.addRecordToLog("EventsList: drawable is null");
						}
						else {
							eventIcon.setImageDrawable(drawable);
						}
					}
				}
				if (layoutResourceId == R.layout.event_item_detailed) {
					t = (TextView) v.findViewById(R.id.eventDate);
					t.setText(e.getEventDate());

					t = (TextView) v.findViewById(R.id.eventTime);
					t.setText(DateFormat.format("kk:mm", e.getStartTime()).toString()	
							+ " - "	+ 
							DateFormat.format("kk:mm", e.getFinishTime()).toString());					
				}
            }

			return v;
		}
	}
	
}
