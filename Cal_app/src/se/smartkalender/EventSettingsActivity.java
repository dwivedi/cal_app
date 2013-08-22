package se.smartkalender;

import se.smartkalender.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;



public class EventSettingsActivity extends Activity implements OnCheckedChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);		
		
		CheckBox timeShowHoursMinutes = (CheckBox) findViewById(R.id.timeShowHoursMinutes);   	
		timeShowHoursMinutes.setChecked(globals.timeShowHoursMinutes);
		timeShowHoursMinutes.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				globals.timeShowHoursMinutes = isChecked;				
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
 		
	}
	
}
