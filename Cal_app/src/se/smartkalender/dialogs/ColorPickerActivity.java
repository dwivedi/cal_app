package se.smartkalender.dialogs;

import se.smartkalender.widgets.ColorPickerView;
import android.app.Activity;
import android.os.Bundle;
import se.smartkalender.R;
import android.view.View;
import android.content.Intent;


public class ColorPickerActivity extends Activity implements View.OnClickListener {
	private ColorPickerView colorpicker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.color_picker_dialog);			
		Bundle b = getIntent().getExtras();
	    int chosenColor = b.getInt("color", 0);
	     
	    colorpicker = (ColorPickerView)findViewById(R.id.colorpickerview);
	    colorpicker.setColor(chosenColor);

	    View ok = findViewById(R.id.ok);
	    ok.setOnClickListener(this);
	     
	    View cancel = findViewById(R.id.cancel);
	    cancel.setOnClickListener(this);
	
	}

    @Override
    public void onClick(View v) {
	    Intent resultIntent = new Intent();
	    switch (v.getId()) {
		    case R.id.ok:
			    int chosenColor = colorpicker.getColor();
			     
			    // Save the result from this activity
			    Bundle b = new Bundle();
			    b.putInt("color", chosenColor);
			    resultIntent.putExtras(b);
			    setResult(Activity.RESULT_OK, resultIntent);
			    finish();
		    break;
		     
		    case R.id.cancel:
			    setResult(Activity.RESULT_CANCELED, resultIntent);
			    finish();
		    break;
	    }
    }

	
}
