package se.smartkalender.dialogs;

import se.smartkalender.EventDetailsActivity;
import se.smartkalender.EventsManager;
import se.smartkalender.R;
import se.smartkalender.globals;
import se.smartkalender.listviews.EventsListView;
import se.smartkalender.types.SmartCalendarEvent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class EventsTemplateDialog extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setLayout(LayoutParams.FILL_PARENT,	LayoutParams.WRAP_CONTENT);
			
		setContentView(R.layout.template_events_dialog);
	    // find place for events list
	    LinearLayout ll = (LinearLayout) findViewById(R.id.placeForEventsList);

        Bundle bundle = getIntent().getExtras();
	    String filterByCategory = "";
        if (bundle != null) {
        	if (bundle.containsKey("category")) {
	        	filterByCategory = bundle.getString("category");
		    	globals.addRecordToLog("category filter was received as " + filterByCategory );	        	
		    	TextView title = (TextView) findViewById(R.id.eventTitle);
		    	title.setText(filterByCategory);
        	}
        }
	    
	    EventsListView elv = new EventsListView(this, EventsManager.getTemplateEvents(), R.layout.event_item_template, itemClickListener, filterByCategory);
	    ll.addView(elv);	    
	}

	
	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> a, View v, int position,
				long id) {
            Intent intent = new Intent(EventsTemplateDialog.this, EventDetailsActivity.class);
            intent.putExtra("eventId", ((SmartCalendarEvent)v.getTag()).getId());
            if (getIntent().getExtras() != null) {
	    		if (getIntent().getExtras().getBoolean("planBackward")) {
	    			intent.putExtra("planBackward", true);    
	    			intent.putExtra("startTime", getIntent().getExtras().getLong("startTime"));    			    			
	    		}
	    		else if (getIntent().getExtras().getBoolean("planForward")) {
	    			intent.putExtra("planForward", true);    
	    			intent.putExtra("startTime", getIntent().getExtras().getLong("startTime"));    			
	    		}
	    		
	    		if (getIntent().getExtras().containsKey("selectedDay")) {
	    			intent.putExtra("selectedDay",getIntent().getExtras().getLong("selectedDay"));
	    		}
            }
            startActivityForResult(intent, 0);				
		}

	};	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(resultCode, data);
		finish();
	}

}
