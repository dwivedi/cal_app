package se.smartkalender.dialogs;

import se.smartkalender.EventDetailsActivity;
import se.smartkalender.EventsManager;
import se.smartkalender.types.SmartCalendarEvent;

import se.smartkalender.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

public class EventFunctionsDialog extends Activity {
	private static final int RESULT_PLAN_BACKWARD = 0;
	private static final int RESULT_PLAN_FORWARD = 1;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.event_functions_dialog);
		getWindow().setLayout(LayoutParams.FILL_PARENT,	LayoutParams.WRAP_CONTENT);
		final int eventId = getIntent().getExtras().getInt("eventId");
        Button btnShowDetails = (Button) findViewById(R.id.btnShowDetails);
        btnShowDetails.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(EventFunctionsDialog.this, EventDetailsActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("readOnly", true);
                startActivityForResult(intent, 1);
            }
        });
        
        Button btnChangeEvent = (Button) findViewById(R.id.btnChangeEvent);
        btnChangeEvent.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(EventFunctionsDialog.this, EventDetailsActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("readOnly", false);
                startActivityForResult(intent, 0);
            }
        });
        
        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	EventsManager.deleteEvent(EventFunctionsDialog.this, eventId);
            	setResult(RESULT_OK);
            	finish();                
            }
        });
        
        Button planBackward = (Button) findViewById(R.id.planBackward);
        planBackward.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	SmartCalendarEvent event = new SmartCalendarEvent(EventsManager.getEvent(eventId));
            	Intent intent = new Intent(EventFunctionsDialog.this, TemplateCategoriesDialog.class);
            	intent.putExtra("planBackward", true);
            	intent.putExtra("startTime", event.getStartTime().getTime());
            	if (getIntent().getExtras().containsKey("selectedDay")) {
            		intent.putExtra("selectedDay", getIntent().getExtras().getLong("selectedDay"));
        		}
                startActivityForResult(intent, RESULT_PLAN_BACKWARD);
                finish();
            }
        });
        Button planForward = (Button) findViewById(R.id.planForward);
        planForward.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	SmartCalendarEvent event = new SmartCalendarEvent(EventsManager.getEvent(eventId));
            	Intent intent = new Intent(EventFunctionsDialog.this, TemplateCategoriesDialog.class);
            	intent.putExtra("planForward", true);            	
            	intent.putExtra("startTime", event.getFinishTime().getTime());
            	if (getIntent().getExtras().containsKey("selectedDay")) {
            		intent.putExtra("selectedDay", getIntent().getExtras().getLong("selectedDay"));
        		}
            	startActivityForResult(intent, RESULT_PLAN_FORWARD);            	
            	finish();
            }
        });
        
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(resultCode, data);
		finish();
	}

}
