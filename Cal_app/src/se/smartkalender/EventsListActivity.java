package se.smartkalender;

import se.smartkalender.listviews.EventsListView;
import se.smartkalender.types.SmartCalendarEvent;

import se.smartkalender.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

public class EventsListActivity extends Activity {
	EventsListView eventsListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_list);
	    // find place for events list
	    LinearLayout ll = (LinearLayout) findViewById(R.id.placeForEventsList);
	    eventsListView = new EventsListView(this, EventsManager.getAllEvents(), R.layout.event_item_detailed, itemClickListener);
	    ll.addView(eventsListView);
	    
	    Button btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener( new View.OnClickListener() {
	        public void onClick(View v) {
	        	finish();		        	
	        }
	    });
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> a, View v, int position,
				long id) {
            Intent intent = new Intent(EventsListActivity.this, EventDetailsActivity.class);
            intent.putExtra("eventId", ((SmartCalendarEvent)v.getTag()).getId());
            startActivityForResult(intent, 0);			
		}

	};	

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		eventsListView.Reload(this, EventsManager.getAllEvents());
	}

}
